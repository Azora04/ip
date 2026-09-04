package gary;

import java.io.IOException;
import java.nio.file.Path;

import gary.command.CommandType;
import gary.command.Parser;
import gary.storage.Storage;
import gary.task.Task;
import gary.task.TaskList;
import gary.ui.Ui;

/**
 * Runs the Gary chatbot command-line interface.
 */
public class Gary {
    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Creates a chatbot that persists tasks at the given path.
     *
     * @param filePath Path of the task data file.
     */
    public Gary(Path filePath) {
        parser = new Parser();
        storage = new Storage(filePath);
        ui = new Ui();
        tasks = loadTasks();
    }

    /**
     * Runs the chatbot until the user exits or closes the input stream.
     */
    public void run() {
        ui.showWelcome();
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            CommandType commandType = parser.parseCommandType(command);
            ui.showDivider();
            ui.showMessage(createResponse(command, commandType));
            ui.showDivider();
            if (commandType == CommandType.BYE) {
                break;
            }
        }
    }

    /**
     * Returns Gary's response to one user command.
     *
     * @param command User command to process.
     * @return Response to display to the user.
     */
    public String getResponse(String command) {
        CommandType commandType = parser.parseCommandType(command);
        return createResponse(command, commandType);
    }

    /**
     * Starts the chatbot and processes commands until the user exits.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Gary(Path.of("data", "gary.txt")).run();
    }

    private String createResponse(String command, CommandType commandType) {
        if (commandType == CommandType.BYE) {
            return "Bye. Hope to see you again soon!";
        }
        return handleCommand(command, commandType);
    }

    private String handleCommand(String command, CommandType commandType) {
        if (commandType == CommandType.LIST) {
            return getTaskListResponse();
        } else if (commandType == CommandType.MARK) {
            return markTask(command);
        } else if (commandType == CommandType.UNMARK) {
            return unmarkTask(command);
        } else if (commandType == CommandType.TODO
                || commandType == CommandType.DEADLINE
                || commandType == CommandType.EVENT) {
            return addTask(command, commandType);
        } else if (commandType == CommandType.DELETE) {
            return deleteTask(command);
        } else if (commandType == CommandType.FIND) {
            return findTasks(command);
        }
        return "Invalid command";
    }

    private String getTaskListResponse() {
        StringBuilder response = new StringBuilder("Here are the tasks in your list:");
        for (int i = 1; i <= tasks.size(); i++) {
            response.append(System.lineSeparator())
                    .append(i)
                    .append('.')
                    .append(tasks.getTask(i));
        }
        return response.toString();
    }

    private String markTask(String command) {
        int taskNumber = parser.parseTaskNumber(command, tasks.size());
        if (taskNumber == -1) {
            return "Error: The task number is invalid";
        }

        Task task = tasks.markAsDone(taskNumber);
        return addSaveError("Nice! I've marked this task as done:"
                + System.lineSeparator() + "  " + task);
    }

    private String unmarkTask(String command) {
        int taskNumber = parser.parseTaskNumber(command, tasks.size());
        if (taskNumber == -1) {
            return "Error: The task number is invalid";
        }

        Task task = tasks.markAsNotDone(taskNumber);
        return addSaveError("OK, I've marked this task as not done yet:"
                + System.lineSeparator() + "  " + task);
    }

    private String addTask(String command, CommandType commandType) {
        try {
            return addTask(parser.parseTask(command, commandType));
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    private String addTask(Task task) {
        tasks.add(task);
        return addSaveError("Got it. I've added this task:"
                + System.lineSeparator() + "  " + task
                + System.lineSeparator() + "Now you have " + tasks.size() + " tasks in the list.");
    }

    private String deleteTask(String command) {
        int taskNumber = parser.parseTaskNumber(command, tasks.size());
        if (taskNumber == -1) {
            return "Error: The task number is invalid";
        }

        Task task = tasks.delete(taskNumber);
        return addSaveError("Noted. I've removed this task:"
                + System.lineSeparator() + "  " + task
                + System.lineSeparator() + "Now you have " + tasks.size() + " tasks in the list.");
    }

    private String findTasks(String command) {
        String keyword;
        try {
            keyword = parser.parseKeyword(command);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }

        StringBuilder response = new StringBuilder("Here are the matching tasks in your list:");
        for (int i = 1; i <= tasks.size(); i++) {
            Task task = tasks.getTask(i);
            if (task.containsKeyword(keyword)) {
                response.append(System.lineSeparator())
                        .append(i)
                        .append('.')
                        .append(task);
            }
        }
        return response.toString();
    }

    private TaskList loadTasks() {
        try {
            return new TaskList(storage.loadTasks());
        } catch (IOException e) {
            ui.showMessage("Error: Unable to load tasks");
            return new TaskList();
        }
    }

    private String addSaveError(String response) {
        try {
            storage.saveTasks(tasks.getTasks());
            return response;
        } catch (IOException e) {
            return "Error: Unable to save tasks" + System.lineSeparator() + response;
        }
    }
}
