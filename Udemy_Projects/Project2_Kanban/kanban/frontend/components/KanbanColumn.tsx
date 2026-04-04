"use client";

import type { FormEvent } from "react";
import { useDroppable } from "@dnd-kit/core";
import { SortableContext, verticalListSortingStrategy } from "@dnd-kit/sortable";
import type { Column } from "@/lib/types";
import { SortableCard } from "./SortableCard";

type Props = {
  column: Column;
  titleInput: string;
  isEditingTitle: boolean;
  onStartEditTitle: () => void;
  onTitleChange: (value: string) => void;
  onCommitTitle: () => void;
  onCancelTitle: () => void;
  showAddForm: boolean;
  onToggleAddForm: () => void;
  newTitle: string;
  newDetails: string;
  onNewTitleChange: (v: string) => void;
  onNewDetailsChange: (v: string) => void;
  onSubmitNewCard: (e: FormEvent) => void;
  onDeleteCard: (columnId: string, cardId: string) => void;
};

export function KanbanColumn({
  column,
  titleInput,
  isEditingTitle,
  onStartEditTitle,
  onTitleChange,
  onCommitTitle,
  onCancelTitle,
  showAddForm,
  onToggleAddForm,
  newTitle,
  newDetails,
  onNewTitleChange,
  onNewDetailsChange,
  onSubmitNewCard,
  onDeleteCard,
}: Props) {
  const { setNodeRef, isOver } = useDroppable({
    id: column.id,
    data: { type: "column" },
  });

  const cardIds = column.cards.map((c) => c.id);

  return (
    <section
      data-testid={`column-${column.id}`}
      className={`flex w-72 shrink-0 flex-col rounded-xl border-2 bg-[#f8fafc] ${
        isOver
          ? "border-[var(--color-accent)] shadow-md"
          : "border-[var(--color-primary)]/20"
      }`}
    >
      <header className="border-b border-[var(--color-primary)]/15 px-3 py-2">
        {isEditingTitle ? (
          <input
            autoFocus
            className="w-full rounded border border-[var(--color-primary)]/40 px-2 py-1 text-sm font-semibold text-[var(--color-navy)] outline-none focus:ring-2 focus:ring-[var(--color-primary)]"
            value={titleInput}
            onChange={(e) => onTitleChange(e.target.value)}
            onBlur={onCommitTitle}
            onKeyDown={(e) => {
              if (e.key === "Enter") onCommitTitle();
              if (e.key === "Escape") onCancelTitle();
            }}
            aria-label="Column title"
          />
        ) : (
          <button
            type="button"
            onClick={onStartEditTitle}
            className="w-full text-left text-base font-semibold text-[var(--color-navy)] hover:text-[var(--color-primary)]"
          >
            {column.title}
          </button>
        )}
      </header>

      <div ref={setNodeRef} className="flex min-h-[120px] flex-1 flex-col gap-2 p-2">
        <SortableContext items={cardIds} strategy={verticalListSortingStrategy}>
          {column.cards.map((card) => (
            <SortableCard
              key={card.id}
              card={card}
              columnId={column.id}
              onDelete={onDeleteCard}
            />
          ))}
        </SortableContext>
      </div>

      <footer className="border-t border-[var(--color-primary)]/10 p-2">
        {showAddForm ? (
          <form onSubmit={onSubmitNewCard} className="flex flex-col gap-2">
            <input
              data-testid="new-card-title"
              placeholder="Title"
              aria-label="Card title"
              required
              className="rounded border border-gray-200 px-2 py-1.5 text-sm text-[var(--color-navy)] outline-none focus:border-[var(--color-primary)]"
              value={newTitle}
              onChange={(e) => onNewTitleChange(e.target.value)}
            />
            <textarea
              data-testid="new-card-details"
              placeholder="Details"
              aria-label="Card details"
              required
              rows={2}
              className="resize-none rounded border border-gray-200 px-2 py-1.5 text-sm text-[var(--color-navy)] outline-none focus:border-[var(--color-primary)]"
              value={newDetails}
              onChange={(e) => onNewDetailsChange(e.target.value)}
            />
            <div className="flex gap-2">
              <button
                type="submit"
                className="rounded-md bg-[var(--color-secondary)] px-3 py-1.5 text-sm font-medium text-white hover:opacity-95"
              >
                Add card
              </button>
              <button
                type="button"
                onClick={onToggleAddForm}
                className="rounded-md border border-gray-200 px-3 py-1.5 text-sm text-[var(--color-gray)] hover:bg-gray-50"
              >
                Cancel
              </button>
            </div>
          </form>
        ) : (
          <button
            type="button"
            data-testid={`add-card-${column.id}`}
            onClick={onToggleAddForm}
            className="w-full rounded-md border border-dashed border-[var(--color-primary)]/35 py-2 text-sm font-medium text-[var(--color-primary)] hover:bg-[var(--color-primary)]/5"
          >
            Add card
          </button>
        )}
      </footer>
    </section>
  );
}
