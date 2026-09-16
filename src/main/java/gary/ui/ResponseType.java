package gary.ui;

/**
 * Describes how a chatbot response should be presented to the user.
 */
public enum ResponseType {
    /** A successful response containing information or confirmation. */
    NORMAL,
    /** A response that explains invalid input or an unsuccessful operation. */
    ERROR,
    /** The final response shown when the user exits. */
    FAREWELL
}
