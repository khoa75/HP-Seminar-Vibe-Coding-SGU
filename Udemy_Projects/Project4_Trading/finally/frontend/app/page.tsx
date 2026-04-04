"use client";

import { useEffect, useState } from "react";
import Header from "@/components/Header";
import WatchlistGrid from "@/components/WatchlistGrid";
import PortfolioPanel from "@/components/PortfolioPanel";
import TradingPanel from "@/components/TradingPanel";

interface PriceUpdate {
  ticker: string;
  price: number;
  timestamp: string;
}

export default function Home() {
  const [prices, setPrices] = useState<Record<string, number>>({});
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    // Connect to SSE stream
    const eventSource = new EventSource("/api/stream");

    eventSource.onopen = () => {
      setConnected(true);
    };

    eventSource.onmessage = (event) => {
      try {
        const data: PriceUpdate = JSON.parse(event.data);
        setPrices((prev) => ({ ...prev, [data.ticker]: data.price }));
      } catch (error) {
        console.error("Error parsing SSE data:", error);
      }
    };

    eventSource.onerror = () => {
      setConnected(false);
      eventSource.close();
      // Reconnect after 3 seconds
      setTimeout(() => {
        window.location.reload();
      }, 3000);
    };

    return () => {
      eventSource.close();
    };
  }, []);

  return (
    <div className="min-h-screen bg-[#0d1117]">
      <Header connectionStatus={connected} />

      <main className="grid grid-cols-1 lg:grid-cols-3 gap-4 p-4 max-w-7xl mx-auto">
        {/* Left Panel - Watchlist */}
        <div className="lg:col-span-2">
          <WatchlistGrid prices={prices} />
        </div>

        {/* Right Panel - Trading & Portfolio */}
        <div className="space-y-4">
          <PortfolioPanel prices={prices} />
          <TradingPanel prices={prices} />
        </div>
      </main>
    </div>
  );
}
