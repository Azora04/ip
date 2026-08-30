package gary.command;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gary.task.Deadline;
import gary.task.Event;
import gary.task.Todo;
import org.junit.jupiter.api.Test;

class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseTask_supportedTypes_returnsMatchingTasks() {
        assertAll(
                () -> assertInstanceOf(Todo.class,
                        parser.parseTask("todo read book", CommandType.TODO)),
                () -> assertInstanceOf(Deadline.class,
                        parser.parseTask("deadline return book /by 2019-12-02", CommandType.DEADLINE)),
                () -> assertInstanceOf(Event.class,
                        parser.parseTask("event meeting /from 2019-12-02 /to 2019-12-03", CommandType.EVENT))
        );
    }

    @Test
    void parseTask_invalidDetails_throwsException() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> parser.parseTask("todo", CommandType.TODO)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> parser.parseTask("deadline report /by tomorrow", CommandType.DEADLINE)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> parser.parseTask("event meeting /from Monday /to Tuesday", CommandType.EVENT))
        );
    }

    @Test
    void parseTaskNumber_variedInputs_returnsValidatedNumber() {
        assertAll(
                () -> assertEquals(2, parser.parseTaskNumber("mark 2", 3)),
                () -> assertEquals(-1, parser.parseTaskNumber("mark 0", 3)),
                () -> assertEquals(-1, parser.parseTaskNumber("mark 4", 3)),
                () -> assertEquals(-1, parser.parseTaskNumber("mark two", 3))
        );
    }

    @Test
    void parseKeyword_presentAndMissing_returnsOrThrows() {
        assertAll(
                () -> assertEquals("read book", parser.parseKeyword("find read book")),
                () -> assertThrows(IllegalArgumentException.class, () -> parser.parseKeyword("find"))
        );
    }
}
