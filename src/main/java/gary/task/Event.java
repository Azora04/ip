package gary.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that takes place between two dates.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    /** Date on which the event starts. */
    private LocalDate from;
    /** Date on which the event ends. */
    private LocalDate to;

    /**
     * Creates an incomplete event.
     *
     * @param description Description of the task.
     * @param from Date on which the event starts.
     * @param to Date on which the event ends.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start date.
     *
     * @return Event start date.
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns the event end date.
     *
     * @return Event end date.
     */
    public LocalDate getTo() {
        return to;
    }

    /**
     * Returns the event in a user-friendly display format.
     *
     * @return Formatted event.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }
}
