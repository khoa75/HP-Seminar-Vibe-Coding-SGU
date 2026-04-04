"use client";

import dynamic from "next/dynamic";

const KanbanBoard = dynamic(
  () =>
    import("@/components/KanbanBoard").then((mod) => ({ default: mod.KanbanBoard })),
  {
    ssr: false,
    loading: () => (
      <p className="px-2 py-8 text-sm text-[var(--color-gray)]">Loading board…</p>
    ),
  },
);

export function KanbanBoardLoader() {
  return <KanbanBoard />;
}
