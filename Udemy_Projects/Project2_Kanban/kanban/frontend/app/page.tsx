import { KanbanBoardLoader } from "@/components/KanbanBoardLoader";

export default function Home() {
  return (
    <div className="flex min-h-full flex-1 flex-col">
      <header className="border-b border-[var(--color-primary)]/20 bg-white px-6 py-5 shadow-sm">
        <h1 className="text-2xl font-bold tracking-tight text-[var(--color-navy)]">
          Project board
        </h1>
        <p className="mt-1 text-sm text-[var(--color-gray)]">
          Drag cards between columns. Click a column title to rename it.
        </p>
      </header>
      <main className="flex-1 px-6 py-6">
        <KanbanBoardLoader />
      </main>
    </div>
  );
}
