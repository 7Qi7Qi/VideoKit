package main;

import static main.OtherKitsService.alertAutoClose;

import enums.Result;
import java.awt.Desktop;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.function.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import kit.ProgressBarKit;
import kit.UnusedKit;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import output.*;

public class FxController implements Initializable {


    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private SingleSelectionModel<Tab> selectionMode;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab mainTab;

    @FXML
    private ComboBox<String> fileComboBox;

    @FXML
    private CheckBox retainOrigin;

    @FXML
    private Tab logTab;

    @FXML
    private TextArea outputText;

    @FXML
    void handlerDownload() {
        preTreat("1");
    }

    @FXML
    void handlerFileName() {
        preTreat("2");
    }

    @FXML
    void cutVideo() {
        preTreat("3");
    }

    @FXML
    void buildCover() {
        preTreat("4");
    }

    @FXML
    void name2Capital() {
        otherKit(OtherKitsService::lower2Upper);
    }

    @FXML
    void handlerWallPaper() {
        otherKit(OtherKitsService::tidyVideos);
    }

    @FXML
    void openFolder() {
        String filePath = this.fileComboBox.getValue();
        if (StringUtils.isNotBlank(filePath)) {
            try {
                Desktop.getDesktop().open(new File(filePath));
            } catch (IOException e) {
                System.out.println("failed to open folder" + filePath);
            }
        }
    }

    @FXML
    void onOpen() {
        //FileChooser
        DirectoryChooser directoryChooser = new DirectoryChooser();
        String filePath = this.fileComboBox.getValue();
        if (StringUtils.isNotBlank(filePath)) {
            directoryChooser.setInitialDirectory(new File(filePath));
        }
        File fileSelected = directoryChooser.showDialog(null);
        directoryChooser.setTitle("选择主目录");
        if (fileSelected != null) {
            fileComboBox.setValue(fileSelected.getAbsolutePath());
        }
    }

    @FXML
    void clearOutput() {
        outputText.clear();
        selectionMode.select(mainTab);
    }

    @FXML
    void testMethod() {
//        new Thread(() -> {
//            System.out.println("LOGGER = " + LOGGER);
//            ProgressBarKit builder = new ProgressBarKit.Builder().waitMs(50).builder();
//            for (int i = 0; i < 10; i++) {
//                builder.printProgress(i);
//            }
//            System.out.println("LOGGER = " + LOGGER);
//            UnusedKit.testLogger();
//        }).start();
        selectionMode.select(mainTab);
    }

    @FXML
    void nextPage() {
        selectionMode.select(logTab);
    }

    public void otherKit(Function<String, Result> kitMethod) {
        String filePath = this.fileComboBox.getValue();
        if (StringUtils.isNotBlank(filePath)) {
            Result result = kitMethod.apply(filePath);
            if (result.getResult()) {
                alertAutoClose(null);
            } else {
                new Alert(AlertType.WARNING, "操作失败：" + result.getMessage(),
                        ButtonType.CLOSE).show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "请先选择文件目录", ButtonType.CLOSE).show();
        }
    }

    private void preTreat(String opt) {
        String filePath = this.fileComboBox.getValue();
        if (StringUtils.isNotBlank(filePath)) {
            if (!filePath.endsWith("#")) {
                new Alert(Alert.AlertType.WARNING, "文件目录必须是 # ", ButtonType.CLOSE).show();
            } else {
                try {
                    this.nextPage();
                    OtherKitsService.mainVideoFix(opt, filePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "请先选择文件目录", ButtonType.CLOSE).show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileComboBox.setEditable(true);
        fileComboBox.getItems().add("G:\\迅雷\\#");
        fileComboBox.getItems().add("D:\\Archives\\#");
        fileComboBox.setValue("G:\\迅雷\\#");
        outputText.setWrapText(true);
        //redirect stream
        PrintStream ps = new PrintStream(new ConsoleOutput(outputText));
        System.setOut(ps);
        System.setErr(ps);
        selectionMode = mainTabPane.getSelectionModel();
    }


}
