"""FinAlly Backend - AI Trading Workstation API."""

import asyncio
import sqlite3
import json
from contextlib import asynccontextmanager
from datetime import datetime
from typing import AsyncGenerator

from fastapi import FastAPI, HTTPException, Query, BackgroundTasks
from fastapi.staticfiles import StaticFiles
from fastapi.responses import FileResponse, StreamingResponse
from fastapi.middleware.cors import CORSMiddleware
from pathlib import Path

from app.database import init_db, get_db
from app.market.factory import create_market_data_source
from app.market.cache import PriceCache
from app.portfolio_service import PortfolioService

# Initialize database
init_db()

# Global state
price_cache = PriceCache()
market_data_source = None
market_data_task = None


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Manage application lifecycle."""
    global market_data_source, market_data_task
    
    # Startup
    market_data_source = create_market_data_source(price_cache)
    await market_data_source.start(
        ["AAPL", "MSFT", "GOOGL", "AMZN", "NVDA", "TSLA", "META", "NFLX", "ADBE", "PYPL"]
    )
    
    # Run market data update in background
    async def market_update_loop():
        while True:
            try:
                await asyncio.sleep(0.5)
            except asyncio.CancelledError:
                break
    
    market_data_task = asyncio.create_task(market_update_loop())
    
    yield
    
    # Shutdown
    if market_data_task:
        market_data_task.cancel()
    await market_data_source.stop()


app = FastAPI(
    title="FinAlly API",
    description="AI-powered trading workstation backend",
    version="0.1.0",
    lifespan=lifespan,
)

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ==================== API Routes ====================

@app.get("/api/health")
async def health():
    """Health check endpoint."""
    return {"status": "healthy"}


@app.get("/api/watchlist")
async def get_watchlist():
    """Get current watchlist."""
    conn = get_db()
    cursor = conn.cursor()
    
    cursor.execute("SELECT ticker FROM watchlist ORDER BY created_at")
    tickers = [row[0] for row in cursor.fetchall()]
    
    conn.close()
    
    prices = {ticker: price_cache.get_price(ticker) for ticker in tickers}
    
    return {
        "tickers": tickers,
        "prices": prices
    }


@app.post("/api/watchlist/add")
async def add_to_watchlist(ticker: str = Query(...)):
    """Add ticker to watchlist."""
    conn = get_db()
    cursor = conn.cursor()
    
    try:
        cursor.execute("INSERT INTO watchlist (ticker) VALUES (?)", (ticker.upper(),))
        conn.commit()
        conn.close()
        return {"success": True, "ticker": ticker.upper()}
    except sqlite3.IntegrityError:
        conn.close()
        return {"success": False, "error": "Ticker already in watchlist"}


@app.delete("/api/watchlist/remove")
async def remove_from_watchlist(ticker: str = Query(...)):
    """Remove ticker from watchlist."""
    conn = get_db()
    cursor = conn.cursor()
    
    cursor.execute("DELETE FROM watchlist WHERE ticker = ?", (ticker.upper(),))
    conn.commit()
    conn.close()
    
    return {"success": True, "ticker": ticker.upper()}


@app.get("/api/portfolio")
async def get_portfolio():
    """Get current portfolio state."""
    prices = price_cache.get_all_prices()
    portfolio = PortfolioService.get_portfolio(prices)
    return portfolio.to_dict()


@app.post("/api/trade")
async def execute_trade(
    ticker: str = Query(...),
    side: str = Query(...),
    quantity: int = Query(...),
):
    """Execute a market order."""
    ticker = ticker.upper()
    side = side.lower()
    
    if side not in ["buy", "sell"]:
        raise HTTPException(status_code=400, detail="Side must be 'buy' or 'sell'")
    
    price = price_cache.get_price(ticker)
    if price is None:
        raise HTTPException(status_code=400, detail=f"No price data for {ticker}")
    
    result = PortfolioService.execute_trade(ticker, side, quantity, price)
    
    if "error" in result:
        raise HTTPException(status_code=400, detail=result["error"])
    
    return result


@app.get("/api/trades")
async def get_trades(limit: int = Query(50, ge=1, le=500)):
    """Get recent trades."""
    trades = PortfolioService.get_trades(limit)
    return {"trades": [t.to_dict() for t in trades]}


@app.get("/api/prices")
async def get_prices():
    """Get current prices for all tracked tickers."""
    return price_cache.get_all_prices()


@app.get("/api/stream")
async def stream_prices() -> StreamingResponse:
    """Stream live price updates via SSE."""
    async def event_generator() -> AsyncGenerator[str, None]:
        previous_prices = {}
        
        while True:
            try:
                # Get current prices
                current_prices = price_cache.get_all_prices()
                
                # Send only changed prices
                for ticker, price in current_prices.items():
                    if ticker not in previous_prices or abs(
                        previous_prices[ticker] - price
                    ) > 0.001:  # Only send if price changed
                        event_data = json.dumps({
                            "ticker": ticker,
                            "price": round(price, 2),
                            "timestamp": datetime.now().isoformat()
                        })
                        yield f"data: {event_data}\n\n"
                
                previous_prices = current_prices.copy()
                
                await asyncio.sleep(0.2)  # Update every 200ms
            except Exception as e:
                print(f"Error in stream: {e}")
                break
    
    return StreamingResponse(
        event_generator(),
        media_type="text/event-stream",
        headers={
            "Cache-Control": "no-cache",
            "X-Accel-Buffering": "no",
            "Connection": "keep-alive",
        }
    )


# ==================== Static Files ====================

FRONTEND_OUT = Path(__file__).parent.parent.parent / "frontend" / "out"

if FRONTEND_OUT.exists():
    app.mount("/", StaticFiles(directory=FRONTEND_OUT, html=True), name="frontend")


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
