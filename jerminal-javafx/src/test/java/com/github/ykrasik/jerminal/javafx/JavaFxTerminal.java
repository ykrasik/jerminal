package com.github.ykrasik.jerminal.javafx;

import com.github.ykrasik.jerminal.api.display.terminal.Terminal;
import javafx.scene.control.TextArea;

import java.util.Objects;

/**
 * @author Yevgeny Krasik
 */
public class JavaFxTerminal implements Terminal {
    private final TextArea textArea;

    public JavaFxTerminal(TextArea textArea) {
        this.textArea = Objects.requireNonNull(textArea);
    }

    @Override
    public void begin() {

    }

    @Override
    public void end() {

    }

    @Override
    public void println(String text) {
        textArea.appendText(text);
        textArea.appendText("\n");
    }

    @Override
    public void printlnError(String text) {
        // TODO: Make this red.
        println(text);
    }
}