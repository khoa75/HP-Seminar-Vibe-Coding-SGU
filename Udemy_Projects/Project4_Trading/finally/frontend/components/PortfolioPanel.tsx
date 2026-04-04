"use client";

import React, { useEffect, useState } from "react";

interface Portfolio {
  cash: number;
  total_value: number;
  total_pnl: number;
  total_pnl_percent: number;
}

interface PortfolioPanelProps {
  prices: Record<string, number>;
}

export default function PortfolioPanel({ prices }: PortfolioPanelProps) {
  const [portfolio, setPortfolio] = useState<Portfolio | null>(null);

  useEffect(() => {
    const fetchPortfolio = () => {
      fetch("/api/portfolio")
        .then((res) => res.json())
        .then(setPortfolio)
        .catch(console.error);
    };

    fetchPortfolio();
    const interval = setInterval(fetchPortfolio, 2000);
    return () => clearInterval(interval);
  }, [prices]);

  if (!portfolio) {
    return (
      <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-4 animate-pulse">
        <div className="h-32 bg-[#0d1117] rounded"></div>
      </div>
    );
  }

  const isProfitable = portfolio.total_pnl >= 0;

  return (
    <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-4">
      <h2 className="text-lg font-semibold mb-4">Portfolio</h2>

      <div className="space-y-3">
        <div className="flex justify-between text-sm">
          <span className="text-[#8b949e]">Total Value</span>
          <span className="text-[#c9d1d9] font-semibold">${portfolio.total_value.toFixed(2)}</span>
        </div>

        <div className="flex justify-between text-sm">
          <span className="text-[#8b949e]">Cash</span>
          <span className="text-[#c9d1d9] font-semibold">${portfolio.cash.toFixed(2)}</span>
        </div>

        <div className="border-t border-[#30363d] pt-3">
          <div className="flex justify-between text-sm">
            <span className="text-[#8b949e]">P&L</span>
            <span className={`font-semibold ${isProfitable ? "text-[#34a853]" : "text-[#f85149]"}`}>
              ${portfolio.total_pnl.toFixed(2)} ({portfolio.total_pnl_percent.toFixed(2)}%)
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}
