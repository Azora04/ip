package gary;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GaryTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_taskLifecycle_returnsUpdatedTaskDetails() {
        Gary gary = new Gary(temporaryDirectory.resolve("data").resolve("gary.txt"));
        String separator = System.lineSeparator();

        assertEquals("Got it. I've added this task:" + separator
                + "  [T][ ] read book" + separator
                + "Now you have 1 tasks in the list.", gary.getResponse("todo read book"));
        assertEquals("Nice! I've marked this task as done:" + separator
                + "  [T][X] read book", gary.getResponse("mark 1"));
        assertEquals("Here are the matching tasks in your list:" + separator
                + "1.[T][X] read book", gary.getResponse("find BOOK"));
        assertEquals("OK, I've marked this task as not done yet:" + separator
                + "  [T][ ] read book", gary.getResponse("unmark 1"));
        assertEquals("Noted. I've removed this task:" + separator
                + "  [T][ ] read book" + separator
                + "Now you have 0 tasks in the list.", gary.getResponse("delete 1"));
    }

    @Test
    void getResponse_invalidAndExitCommands_returnsMessages() {
        Gary gary = new Gary(temporaryDirectory.resolve("gary.txt"));

        assertEquals("Error: The description of a todo cannot be empty", gary.getResponse("todo"));
        assertEquals("Invalid command", gary.getResponse("blah"));
        assertEquals("Bye. Hope to see you again soon!", gary.getResponse("bye"));
    }

    @Test
    void getResponse_newInstance_loadsPersistedTasks() {
        Path filePath = temporaryDirectory.resolve("data").resolve("gary.txt");
        Gary firstGary = new Gary(filePath);
        String separator = System.lineSeparator();

        firstGary.getResponse("deadline return book /by 2019-12-02");
        firstGary.getResponse("mark 1");
        Gary secondGary = new Gary(filePath);

        assertEquals("Here are the tasks in your list:" + separator
                + "1.[D][X] return book (by: Dec 2 2019)", secondGary.getResponse("list"));
    }
}
