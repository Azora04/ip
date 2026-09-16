package gary.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import gary.task.Deadline;
import gary.task.Event;
import gary.task.Task;
import gary.task.Todo;

/**
 * Parses user input into commands and command arguments.
 */
public class Parser {
    private static final DateTimeFormatter INPUT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

    /**
     * Returns the command type represented by the user input.
     *
     * @param input User input to classify.
     * @return Matching command type, or {@link CommandType#UNKNOWN} when the input is invalid.
     */
    public CommandType parseCommandType(String input) {
        assert input != null : "Input should not be null";

        CommandType commandType = CommandType.from(input);
        if (commandType == CommandType.BYE && !input.equals("bye")) {
            return CommandType.UNKNOWN;
        }
        if (commandType == CommandType.HELP && !input.equals("help")) {
            return CommandType.UNKNOWN;
        }
        if (commandType == CommandType.LIST && !input.equals("list")) {
            return CommandType.UNKNOWN;
        }
        return commandType;
    }

    /**
     * Returns the task described by an add command.
     *
     * @param input User command containing task details.
     * @param commandType Type of task to create.
     * @return Task parsed from the command.
     * @throws IllegalArgumentException If the task details are incomplete or invalid.
     */
    public Task parseTask(String input, CommandType commandType) throws IllegalArgumentException {
        assert input != null : "Input should not be null";
        assert commandType != null : "Command type should not be null";

        return switch (commandType) {
            case TODO -> parseTodo(input);
            case DEADLINE -> parseDeadline(input);
            case EVENT -> parseEvent(input);
            default -> throw new IllegalArgumentException("Invalid command");
        };
    }

    /**
     * Returns a valid one-based task number from the user input.
     *
     * @param input User command containing a task number.
     * @param taskCount Number of tasks currently stored.
     * @return Parsed task number, or -1 when it is invalid.
     */
    public int parseTaskNumber(String input, int taskCount) {
        assert input != null : "Input should not be null";
        assert taskCount >= 0 : "Task count should not be negative";

        String numberText = getArguments(input);
        try {
            int taskNumber = Integer.parseInt(numberText);
            return taskNumber >= 1 && taskNumber <= taskCount ? taskNumber : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Returns the non-empty keyword supplied to a find command.
     *
     * @param input Find command containing the keyword.
     * @return Keyword to match against task descriptions.
     * @throws IllegalArgumentException If the keyword is empty.
     */
    public String parseKeyword(String input) throws IllegalArgumentException {
        assert input != null : "Input should not be null";

        String keyword = getArguments(input);
        if (keyword.isEmpty()) {
            throw new IllegalArgumentException("Error: The keyword for a find cannot be empty");
        }
        return keyword;
    }

    private Task parseTodo(String input) {
        String description = getArguments(input);
        if (description.isEmpty()) {
            throw new IllegalArgumentException("Error: The description of a todo cannot be empty");
        }
        return new Todo(description);
    }

    private Task parseDeadline(String input) {
        String taskDetails = getArguments(input);
        int byIndex = taskDetails.indexOf("/by");
        if (taskDetails.isEmpty()) {
            throw new IllegalArgumentException("Error: The description of a deadline cannot be empty");
        }
        if (byIndex == -1) {
            throw new IllegalArgumentException("Error: The deadline format is invalid");
        }

        String description = taskDetails.substring(0, byIndex).trim();
        String deadlineDateText = taskDetails.substring(byIndex + 3).trim();
        if (description.isEmpty()) {
            throw new IllegalArgumentException("Error: The description of a deadline cannot be empty");
        }
        if (deadlineDateText.isEmpty()) {
            throw new IllegalArgumentException("Error: The deadline time cannot be empty");
        }

        LocalDate deadlineDate = parseDate(
                deadlineDateText,
                "Error: The deadline date must be in DD-MM-YYYY format");
        return new Deadline(description, deadlineDate);
    }

    private Task parseEvent(String input) {
        String taskDetails = getArguments(input);
        int fromIndex = taskDetails.indexOf("/from");
        int toIndex = taskDetails.indexOf("/to");
        if (taskDetails.isEmpty()) {
            throw new IllegalArgumentException("Error: The description of an event cannot be empty");
        }
        if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
            throw new IllegalArgumentException("Error: The event format is invalid");
        }

        String description = taskDetails.substring(0, fromIndex).trim();
        String startDateText = taskDetails.substring(fromIndex + 5, toIndex).trim();
        String endDateText = taskDetails.substring(toIndex + 3).trim();
        if (description.isEmpty() || startDateText.isEmpty() || endDateText.isEmpty()) {
            throw new IllegalArgumentException("Error: The event format is invalid");
        }

        String invalidDateMessage = "Error: The event dates must be in DD-MM-YYYY format";
        LocalDate startDate = parseDate(startDateText, invalidDateMessage);
        LocalDate endDate = parseDate(endDateText, invalidDateMessage);
        return new Event(description, startDate, endDate);
    }

    private LocalDate parseDate(String dateText, String invalidDateMessage) {
        try {
            return LocalDate.parse(dateText, INPUT_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(invalidDateMessage);
        }
    }

    private String getArguments(String input) {
        int separatorIndex = input.indexOf(' ');
        return separatorIndex == -1 ? "" : input.substring(separatorIndex + 1).trim();
    }
}
