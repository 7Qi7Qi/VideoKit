package output;

import java.io.OutputStream;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class ConsoleOutput extends OutputStream {

    private TextArea console;

    public ConsoleOutput(TextArea console) {
        this.console = console;
    }

    @Override
    public void write(int b) {
        Platform.runLater(() -> console.appendText(String.valueOf((char) b)));
    }
}
