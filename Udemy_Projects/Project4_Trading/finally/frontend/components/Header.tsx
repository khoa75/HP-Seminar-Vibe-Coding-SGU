import React from "react";

interface HeaderProps {
  connectionStatus: boolean;
}

export default function Header({ connectionStatus }: HeaderProps) {
  return (
    <header className="border-b border-[#30363d] bg-[#0d1117] sticky top-0 z-50">
      <div className="px-4 py-3 flex items-center justify-between max-w-7xl mx-auto">
        <div className="flex items-center gap-4">
          <h1 className="text-2xl font-bold">
            <span className="text-[#ecad0a]">Fin</span>
            <span className="text-[#209dd7]">Ally</span>
          </h1>
          <p className="text-sm text-[#8b949e]">AI Trading Workstation</p>
        </div>

        <div className="flex items-center gap-3">
          <div className="flex items-center gap-2">
            <div
              className={`w-2 h-2 rounded-full ${
                connectionStatus ? "bg-[#34a853]" : "bg-[#f85149]"
              }`}
            ></div>
            <span className="text-xs text-[#8b949e]">
              {connectionStatus ? "Connected" : "Disconnected"}
            </span>
          </div>
        </div>
      </div>
    </header>
  );
}
