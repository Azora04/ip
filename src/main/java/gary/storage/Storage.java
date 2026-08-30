package gary.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import gary.task.Deadline;
import gary.task.Event;
import gary.task.Task;
import gary.task.Todo;

/**
 * Loads tasks from and saves tasks to a text file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a storage manager for the specified data file.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads valid task records from the data file.
     *
     * @return Tasks represented by valid records, or an empty list if the file does not exist.
     * @throws IOException If the data file cannot be read.
     */
    public ArrayList<Task> loadTasks() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
            Task task = parseTask(line);
            if (task != null) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    /**
     * Saves the tasks, creating the data directory when needed.
     *
     * @param tasks Tasks to save.
     * @throws IOException If the tasks cannot be written.
     */
    public void saveTasks(List<Task> tasks) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(formatTask(task));
        }
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }

    private Task parseTask(String line) {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3 || !(fields[1].equals("0") || fields[1].equals("1"))) {
            return null;
        }

        Task task;
        try {
            switch (fields[0]) {
                case "T":
                    task = fields.length == 3 && !fields[2].isBlank() ? new Todo(fields[2]) : null;
                    break;
                case "D":
                    task = fields.length == 4 && !fields[2].isBlank() && !fields[3].isBlank()
                            ? new Deadline(fields[2], LocalDate.parse(fields[3])) : null;
                    break;
                case "E":
                    task = fields.length == 5 && !fields[2].isBlank()
                            && !fields[3].isBlank() && !fields[4].isBlank()
                            ? new Event(fields[2], LocalDate.parse(fields[3]),
                                    LocalDate.parse(fields[4])) : null;
                    break;
                default:
                    task = null;
            }
        } catch (DateTimeParseException e) {
            task = null;
        }

        if (task != null && fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    private String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Todo) {
            return "T | " + status + " | " + task.getDescription();
        }
        if (task instanceof Deadline deadline) {
            return "D | " + status + " | " + task.getDescription() + " | " + deadline.getBy();
        }
        if (task instanceof Event event) {
            return "E | " + status + " | " + task.getDescription() + " | "
                    + event.getFrom() + " | " + event.getTo();
        }
        throw new IllegalArgumentException("Unsupported task type: " + task.getClass().getName());
    }
}
