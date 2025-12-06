# Guidelines for Task Management

This document provides instructions on how to work with the `ai/tasks.md` checklist during the development of the Agent Prediction Cloud.

## Workflow
1.  **Pick a Task:** Select the next available task from `ai/tasks.md` based on the phase order.
2.  **Implement:** Write the code to complete the task. Ensure you adhere to the requirements linked to the task.
3.  **Verify:** Run tests or manually verify that the task is completed and meets the acceptance criteria.
4.  **Mark Complete:** Update `ai/tasks.md` by changing `[ ]` to `[x]` for the completed task.

## Rules
*   **Do not remove tasks:** If a task is no longer relevant, mark it as skipped or add a note, but keep the history.
*   **Adding Tasks:** If new work is discovered:
    *   Add a new task entry in the appropriate phase.
    *   Link it to the relevant Requirement and Plan Item (update `requirements.md` and `plan.md` if necessary).
*   **Consistency:** Maintain the existing formatting of the task list.
*   **Atomic Commits:** Try to commit changes related to a single task or a small group of related tasks.

## Reference
*   **Requirements:** `ai/requirements.md` - The source of truth for what needs to be built.
*   **Plan:** `ai/plan.md` - The high-level strategy for implementation.
*   **Tech Spec:** `ai/technical_specification.md` - Detailed technical decisions and architecture.
