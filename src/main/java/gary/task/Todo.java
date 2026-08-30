package gary.task;

/**
 * Represents a task without an associated date.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
