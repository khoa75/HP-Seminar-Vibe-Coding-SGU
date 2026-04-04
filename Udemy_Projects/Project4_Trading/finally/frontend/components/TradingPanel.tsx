"use client";

import React, { useState } from "react";

interface TradingPanelProps {
  prices: Record<string, number>;
}

export default function TradingPanel({ prices }: TradingPanelProps) {
  const [ticker, setTicker] = useState("");
  const [quantity, setQuantity] = useState(1);
  const [side, setSide] = useState<"buy" | "sell">("buy");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const price = ticker ? prices[ticker] || 0 : 0;
  const total = price * quantity;

  const handleTrade = async () => {
    if (!ticker || quantity <= 0) {
      setMessage("Invalid input");
      return;
    }

    setLoading(true);
    setMessage("");

    try {
      const response = await fetch(
        `/api/trade?ticker=${ticker}&side=${side}&quantity=${quantity}`,
        { method: "POST" }
      );

      const data = await response.json();

      if (!response.ok) {
        setMessage(`Error: ${data.detail || "Trade failed"}`);
      } else {
        setMessage(`${side.toUpperCase()} ${quantity} ${ticker} @ $${price.toFixed(2)}`);
        setTicker("");
        setQuantity(1);

        setTimeout(() => setMessage(""), 3000);
      }
    } catch (error) {
      setMessage("Connection error");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-4">
      <h2 className="text-lg font-semibold mb-4">Quick Trade</h2>

      <div className="space-y-3">
        <div>
          <label className="text-xs text-[#8b949e] block mb-1">Ticker</label>
          <input
            type="text"
            value={ticker}
            onChange={(e) => setTicker(e.target.value.toUpperCase())}
            placeholder="e.g., AAPL"
            className="w-full bg-[#0d1117] border border-[#30363d] rounded px-3 py-2 text-[#c9d1d9] placeholder-[#6e7681] focus:outline-none focus:border-[#58a6ff]"
          />
        </div>

        <div className="grid grid-cols-2 gap-2">
          <div>
            <label className="text-xs text-[#8b949e] block mb-1">Qty</label>
            <input
              type="number"
              value={quantity}
              onChange={(e) => setQuantity(Math.max(1, parseInt(e.target.value) || 0))}
              min="1"
              className="w-full bg-[#0d1117] border border-[#30363d] rounded px-3 py-2 text-[#c9d1d9] focus:outline-none focus:border-[#58a6ff]"
            />
          </div>

          <div>
            <label className="text-xs text-[#8b949e] block mb-1">Price</label>
            <div className="bg-[#0d1117] border border-[#30363d] rounded px-3 py-2 text-[#c9d1d9] text-sm">
              ${price.toFixed(2)}
            </div>
          </div>
        </div>

        <div className="text-sm text-[#8b949e]">
          Total: <span className="text-[#c9d1d9] font-semibold">${total.toFixed(2)}</span>
        </div>

        <div className="grid grid-cols-2 gap-2">
          <button
            onClick={() => setSide("buy")}
            className={`py-2 px-3 rounded font-semibold text-sm transition ${
              side === "buy"
                ? "bg-[#34a853] text-white"
                : "bg-[#0d1117] border border-[#30363d] text-[#c9d1d9] hover:border-[#34a853]"
            }`}
          >
            Buy
          </button>

          <button
            onClick={() => setSide("sell")}
            className={`py-2 px-3 rounded font-semibold text-sm transition ${
              side === "sell"
                ? "bg-[#f85149] text-white"
                : "bg-[#0d1117] border border-[#30363d] text-[#c9d1d9] hover:border-[#f85149]"
            }`}
          >
            Sell
          </button>
        </div>

        <button
          onClick={handleTrade}
          disabled={loading || !ticker}
          className="w-full bg-[#753991] text-white py-2 rounded font-semibold hover:opacity-90 disabled:opacity-50 transition"
        >
          {loading ? "..." : `${side.toUpperCase()} ${ticker}`}
        </button>

        {message && (
          <div className={`text-xs p-2 rounded text-center ${
            message.startsWith("Error")
              ? "bg-[#f8504912] text-[#f85149]"
              : "bg-[#34a85312] text-[#34a853]"
          }`}>
            {message}
          </div>
        )}
      </div>
    </div>
  );
}
