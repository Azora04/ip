package gary.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import gary.task.Deadline;
import gary.task.Event;
import gary.task.Todo;

class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseCommandType_helpWithAndWithoutArguments_returnsHelpOrUnknown() {
        assertEquals(CommandType.HELP, parser.parseCommandType("help"));
        assertEquals(CommandType.UNKNOWN, parser.parseCommandType("help extra"));
    }

    @Test
    void parseCommandType_variedWhitespaceAndCase_returnsCommandType() {
        assertEquals(CommandType.LIST, parser.parseCommandType("  LIST  "));
        assertEquals(CommandType.TODO, parser.parseCommandType("\ttodo\tread book"));
    }

    @Test
    void parseTask_supportedTypes_returnsMatchingTasks() {
        assertInstanceOf(Todo.class,
                parser.parseTask("todo read book", CommandType.TODO));
        Deadline deadline = assertInstanceOf(Deadline.class,
                parser.parseTask("deadline return book /by 02-12-2019", CommandType.DEADLINE));
        Event event = assertInstanceOf(Event.class,
                parser.parseTask("event meeting /from 02-12-2019 /to 03-12-2019", CommandType.EVENT));

        assertEquals(LocalDate.of(2019, 12, 2), deadline.getBy());
        assertEquals(LocalDate.of(2019, 12, 2), event.getFrom());
        assertEquals(LocalDate.of(2019, 12, 3), event.getTo());
    }

    @Test
    void parseTask_invalidDetails_throwsException() {
        Executable parseTodoWithoutDescription = () -> parser.parseTask("todo", CommandType.TODO);
        Executable parseDeadlineWithInvalidDate = () -> parser.parseTask(
                "deadline report /by tomorrow", CommandType.DEADLINE);
        Executable parseEventWithInvalidDates = () -> parser.parseTask(
                "event meeting /from Monday /to Tuesday", CommandType.EVENT);
        Executable parseDeadlineWithIsoDate = () -> parser.parseTask(
                "deadline report /by 2019-12-02", CommandType.DEADLINE);
        Executable parseDeadlineWithImpossibleDate = () -> parser.parseTask(
                "deadline report /by 31-02-2019", CommandType.DEADLINE);
        Executable parseEventWithReversedDates = () -> parser.parseTask(
                "event trip /from 03-12-2019 /to 02-12-2019", CommandType.EVENT);

        assertThrows(IllegalArgumentException.class, parseTodoWithoutDescription);
        assertThrows(IllegalArgumentException.class, parseDeadlineWithInvalidDate);
        assertThrows(IllegalArgumentException.class, parseEventWithInvalidDates);
        assertThrows(IllegalArgumentException.class, parseDeadlineWithIsoDate);
        assertThrows(IllegalArgumentException.class, parseDeadlineWithImpossibleDate);
        assertThrows(IllegalArgumentException.class, parseEventWithReversedDates);
    }

    @Test
    void parseTask_uppercaseSeparators_returnsMatchingTasks() {
        assertInstanceOf(Deadline.class,
                parser.parseTask("DEADLINE report /BY 02-12-2019", CommandType.DEADLINE));
        assertInstanceOf(Event.class,
                parser.parseTask("EVENT meeting /FROM 02-12-2019 /TO 03-12-2019", CommandType.EVENT));
    }

    @Test
    void parseTaskNumber_variedInputs_returnsValidatedNumber() {
        assertEquals(2, parser.parseTaskNumber("mark 2", 3));
        assertEquals(-1, parser.parseTaskNumber("mark 0", 3));
        assertEquals(-1, parser.parseTaskNumber("mark 4", 3));
        assertEquals(-1, parser.parseTaskNumber("mark two", 3));
    }

    @Test
    void parseKeyword_presentAndMissing_returnsOrThrows() {
        Executable parseMissingKeyword = () -> parser.parseKeyword("find");

        assertEquals("read book", parser.parseKeyword("find read book"));
        assertThrows(IllegalArgumentException.class, parseMissingKeyword);
    }
}
