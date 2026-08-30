package gary;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
        ui.showWelcome();
        Storage storage = new Storage(Path.of("data", "gary.txt"));
        ArrayList<Task> tasks = loadTasks(storage, ui);
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            CommandType commandType = CommandType.from(command);
            ui.showDivider();

            if (commandType == CommandType.BYE && command.equals("bye")) {
                ui.showMessage("Bye. Hope to see you again soon!");
                ui.showDivider();
                break;
            }

            if (commandType == CommandType.LIST && command.equals("list")) {
                ui.showMessage("Here are the tasks in your list:");
                for (int i = 0; i < tasks.size(); i++) {
                    ui.showMessage((i + 1) + "." + tasks.get(i));
                }
            } else if (commandType == CommandType.MARK) {
                int taskNumber = getTaskNumber(command.substring(4).trim(), tasks.size());
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
                int taskNumber = getTaskNumber(command.substring(6).trim(), tasks.size());
                if (taskNumber == -1) {
                    ui.showMessage("Error: The task number is invalid");
                } else {
                    Task task = tasks.get(taskNumber - 1);
                    task.markAsNotDone();
                    saveTasks(storage, tasks, ui);
                    ui.showMessage("OK, I've marked this task as not done yet:");
                    ui.showMessage("  " + task);
                }
            } else if (commandType == CommandType.TODO) {
                String description = command.substring(4).trim();
                if (description.isEmpty()) {
                    ui.showMessage("Error: The description of a todo cannot be empty");
                } else {
                    addTask(tasks, new Todo(description), storage, ui);
                }
            } else if (commandType == CommandType.DEADLINE) {
                String taskDetails = command.substring(8).trim();
                int byIndex = taskDetails.indexOf("/by");
                if (taskDetails.isEmpty()) {
                    ui.showMessage("Error: The description of a deadline cannot be empty");
                } else if (byIndex == -1) {
                    ui.showMessage("Error: The deadline format is invalid");
                } else {
                    String description = taskDetails.substring(0, byIndex).trim();
                    String by = taskDetails.substring(byIndex + 3).trim();
                    if (description.isEmpty()) {
                        ui.showMessage("Error: The description of a deadline cannot be empty");
                    } else if (by.isEmpty()) {
                        ui.showMessage("Error: The deadline time cannot be empty");
                    } else {
                        try {
                            addTask(tasks, new Deadline(description, LocalDate.parse(by)), storage, ui);
                        } catch (DateTimeParseException e) {
                            ui.showMessage("Error: The deadline date must be in yyyy-MM-dd format");
                        }
                    }
                }
            } else if (commandType == CommandType.EVENT) {
                String taskDetails = command.substring(5).trim();
                int fromIndex = taskDetails.indexOf("/from");
                int toIndex = taskDetails.indexOf("/to");
                if (taskDetails.isEmpty()) {
                    ui.showMessage("Error: The description of an event cannot be empty");
                } else if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
                    ui.showMessage("Error: The event format is invalid");
                } else {
                    String description = taskDetails.substring(0, fromIndex).trim();
                    String from = taskDetails.substring(fromIndex + 5, toIndex).trim();
                    String to = taskDetails.substring(toIndex + 3).trim();
                    if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                        ui.showMessage("Error: The event format is invalid");
                    } else {
                        try {
                            addTask(tasks, new Event(description, LocalDate.parse(from),
                                    LocalDate.parse(to)), storage, ui);
                        } catch (DateTimeParseException e) {
                            ui.showMessage("Error: The event dates must be in yyyy-MM-dd format");
                        }
                    }
                }
            } else if (commandType == CommandType.DELETE) {
                int taskNumber = getTaskNumber(command.substring(6).trim(), tasks.size());
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

    private static int getTaskNumber(String numberText, int taskCount) {
        try {
            int taskNumber = Integer.parseInt(numberText);
            return taskNumber >= 1 && taskNumber <= taskCount ? taskNumber : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
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
