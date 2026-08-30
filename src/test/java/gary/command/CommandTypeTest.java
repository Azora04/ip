package gary.command;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CommandTypeTest {
    @Test
    void from_supportedCommands_returnsMatchingTypes() {
        assertAll(
                () -> assertEquals(CommandType.BYE, CommandType.from("bye")),
                () -> assertEquals(CommandType.LIST, CommandType.from("list")),
                () -> assertEquals(CommandType.MARK, CommandType.from("mark 1")),
                () -> assertEquals(CommandType.UNMARK, CommandType.from("unmark 1")),
                () -> assertEquals(CommandType.TODO, CommandType.from("todo read book")),
                () -> assertEquals(CommandType.DEADLINE,
                        CommandType.from("deadline return book /by 2019-12-02")),
                () -> assertEquals(CommandType.EVENT,
                        CommandType.from("event meeting /from 2019-12-02 /to 2019-12-03")),
                () -> assertEquals(CommandType.DELETE, CommandType.from("delete 1"))
        );
    }

    @Test
    void from_unknownOrEmptyInput_returnsUnknown() {
        assertAll(
                () -> assertEquals(CommandType.UNKNOWN, CommandType.from("")),
                () -> assertEquals(CommandType.UNKNOWN, CommandType.from("blah")),
                () -> assertEquals(CommandType.UNKNOWN, CommandType.from(" todo read book")),
                () -> assertEquals(CommandType.UNKNOWN, CommandType.from("Todo read book"))
        );
    }
}
