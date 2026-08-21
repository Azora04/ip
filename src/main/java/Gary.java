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
                    System.out.println((i + 1) + ".[" + tasks[i].getStatusIcon() + "] "
                            + tasks[i].getDescription());
                }
            } else if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring(5));
                Task task = tasks[taskNumber - 1];
                task.markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  [" + task.getStatusIcon() + "] " + task.getDescription());
            } else if (command.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(command.substring(7));
                Task task = tasks[taskNumber - 1];
                task.markAsNotDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  [" + task.getStatusIcon() + "] " + task.getDescription());
            } else {
                tasks[taskCount] = new Task(command);
                taskCount++;
                System.out.println("added: " + command);
            }
            System.out.println(line);
        }
    }
}
