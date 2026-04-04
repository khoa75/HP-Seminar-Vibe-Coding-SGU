"""Portfolio service for trading and position management."""

import sqlite3
from datetime import datetime
from app.database import get_db
from app.models import Position, Portfolio, Trade


class PortfolioService:
    """Service for managing portfolio operations."""

    @staticmethod
    def get_portfolio(market_prices: dict[str, float]) -> Portfolio:
        """Get current portfolio state with current prices."""
        conn = get_db()
        cursor = conn.cursor()
        
        # Get cash
        cursor.execute("SELECT cash FROM portfolio LIMIT 1")
        result = cursor.fetchone()
        cash = float(result[0]) if result else 10000.0
        
        # Get positions
        cursor.execute("SELECT ticker, quantity, average_cost, created_at FROM positions")
        positions = {}
        for row in cursor.fetchall():
            ticker = row[0]
            quantity = row[1]
            average_cost = row[2]
            created_at = datetime.fromisoformat(row[3])
            current_price = market_prices.get(ticker, average_cost)
            
            positions[ticker] = Position(
                ticker=ticker,
                quantity=quantity,
                average_cost=average_cost,
                current_price=current_price,
                created_at=created_at
            )
        
        # Get portfolio value history
        cursor.execute("""
            SELECT value, timestamp FROM portfolio_value_history
            ORDER BY timestamp DESC LIMIT 100
        """)
        portfolio_value_history = [
            {
                "value": float(row[0]),
                "timestamp": row[1]
            }
            for row in cursor.fetchall()
        ]
        portfolio_value_history.reverse()
        
        conn.close()
        
        portfolio = Portfolio(
            cash=cash,
            positions=positions,
            portfolio_value_history=portfolio_value_history
        )
        
        return portfolio

    @staticmethod
    def execute_trade(
        ticker: str,
        side: str,
        quantity: int,
        price: float
    ) -> dict:
        """Execute a market order (buy or sell)."""
        if quantity <= 0:
            return {"error": "Quantity must be positive"}
        
        conn = get_db()
        cursor = conn.cursor()
        
        # Get current cash
        cursor.execute("SELECT cash FROM portfolio LIMIT 1")
        result = cursor.fetchone()
        cash = float(result[0]) if result else 10000.0
        
        if side == "buy":
            cost = quantity * price
            if cash < cost:
                conn.close()
                return {
                    "error": f"Insufficient cash. Need ${cost:.2f}, have ${cash:.2f}"
                }
            
            # Get existing position
            cursor.execute(
                "SELECT quantity, average_cost FROM positions WHERE ticker = ?",
                (ticker,)
            )
            existing = cursor.fetchone()
            
            if existing:
                old_qty, old_cost = existing[0], existing[1]
                new_qty = old_qty + quantity
                new_cost = (old_qty * old_cost + quantity * price) / new_qty
                cursor.execute(
                    "UPDATE positions SET quantity = ?, average_cost = ? WHERE ticker = ?",
                    (new_qty, new_cost, ticker)
                )
            else:
                cursor.execute(
                    "INSERT INTO positions (ticker, quantity, average_cost) VALUES (?, ?, ?)",
                    (ticker, quantity, price)
                )
            
            # Deduct cash
            cursor.execute(
                "UPDATE portfolio SET cash = cash - ? WHERE id = 1",
                (cost,)
            )
        
        elif side == "sell":
            # Get existing position
            cursor.execute(
                "SELECT quantity FROM positions WHERE ticker = ?",
                (ticker,)
            )
            result = cursor.fetchone()
            
            if not result or result[0] < quantity:
                conn.close()
                held = result[0] if result else 0
                return {
                    "error": f"Cannot sell {quantity} shares of {ticker}. Only have {held}."
                }
            
            proceeds = quantity * price
            
            # Update or delete position
            new_qty = result[0] - quantity
            if new_qty > 0:
                cursor.execute(
                    "UPDATE positions SET quantity = ? WHERE ticker = ?",
                    (new_qty, ticker)
                )
            else:
                cursor.execute("DELETE FROM positions WHERE ticker = ?", (ticker,))
            
            # Add cash
            cursor.execute(
                "UPDATE portfolio SET cash = cash + ? WHERE id = 1",
                (proceeds,)
            )
        
        else:
            conn.close()
            return {"error": f"Invalid side: {side}"}
        
        # Record trade
        cursor.execute(
            "INSERT INTO trades (ticker, side, quantity, price) VALUES (?, ?, ?, ?)",
            (ticker, side, quantity, price)
        )
        
        conn.commit()
        conn.close()
        
        return {
            "success": True,
            "ticker": ticker,
            "side": side,
            "quantity": quantity,
            "price": price,
            "timestamp": datetime.now().isoformat()
        }

    @staticmethod
    def get_trades(limit: int = 50) -> list[Trade]:
        """Get recent trades."""
        conn = get_db()
        cursor = conn.cursor()
        
        cursor.execute("""
            SELECT ticker, side, quantity, price, timestamp
            FROM trades
            ORDER BY timestamp DESC
            LIMIT ?
        """, (limit,))
        
        trades = []
        for row in cursor.fetchall():
            trades.append(Trade(
                ticker=row[0],
                side=row[1],
                quantity=row[2],
                price=row[3],
                timestamp=datetime.fromisoformat(row[4])
            ))
        
        conn.close()
        return trades
