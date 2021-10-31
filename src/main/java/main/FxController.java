package main;

import enums.Result;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.function.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FxController implements Initializable {


    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @FXML
    private ComboBox<String> fileComboBox;

    @FXML
    private CheckBox retainOrigin;

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

    public void otherKit(Function<String, Result> kitMethod) {
        String filePath = this.fileComboBox.getValue();
        if (StringUtils.isNotBlank(filePath)) {
            Result result = kitMethod.apply(filePath);
            if (result.getResult()) {
                alertAutoClose(null);
            }else {
                new Alert(AlertType.WARNING, "操作失败：" + result.getMessage(), ButtonType.CLOSE).show();
            }
        }else {
            new Alert(Alert.AlertType.WARNING, "请先选择文件目录", ButtonType.CLOSE).show();
        }
    }

    private void preTreat(String opt) {
        String filePath = this.fileComboBox.getValue();
        if (StringUtils.isNotBlank(filePath)) {
            if ( !filePath.endsWith("#")) {
                new Alert(Alert.AlertType.WARNING, "文件目录必须是 # ", ButtonType.CLOSE).show();
            }else {
                long start = System.currentTimeMillis();
                LOGGER.info("Start operation from {} ", new Date(start));
                Alert process = new Alert(AlertType.INFORMATION, "任务执行中", null);
                process.show();
                try {
                    OtherKitsService.mainVideoFix(opt, filePath);
                    long end = System.currentTimeMillis();
                    LOGGER.info("Finish operation at {}, use {}s", new Date(end), (end - start)/1000);
                    this.alertAutoClose(null);
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(AlertType.ERROR, "操作失败" + e.getMessage(),  ButtonType.CANCEL).show();
                }
                process.close();
            }
        }else {
            new Alert(Alert.AlertType.WARNING, "请先选择文件目录", ButtonType.CLOSE).show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileComboBox.setEditable(true);
        fileComboBox.getItems().add("F:\\迅雷\\#");
        fileComboBox.getItems().add("D:\\Archives\\#");
        fileComboBox.setValue("F:\\迅雷\\#");
    }


    public void alertAutoClose(String contentText) {
        if (contentText == null) {
            contentText = "操作完成";
        }
        Alert alert = new Alert(AlertType.INFORMATION, contentText, ButtonType.FINISH);
        alert.show();
        Thread autoClose = new Thread(() -> {
            try {
                Thread.sleep(3000);
                Platform.runLater(() -> {
                    if (alert.isShowing()) {
                        alert.close();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        autoClose.setDaemon(true);
        autoClose.start();
    }
}
