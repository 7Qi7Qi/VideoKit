package kit;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javafx.scene.control.TextArea;
import org.jetbrains.annotations.NotNull;

public class ConsolePrint extends PrintStream {

    TextArea console;

    public ConsolePrint(TextArea console) {
        super(new ByteArrayOutputStream());
        this.console = console;
    }

    @Override
    public void write(@NotNull byte[] bytes, int off, int len) {
        print(new String(bytes, off, len));
    }

    @Override
    public void print(String s) {
        console.appendText(s);
    }
}
