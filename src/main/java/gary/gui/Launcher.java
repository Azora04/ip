package gary.gui;

import javafx.application.Application;

/**
 * Launches Gary through a non-JavaFX entry point.
 */
public class Launcher {
    /**
     * Starts the Gary JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
