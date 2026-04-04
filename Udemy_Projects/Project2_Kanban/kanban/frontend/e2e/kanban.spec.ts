import { expect, test } from "@playwright/test";

test.describe("Kanban MVP", () => {
  test("loads dummy board", async ({ page }) => {
    await page.goto("/");
    await expect(page.getByTestId("kanban-board")).toBeVisible({
      timeout: 15_000,
    });
    await expect(page.getByText("Design review", { exact: true })).toBeVisible();
    await expect(page.getByText("Backlog", { exact: true })).toBeVisible();
  });

  test("adds a card to To Do column", async ({ page }) => {
    await page.goto("/");
    await expect(page.getByTestId("kanban-board")).toBeVisible({
      timeout: 15_000,
    });
    const addBtn = page.getByTestId("add-card-col-todo");
    await addBtn.scrollIntoViewIfNeeded();
    await addBtn.click();
    await expect(page.getByTestId("new-card-title")).toBeVisible();
    await page.getByTestId("new-card-title").fill("E2E task");
    await page.getByTestId("new-card-details").fill("Created by Playwright");
    await page.locator("form").getByRole("button", { name: "Add card" }).click();
    await expect(page.getByText("E2E task", { exact: true })).toBeVisible();
  });

  test("removes a card", async ({ page }) => {
    await page.goto("/");
    await expect(page.getByTestId("kanban-board")).toBeVisible({
      timeout: 15_000,
    });
    const backlog = page.getByTestId("column-col-backlog");
    await expect(
      backlog.getByRole("heading", { level: 3, name: "API contract" }),
    ).toBeVisible();
    await backlog.getByRole("button", { name: "Delete API contract" }).click();
    await expect(
      backlog.getByRole("heading", { level: 3, name: "API contract" }),
    ).toHaveCount(0);
  });
});
