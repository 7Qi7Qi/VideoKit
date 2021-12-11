package output;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.jetbrains.annotations.NotNull;

public class ConsoleOutput extends OutputStream {

    private final TextArea console;

    public ConsoleOutput(TextArea console) {
        this.console = console;
    }

    @Override
    public void write(int b) {
        Platform.runLater(() -> console.appendText(String.valueOf((char) b)));
    }

    @Override
    public void write(@NotNull byte[] b, int off, int len) {
        if ( len > 0) {
            //set string charset, avoid garbled output problem
            String s = new String(b, off, len, StandardCharsets.UTF_8);
            Platform.runLater(() -> console.appendText(s));
        }
    }
}
