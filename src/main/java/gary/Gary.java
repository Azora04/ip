package gary;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Runs the Gary chatbot command-line interface.
 */
public class Gary {
    /**
     * Starts the chatbot and processes commands until the user exits.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Parser parser = new Parser();
        ui.showWelcome();
        Storage storage = new Storage(Path.of("data", "gary.txt"));
        ArrayList<Task> tasks = loadTasks(storage, ui);
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            CommandType commandType = parser.parseCommandType(command);
            ui.showDivider();

            if (commandType == CommandType.BYE) {
                ui.showMessage("Bye. Hope to see you again soon!");
                ui.showDivider();
                break;
            }

            if (commandType == CommandType.LIST) {
                ui.showMessage("Here are the tasks in your list:");
                for (int i = 0; i < tasks.size(); i++) {
                    ui.showMessage((i + 1) + "." + tasks.get(i));
                }
            } else if (commandType == CommandType.MARK) {
                int taskNumber = parser.parseTaskNumber(command, tasks.size());
                if (taskNumber == -1) {
                    ui.showMessage("Error: The task number is invalid");
                } else {
                    Task task = tasks.get(taskNumber - 1);
                    task.markAsDone();
                    saveTasks(storage, tasks, ui);
                    ui.showMessage("Nice! I've marked this task as done:");
                    ui.showMessage("  " + task);
                }
            } else if (commandType == CommandType.UNMARK) {
                int taskNumber = parser.parseTaskNumber(command, tasks.size());
                if (taskNumber == -1) {
                    ui.showMessage("Error: The task number is invalid");
                } else {
                    Task task = tasks.get(taskNumber - 1);
                    task.markAsNotDone();
                    saveTasks(storage, tasks, ui);
                    ui.showMessage("OK, I've marked this task as not done yet:");
                    ui.showMessage("  " + task);
                }
            } else if (commandType == CommandType.TODO
                    || commandType == CommandType.DEADLINE
                    || commandType == CommandType.EVENT) {
                try {
                    addTask(tasks, parser.parseTask(command, commandType), storage, ui);
                } catch (IllegalArgumentException e) {
                    ui.showMessage(e.getMessage());
                }
            } else if (commandType == CommandType.DELETE) {
                int taskNumber = parser.parseTaskNumber(command, tasks.size());
                if (taskNumber == -1) {
                    ui.showMessage("Error: The task number is invalid");
                } else {
                    Task task = tasks.remove(taskNumber - 1);
                    saveTasks(storage, tasks, ui);
                    ui.showMessage("Noted. I've removed this task:");
                    ui.showMessage("  " + task);
                    ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
                }
            } else {
                ui.showMessage("Invalid command");
            }
            ui.showDivider();
        }
    }

    private static void addTask(ArrayList<Task> tasks, Task task, Storage storage, Ui ui) {
        tasks.add(task);
        saveTasks(storage, tasks, ui);
        ui.showMessage("Got it. I've added this task:");
        ui.showMessage("  " + task);
        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
    }

    private static ArrayList<Task> loadTasks(Storage storage, Ui ui) {
        try {
            return storage.loadTasks();
        } catch (IOException e) {
            ui.showMessage("Error: Unable to load tasks");
            return new ArrayList<>();
        }
    }

    private static void saveTasks(Storage storage, ArrayList<Task> tasks, Ui ui) {
        try {
            storage.saveTasks(tasks);
        } catch (IOException e) {
            ui.showMessage("Error: Unable to save tasks");
        }
    }
}
