package gary.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Displays one message and identifies its speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label avatar;
    @FXML
    private Label dialog;

    private DialogBox(String text, String avatarText) {
        URL view = Objects.requireNonNull(
                DialogBox.class.getResource("/view/DialogBox.fxml"),
                "DialogBox.fxml is missing");
        FXMLLoader loader = new FXMLLoader(view);
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load dialog box", e);
        }

        dialog.setText(text);
        avatar.setText(avatarText);
    }

    /**
     * Returns a right-aligned user message.
     *
     * @param text Message text.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "YOU");
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Returns a left-aligned Gary message.
     *
     * @param text Message text.
     * @return Gary dialog box.
     */
    public static DialogBox getGaryDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "G");
        dialogBox.getStyleClass().add("gary-dialog");
        dialogBox.flip();
        return dialogBox;
    }

    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
