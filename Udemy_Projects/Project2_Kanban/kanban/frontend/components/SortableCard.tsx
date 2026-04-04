"use client";

import { useSortable } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import type { Card } from "@/lib/types";

type Props = {
  card: Card;
  columnId: string;
  onDelete: (columnId: string, cardId: string) => void;
};

export function SortableCard({ card, columnId, onDelete }: Props) {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } =
    useSortable({ id: card.id, data: { type: "card", columnId } });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      className={`rounded-lg border border-[var(--color-accent)]/25 bg-white p-3 shadow-sm transition-shadow hover:shadow-md ${
        isDragging ? "opacity-60 ring-2 ring-[var(--color-primary)]" : ""
      }`}
    >
      <div className="flex gap-2">
        <button
          type="button"
          className="mt-0.5 shrink-0 cursor-grab touch-none text-[var(--color-gray)] active:cursor-grabbing"
          aria-label="Drag card"
          {...listeners}
          {...attributes}
        >
          <GripIcon />
        </button>
        <div className="min-w-0 flex-1">
          <h3 className="font-semibold text-[var(--color-navy)]">{card.title}</h3>
          <p className="mt-1 text-sm leading-relaxed text-[var(--color-gray)]">
            {card.details}
          </p>
        </div>
        <button
          type="button"
          data-testid={`delete-card-${card.id}`}
          onPointerDown={(e) => e.stopPropagation()}
          onClick={() => onDelete(columnId, card.id)}
          className="shrink-0 self-start rounded px-2 py-1 text-sm text-[var(--color-primary)] underline-offset-2 hover:underline"
          aria-label={`Delete ${card.title}`}
        >
          Remove
        </button>
      </div>
    </div>
  );
}

function GripIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 16 16" aria-hidden fill="currentColor">
      <circle cx="6" cy="4" r="1.25" />
      <circle cx="10" cy="4" r="1.25" />
      <circle cx="6" cy="8" r="1.25" />
      <circle cx="10" cy="8" r="1.25" />
      <circle cx="6" cy="12" r="1.25" />
      <circle cx="10" cy="12" r="1.25" />
    </svg>
  );
}
