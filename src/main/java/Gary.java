import java.util.Scanner;

public class Gary {
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
        Task[] tasks = new Task[100];
        int taskCount = 0;
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(line);

            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            }

            if (command.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + tasks[i]);
                }
            } else if (command.equals("mark") || command.startsWith("mark ")) {
                int taskNumber = getTaskNumber(command.substring(4).trim(), taskCount);
                if (taskNumber == -1) {
                    System.out.println("Error: The task number is invalid");
                } else {
                    Task task = tasks[taskNumber - 1];
                    task.markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + task);
                }
            } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                int taskNumber = getTaskNumber(command.substring(6).trim(), taskCount);
                if (taskNumber == -1) {
                    System.out.println("Error: The task number is invalid");
                } else {
                    Task task = tasks[taskNumber - 1];
                    task.markAsNotDone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + task);
                }
            } else if (command.equals("todo") || command.startsWith("todo ")) {
                String description = command.substring(4).trim();
                if (description.isEmpty()) {
                    System.out.println("Error: The description of a todo cannot be empty");
                } else {
                    taskCount = addTask(tasks, taskCount, new Todo(description));
                }
            } else if (command.equals("deadline") || command.startsWith("deadline ")) {
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
                        taskCount = addTask(tasks, taskCount, new Deadline(description, by));
                    }
                }
            } else if (command.equals("event") || command.startsWith("event ")) {
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
                        taskCount = addTask(tasks, taskCount, new Event(description, from, to));
                    }
                }
            } else {
                System.out.println("Invalid command");
            }
            System.out.println(line);
        }
    }

    private static int addTask(Task[] tasks, int taskCount, Task task) {
        tasks[taskCount] = task;
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + (taskCount + 1) + " tasks in the list.");
        return taskCount + 1;
    }

    private static int getTaskNumber(String numberText, int taskCount) {
        try {
            int taskNumber = Integer.parseInt(numberText);
            return taskNumber >= 1 && taskNumber <= taskCount ? taskNumber : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
