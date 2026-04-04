"use client";

import {
  DndContext,
  DragOverlay,
  PointerSensor,
  closestCorners,
  useSensor,
  useSensors,
  type DragEndEvent,
  type DragStartEvent,
} from "@dnd-kit/core";
import { useReducer, useState, type FormEvent } from "react";
import { boardReducer, moveCardAfterDrag } from "@/lib/board-reducer";
import { initialBoardState } from "@/lib/dummy-data";
import type { Card } from "@/lib/types";
import { KanbanColumn } from "./KanbanColumn";

export function KanbanBoard() {
  const [state, dispatch] = useReducer(boardReducer, initialBoardState);
  const [activeCard, setActiveCard] = useState<Card | null>(null);
  const [titleColumnId, setTitleColumnId] = useState<string | null>(null);
  const [titleDraft, setTitleDraft] = useState("");
  const [addFormColumnId, setAddFormColumnId] = useState<string | null>(null);
  const [newTitle, setNewTitle] = useState("");
  const [newDetails, setNewDetails] = useState("");

  const sensors = useSensors(
    useSensor(PointerSensor, { activationConstraint: { distance: 8 } }),
  );

  function handleDragStart(event: DragStartEvent) {
    const id = String(event.active.id);
    for (const col of state.columns) {
      const c = col.cards.find((x) => x.id === id);
      if (c) {
        setActiveCard(c);
        return;
      }
    }
  }

  function handleDragEnd(event: DragEndEvent) {
    setActiveCard(null);
    const { active, over } = event;
    if (!over) return;
    const activeId = String(active.id);
    const overId = String(over.id);
    const next = moveCardAfterDrag(state, activeId, overId);
    if (next !== state) {
      dispatch({ type: "SET_COLUMNS", columns: next.columns });
    }
  }

  function handleDragCancel() {
    setActiveCard(null);
  }

  function startEditTitle(columnId: string, current: string) {
    setTitleColumnId(columnId);
    setTitleDraft(current);
  }

  function commitTitle() {
    if (titleColumnId && titleDraft.trim()) {
      dispatch({
        type: "RENAME_COLUMN",
        columnId: titleColumnId,
        title: titleDraft.trim(),
      });
    }
    setTitleColumnId(null);
  }

  function cancelTitle() {
    setTitleColumnId(null);
  }

  function toggleAddForm(columnId: string) {
    setAddFormColumnId((prev) => (prev === columnId ? null : columnId));
    setNewTitle("");
    setNewDetails("");
  }

  function submitNewCard(columnId: string, e: FormEvent) {
    e.preventDefault();
    if (!newTitle.trim() || !newDetails.trim()) return;
    const card: Card = {
      id: crypto.randomUUID(),
      title: newTitle.trim(),
      details: newDetails.trim(),
    };
    dispatch({
      type: "ADD_CARD",
      columnId,
      card,
    });
    setNewTitle("");
    setNewDetails("");
    setAddFormColumnId(null);
  }

  function deleteCard(columnId: string, cardId: string) {
    dispatch({ type: "DELETE_CARD", columnId, cardId });
  }

  return (
    <DndContext
      sensors={sensors}
      collisionDetection={closestCorners}
      onDragStart={handleDragStart}
      onDragEnd={handleDragEnd}
      onDragCancel={handleDragCancel}
    >
      <div
        data-testid="kanban-board"
        className="flex gap-4 overflow-x-auto pb-4"
      >
        {state.columns.map((col) => (
          <KanbanColumn
            key={col.id}
            column={col}
            titleInput={titleColumnId === col.id ? titleDraft : col.title}
            isEditingTitle={titleColumnId === col.id}
            onStartEditTitle={() => startEditTitle(col.id, col.title)}
            onTitleChange={setTitleDraft}
            onCommitTitle={commitTitle}
            onCancelTitle={cancelTitle}
            showAddForm={addFormColumnId === col.id}
            onToggleAddForm={() => toggleAddForm(col.id)}
            newTitle={addFormColumnId === col.id ? newTitle : ""}
            newDetails={addFormColumnId === col.id ? newDetails : ""}
            onNewTitleChange={setNewTitle}
            onNewDetailsChange={setNewDetails}
            onSubmitNewCard={(e) => submitNewCard(col.id, e)}
            onDeleteCard={deleteCard}
          />
        ))}
      </div>
      <DragOverlay>
        {activeCard ? (
          <div className="pointer-events-none w-72 rounded-lg border border-[var(--color-accent)] bg-white p-3 shadow-lg">
            <h3 className="font-semibold text-[var(--color-navy)]">
              {activeCard.title}
            </h3>
            <p className="mt-1 text-sm text-[var(--color-gray)]">{activeCard.details}</p>
          </div>
        ) : null}
      </DragOverlay>
    </DndContext>
  );
}
