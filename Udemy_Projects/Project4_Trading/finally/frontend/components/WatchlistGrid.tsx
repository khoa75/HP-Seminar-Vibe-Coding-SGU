"use client";

import React, { useEffect, useState } from "react";

interface WatchlistItem {
  ticker: string;
  price: number;
}

interface WatchlistGridProps {
  prices: Record<string, number>;
}

export default function WatchlistGrid({ prices }: WatchlistGridProps) {
  const [watchlist, setWatchlist] = useState<string[]>([]);
  const [previousPrices, setPreviousPrices] = useState<Record<string, number>>({});

  useEffect(() => {
    fetch("/api/watchlist")
      .then((res) => res.json())
      .then((data) => setWatchlist(data.tickers))
      .catch(console.error);
  }, []);

  const items: WatchlistItem[] = watchlist.map((ticker) => ({
    ticker,
    price: prices[ticker] || 0,
  }));

  useEffect(() => {
    setPreviousPrices(prices);
  }, [prices]);

  const getPriceChange = (ticker: string) => {
    const current = prices[ticker];
    const previous = previousPrices[ticker];

    if (!current || !previous) return null;

    const change = current - previous;
    return change >= 0 ? "up" : "down";
  };

  return (
    <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-4">
      <h2 className="text-lg font-semibold mb-4">Watchlist</h2>

      <div className="grid grid-cols-2 sm:grid-cols-3 gap-2">
        {items.map((item) => {
          const direction = getPriceChange(item.ticker);
          const flashClass =
            direction === "up" ? "flash-green" : direction === "down" ? "flash-red" : "";

          return (
            <div
              key={item.ticker}
              className={`bg-[#0d1117] border border-[#30363d] rounded p-3 cursor-pointer hover:border-[#58a6ff] transition ${flashClass}`}
            >
              <div className="text-sm font-semibold">{item.ticker}</div>
              <div className={`text-lg font-bold ${
                direction === "up" ? "text-[#34a853]" : direction === "down" ? "text-[#f85149]" : "text-[#c9d1d9]"
              }`}>
                ${item.price.toFixed(2)}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
