import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Runs the Gary task manager as a command-line application.
 */
public class Gary {
    private Gary() {
    }

    /**
     * Starts an interactive Gary session.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        String banner = "██████╗   █████╗ ██████╗ ██╗   ██╗\n"
                + "██╔════╝ ██╔══██╗██╔══██╗╚██╗ ██╔╝\n"
                + "██║  ███╗███████║██████╔╝ ╚████╔╝ \n"
                + "██║   ██║██╔══██║██╔══██╗  ╚██╔╝  \n"
                + "╚██████╔╝██║  ██║██║  ██║   ██║   \n"
                + " ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝   \n";
        String line = "____________________________________________________________\n";
        System.out.println(line + "\n" +
                banner + "\n" +
                "Hello! I'm Gary.\n" +
                "What can I do for you?\n" +
                line + "\n");

        Scanner scanner = new Scanner(System.in);
        Storage storage = new Storage(Path.of("data", "gary.txt"));
        ArrayList<Task> tasks = loadTasks(storage);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            CommandType commandType = CommandType.from(command);
            System.out.println(line);

            if (commandType == CommandType.BYE && command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            }

            if (commandType == CommandType.LIST && command.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println((i + 1) + "." + tasks.get(i));
                }
            } else if (commandType == CommandType.MARK) {
                int taskNumber = getTaskNumber(command.substring(4).trim(), tasks.size());
                if (taskNumber == -1) {
                    System.out.println("Error: The task number is invalid");
                } else {
                    Task task = tasks.get(taskNumber - 1);
                    task.markAsDone();
                    saveTasks(storage, tasks);
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + task);
                }
            } else if (commandType == CommandType.UNMARK) {
                int taskNumber = getTaskNumber(command.substring(6).trim(), tasks.size());
                if (taskNumber == -1) {
                    System.out.println("Error: The task number is invalid");
                } else {
                    Task task = tasks.get(taskNumber - 1);
                    task.markAsNotDone();
                    saveTasks(storage, tasks);
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + task);
                }
            } else if (commandType == CommandType.TODO) {
                String description = command.substring(4).trim();
                if (description.isEmpty()) {
                    System.out.println("Error: The description of a todo cannot be empty");
                } else {
                    addTask(tasks, new Todo(description), storage);
                }
            } else if (commandType == CommandType.DEADLINE) {
                String taskDetails = command.substring(8).trim();
                int byIndex = taskDetails.indexOf("/by");
                if (taskDetails.isEmpty()) {
                    System.out.println("Error: The description of a deadline cannot be empty");
                } else if (byIndex == -1) {
                    System.out.println("Error: The deadline format is invalid");
                } else {
                    String description = taskDetails.substring(0, byIndex).trim();
                    String by = taskDetails.substring(byIndex + 3).trim();
                    if (description.isEmpty()) {
                        System.out.println("Error: The description of a deadline cannot be empty");
                    } else if (by.isEmpty()) {
                        System.out.println("Error: The deadline time cannot be empty");
                    } else {
                        try {
                            addTask(tasks, new Deadline(description, LocalDate.parse(by)), storage);
                        } catch (DateTimeParseException e) {
                            System.out.println("Error: The deadline date must be in yyyy-MM-dd format");
                        }
                    }
                }
            } else if (commandType == CommandType.EVENT) {
                String taskDetails = command.substring(5).trim();
                int fromIndex = taskDetails.indexOf("/from");
                int toIndex = taskDetails.indexOf("/to");
                if (taskDetails.isEmpty()) {
                    System.out.println("Error: The description of an event cannot be empty");
                } else if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
                    System.out.println("Error: The event format is invalid");
                } else {
                    String description = taskDetails.substring(0, fromIndex).trim();
                    String from = taskDetails.substring(fromIndex + 5, toIndex).trim();
                    String to = taskDetails.substring(toIndex + 3).trim();
                    if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                        System.out.println("Error: The event format is invalid");
                    } else {
                        try {
                            addTask(tasks, new Event(description, LocalDate.parse(from),
                                    LocalDate.parse(to)), storage);
                        } catch (DateTimeParseException e) {
                            System.out.println("Error: The event dates must be in yyyy-MM-dd format");
                        }
                    }
                }
            } else if (commandType == CommandType.DELETE) {
                int taskNumber = getTaskNumber(command.substring(6).trim(), tasks.size());
                if (taskNumber == -1) {
                    System.out.println("Error: The task number is invalid");
                } else {
                    Task task = tasks.remove(taskNumber - 1);
                    saveTasks(storage, tasks);
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + task);
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                }
            } else {
                System.out.println("Invalid command");
            }
            System.out.println(line);
        }
    }

    /**
     * Adds a task, saves the updated list, and displays a confirmation.
     */
    private static void addTask(ArrayList<Task> tasks, Task task, Storage storage) {
        tasks.add(task);
        saveTasks(storage, tasks);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Returns a validated one-based task number.
     *
     * @return Task number, or {@code -1} if the input is not a valid task number.
     */
    private static int getTaskNumber(String numberText, int taskCount) {
        try {
            int taskNumber = Integer.parseInt(numberText);
            return taskNumber >= 1 && taskNumber <= taskCount ? taskNumber : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Loads saved tasks and recovers with an empty list if loading fails.
     */
    private static ArrayList<Task> loadTasks(Storage storage) {
        try {
            return storage.loadTasks();
        } catch (IOException e) {
            System.out.println("Error: Unable to load tasks");
            return new ArrayList<>();
        }
    }

    /**
     * Saves all tasks and reports any storage error to the user.
     */
    private static void saveTasks(Storage storage, ArrayList<Task> tasks) {
        try {
            storage.saveTasks(tasks);
        } catch (IOException e) {
            System.out.println("Error: Unable to save tasks");
        }
    }
}
