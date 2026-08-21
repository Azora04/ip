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
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(line);

            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            }

            System.out.println(command);
            System.out.println(line);
        }
    }
}
