package gary;

import java.io.IOException;
import java.nio.file.Path;

import gary.command.CommandType;
import gary.command.Parser;
import gary.contact.Contact;
import gary.contact.ContactCommandType;
import gary.contact.ContactList;
import gary.contact.ContactParser;
import gary.contact.ContactStorage;
import gary.storage.Storage;
import gary.task.Task;
import gary.task.TaskList;
import gary.ui.ChatResponse;
import gary.ui.Ui;

/**
 * Runs the Gary task and contact management chatbot.
 */
public class Gary {
    private final ContactList contacts;
    private final ContactParser contactParser;
    private final ContactStorage contactStorage;
    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Creates a chatbot that persists tasks and contacts beside the given path.
     *
     * @param filePath Path of the task data file.
     */
    public Gary(Path filePath) {
        Path contactFilePath = filePath.resolveSibling("contacts.txt");
        contactParser = new ContactParser();
        contactStorage = new ContactStorage(contactFilePath);
        parser = new Parser();
        storage = new Storage(filePath);
        ui = new Ui();
        contacts = loadContacts();
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
            ui.showMessage(createResponse(command, commandType).message());
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
        return getChatResponse(command).message();
    }

    /**
     * Returns Gary's typed response to one user command.
     *
     * @param command User command to process.
     * @return Response text and its presentation type.
     */
    public ChatResponse getChatResponse(String command) {
        assert command != null : "Command should not be null";

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

    private ChatResponse createResponse(String command, CommandType commandType) {
        assert command != null : "Command should not be null";
        assert commandType != null : "Command type should not be null";

        return switch (commandType) {
            case BYE -> ChatResponse.farewell("Bye. Hope to see you again soon!");
            case LIST -> getTaskListResponse();
            case MARK -> markTask(command);
            case UNMARK -> unmarkTask(command);
            case TODO, DEADLINE, EVENT -> addTask(command, commandType);
            case DELETE -> deleteTask(command);
            case FIND -> findTasks(command);
            case CONTACT -> handleContactCommand(command);
            case UNKNOWN -> ChatResponse.error("Invalid command");
        };
    }

    private ChatResponse handleContactCommand(String command) {
        ContactCommandType commandType = contactParser.parseCommandType(command);
        return switch (commandType) {
            case ADD -> addContact(command);
            case LIST -> getContactListResponse();
            case FIND -> findContacts(command);
            case DELETE -> deleteContact(command);
            case UNKNOWN -> ChatResponse.error("Invalid contact command");
        };
    }

    private ChatResponse addContact(String command) {
        try {
            Contact contact = contactParser.parseContact(command);
            contacts.add(contact);
            return addContactSaveError(formatResponse(
                    "Got it. I've added this contact:",
                    "  " + contact,
                    getContactCountMessage()));
        } catch (IllegalArgumentException e) {
            return ChatResponse.error(e.getMessage());
        }
    }

    private ChatResponse getContactListResponse() {
        StringBuilder response = new StringBuilder("Here are your contacts:");
        appendMatchingContacts(response, "");
        return ChatResponse.normal(response.toString());
    }

    private ChatResponse findContacts(String command) {
        String keyword;
        try {
            keyword = contactParser.parseKeyword(command);
        } catch (IllegalArgumentException e) {
            return ChatResponse.error(e.getMessage());
        }

        StringBuilder response = new StringBuilder("Here are the matching contacts:");
        appendMatchingContacts(response, keyword);
        return ChatResponse.normal(response.toString());
    }

    private void appendMatchingContacts(StringBuilder response, String keyword) {
        for (int i = 1; i <= contacts.size(); i++) {
            Contact contact = contacts.getContact(i);
            if (keyword.isEmpty() || contact.containsKeyword(keyword)) {
                response.append(System.lineSeparator())
                        .append(i)
                        .append(". ")
                        .append(contact);
            }
        }
    }

    private ChatResponse deleteContact(String command) {
        int contactNumber = contactParser.parseContactNumber(command, contacts.size());
        if (contactNumber == -1) {
            return ChatResponse.error("Error: The contact number is invalid");
        }

        Contact contact = contacts.delete(contactNumber);
        return addContactSaveError(formatResponse(
                "Noted. I've removed this contact:",
                "  " + contact,
                getContactCountMessage()));
    }

    private String getContactCountMessage() {
        String contactWord = contacts.size() == 1 ? "contact" : "contacts";
        return "Now you have " + contacts.size() + " " + contactWord + ".";
    }

    private ChatResponse getTaskListResponse() {
        StringBuilder response = new StringBuilder("Here are the tasks in your list:");
        for (int i = 1; i <= tasks.size(); i++) {
            response.append(System.lineSeparator())
                    .append(i)
                    .append('.')
                    .append(tasks.getTask(i));
        }
        return ChatResponse.normal(response.toString());
    }

    private ChatResponse markTask(String command) {
        int taskNumber = parser.parseTaskNumber(command, tasks.size());
        if (taskNumber == -1) {
            return ChatResponse.error("Error: The task number is invalid");
        }

        Task task = tasks.markAsDone(taskNumber);
        return addSaveError(formatResponse(
                "Nice! I've marked this task as done:",
                "  " + task));
    }

    private ChatResponse unmarkTask(String command) {
        int taskNumber = parser.parseTaskNumber(command, tasks.size());
        if (taskNumber == -1) {
            return ChatResponse.error("Error: The task number is invalid");
        }

        Task task = tasks.markAsNotDone(taskNumber);
        return addSaveError(formatResponse(
                "OK, I've marked this task as not done yet:",
                "  " + task));
    }

    private ChatResponse addTask(String command, CommandType commandType) {
        try {
            return addTask(parser.parseTask(command, commandType));
        } catch (IllegalArgumentException e) {
            return ChatResponse.error(e.getMessage());
        }
    }

    private ChatResponse addTask(Task task) {
        tasks.add(task);
        return addSaveError(formatResponse(
                "Got it. I've added this task:",
                "  " + task,
                "Now you have " + tasks.size() + " tasks in the list."));
    }

    private ChatResponse deleteTask(String command) {
        int taskNumber = parser.parseTaskNumber(command, tasks.size());
        if (taskNumber == -1) {
            return ChatResponse.error("Error: The task number is invalid");
        }

        Task task = tasks.delete(taskNumber);
        return addSaveError(formatResponse(
                "Noted. I've removed this task:",
                "  " + task,
                "Now you have " + tasks.size() + " tasks in the list."));
    }

    private ChatResponse findTasks(String command) {
        String keyword;
        try {
            keyword = parser.parseKeyword(command);
        } catch (IllegalArgumentException e) {
            return ChatResponse.error(e.getMessage());
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
        return ChatResponse.normal(response.toString());
    }

    private TaskList loadTasks() {
        try {
            return new TaskList(storage.loadTasks());
        } catch (IOException e) {
            ui.showMessage("Error: Unable to load tasks");
            return new TaskList();
        }
    }

    private ContactList loadContacts() {
        try {
            return new ContactList(contactStorage.loadContacts());
        } catch (IOException e) {
            ui.showMessage("Error: Unable to load contacts");
            return new ContactList();
        }
    }

    private ChatResponse addSaveError(String response) {
        try {
            storage.saveTasks(tasks.getTasks());
            return ChatResponse.normal(response);
        } catch (IOException e) {
            return ChatResponse.error(formatResponse("Error: Unable to save tasks", response));
        }
    }

    private ChatResponse addContactSaveError(String response) {
        try {
            contactStorage.saveContacts(contacts.getContacts());
            return ChatResponse.normal(response);
        } catch (IOException e) {
            return ChatResponse.error(formatResponse("Error: Unable to save contacts", response));
        }
    }

    private static String formatResponse(String... lines) {
        return String.join(System.lineSeparator(), lines);
    }
}
