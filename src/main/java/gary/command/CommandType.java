package gary.command;

/**
 * Identifies the commands supported by Gary.
 */
public enum CommandType {
    BYE("bye"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    DELETE("delete"),
    UNKNOWN("");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the command type identified by the first word of the input.
     *
     * @param input User input to classify.
     * @return Matching command type, or {@link #UNKNOWN} when none matches.
     */
    public static CommandType from(String input) {
        int separatorIndex = input.indexOf(' ');
        String keyword = separatorIndex == -1 ? input : input.substring(0, separatorIndex);
        for (CommandType commandType : values()) {
            if (commandType.keyword.equals(keyword)) {
                return commandType;
            }
        }
        return UNKNOWN;
    }
}
