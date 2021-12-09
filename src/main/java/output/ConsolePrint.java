package output;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.jetbrains.annotations.NotNull;

public class ConsolePrint extends PrintStream {

    TextArea console;

    public ConsolePrint(TextArea console) {
        this(console, true);
    }
    public ConsolePrint(TextArea console, boolean autoFlush) {
        super(new ByteArrayOutputStream(), autoFlush);
        this.console = console;
    }

    @Override
    public void write(@NotNull byte[] bytes, int off, int len) {
        if (len != 0) {
            Platform.runLater(() -> {
                String s = new String(bytes, off, len);
                if (s.contains("\r\n")) {
                    console.appendText(s);
                }else {
                    console.setText(s);
                }
            });

        }
    }

}
