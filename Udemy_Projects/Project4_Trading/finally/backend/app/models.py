"""Data models for FinAlly backend."""

from dataclasses import dataclass
from datetime import datetime
from decimal import Decimal


@dataclass
class Position:
    """Represents a trading position."""
    ticker: str
    quantity: int
    average_cost: float
    current_price: float
    created_at: datetime

    @property
    def current_value(self) -> float:
        return self.quantity * self.current_price

    @property
    def cost_basis(self) -> float:
        return self.quantity * self.average_cost

    @property
    def unrealized_pnl(self) -> float:
        return self.current_value - self.cost_basis

    @property
    def unrealized_pnl_percent(self) -> float:
        if self.cost_basis == 0:
            return 0.0
        return (self.unrealized_pnl / self.cost_basis) * 100

    def to_dict(self) -> dict:
        return {
            "ticker": self.ticker,
            "quantity": self.quantity,
            "average_cost": round(self.average_cost, 2),
            "current_price": round(self.current_price, 2),
            "current_value": round(self.current_value, 2),
            "cost_basis": round(self.cost_basis, 2),
            "unrealized_pnl": round(self.unrealized_pnl, 2),
            "unrealized_pnl_percent": round(self.unrealized_pnl_percent, 2),
            "created_at": self.created_at.isoformat(),
        }


@dataclass
class Portfolio:
    """Represents a user's portfolio."""
    cash: float
    positions: dict[str, Position]
    portfolio_value_history: list[dict]  # [{timestamp, value}]

    @property
    def total_value(self) -> float:
        positions_value = sum(p.current_value for p in self.positions.values())
        return self.cash + positions_value

    @property
    def total_pnl(self) -> float:
        total_cost_basis = sum(p.cost_basis for p in self.positions.values())
        return self.total_value - (total_cost_basis + self.cash)

    @property
    def total_pnl_percent(self) -> float:
        initial_value = sum(p.cost_basis for p in self.positions.values()) + self.cash
        if initial_value == 0:
            return 0.0
        return (self.total_pnl / initial_value) * 100

    def to_dict(self) -> dict:
        return {
            "cash": round(self.cash, 2),
            "positions": {ticker: pos.to_dict() for ticker, pos in self.positions.items()},
            "total_value": round(self.total_value, 2),
            "total_pnl": round(self.total_pnl, 2),
            "total_pnl_percent": round(self.total_pnl_percent, 2),
            "portfolio_value_history": self.portfolio_value_history,
        }


@dataclass
class Trade:
    """Represents a completed trade."""
    ticker: str
    side: str  # "buy" or "sell"
    quantity: int
    price: float
    timestamp: datetime

    def to_dict(self) -> dict:
        return {
            "ticker": self.ticker,
            "side": self.side,
            "quantity": self.quantity,
            "price": round(self.price, 2),
            "timestamp": self.timestamp.isoformat(),
        }


@dataclass
class ChatMessage:
    """Represents a chat message."""
    role: str  # "user" or "assistant"
    content: str
    timestamp: datetime

    def to_dict(self) -> dict:
        return {
            "role": self.role,
            "content": self.content,
            "timestamp": self.timestamp.isoformat(),
        }
