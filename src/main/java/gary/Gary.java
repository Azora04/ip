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

            if (commandType == CommandType.BYE) {
                ui.showMessage("Bye. Hope to see you again soon!");
                ui.showDivider();
                break;
            }

            handleCommand(command, commandType);
            ui.showDivider();
        }
    }

    /**
     * Starts the chatbot and processes commands until the user exits.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Gary(Path.of("data", "gary.txt")).run();
    }

    private void handleCommand(String command, CommandType commandType) {
        if (commandType == CommandType.LIST) {
            showTaskList();
        } else if (commandType == CommandType.MARK) {
            markTask(command);
        } else if (commandType == CommandType.UNMARK) {
            unmarkTask(command);
        } else if (commandType == CommandType.TODO
                || commandType == CommandType.DEADLINE
                || commandType == CommandType.EVENT) {
            addTask(command, commandType);
        } else if (commandType == CommandType.DELETE) {
            deleteTask(command);
        } else {
            ui.showMessage("Invalid command");
        }
    }

    private void showTaskList() {
        ui.showMessage("Here are the tasks in your list:");
        for (int i = 1; i <= tasks.size(); i++) {
            ui.showMessage(i + "." + tasks.getTask(i));
        }
    }

    private void markTask(String command) {
        int taskNumber = parser.parseTaskNumber(command, tasks.size());
        if (taskNumber == -1) {
            ui.showMessage("Error: The task number is invalid");
            return;
        }

        Task task = tasks.markAsDone(taskNumber);
        saveTasks();
        ui.showMessage("Nice! I've marked this task as done:");
        ui.showMessage("  " + task);
    }

    private void unmarkTask(String command) {
        int taskNumber = parser.parseTaskNumber(command, tasks.size());
        if (taskNumber == -1) {
            ui.showMessage("Error: The task number is invalid");
            return;
        }

        Task task = tasks.markAsNotDone(taskNumber);
        saveTasks();
        ui.showMessage("OK, I've marked this task as not done yet:");
        ui.showMessage("  " + task);
    }

    private void addTask(String command, CommandType commandType) {
        try {
            addTask(parser.parseTask(command, commandType));
        } catch (IllegalArgumentException e) {
            ui.showMessage(e.getMessage());
        }
    }

    private void addTask(Task task) {
        tasks.add(task);
        saveTasks();
        ui.showMessage("Got it. I've added this task:");
        ui.showMessage("  " + task);
        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
    }

    private void deleteTask(String command) {
        int taskNumber = parser.parseTaskNumber(command, tasks.size());
        if (taskNumber == -1) {
            ui.showMessage("Error: The task number is invalid");
            return;
        }

        Task task = tasks.delete(taskNumber);
        saveTasks();
        ui.showMessage("Noted. I've removed this task:");
        ui.showMessage("  " + task);
        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
    }

    private TaskList loadTasks() {
        try {
            return new TaskList(storage.loadTasks());
        } catch (IOException e) {
            ui.showMessage("Error: Unable to load tasks");
            return new TaskList();
        }
    }

    private void saveTasks() {
        try {
            storage.saveTasks(tasks.getTasks());
        } catch (IOException e) {
            ui.showMessage("Error: Unable to save tasks");
        }
    }
}
