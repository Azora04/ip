package gary.gui;

import gary.Gary;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Controls the main Gary chatbot window.
 */
public class MainWindow {
    private static final String WELCOME_MESSAGE = "Hello! I'm Gary.\nWhat can I do for you?";

    @FXML
    private VBox dialogContainer;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button sendButton;
    @FXML
    private TextField userInput;

    private Gary gary;

    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the chatbot used to process input and shows its greeting.
     *
     * @param gary Chatbot to use.
     */
    public void setGary(Gary gary) {
        this.gary = gary;
        dialogContainer.getChildren().add(DialogBox.getGaryDialog(WELCOME_MESSAGE));
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = gary.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getGaryDialog(response));
        userInput.clear();

        if (input.equals("bye")) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }
}
