package main;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class FxController implements Initializable {


    @FXML
    private TextField fileTextField;

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
    void handlerWallPaper() {
        String filePath = this.fileTextField.getText();
        if (StringUtils.isNotBlank(filePath)) {
            VideoKitService.tidyVideos(filePath);
        }else {
            new Alert(Alert.AlertType.WARNING, "请先选择文件目录", null).show();
        }
    }

    @FXML
    void onOpen() {
        Stage fileStage = null;
        //FileChooser
        DirectoryChooser directoryChooser = new DirectoryChooser();
        String filePath = this.fileTextField.getText();
        if (StringUtils.isNotBlank(filePath)) {
            directoryChooser.setInitialDirectory(new File(filePath));
        }
        File fileSelected = directoryChooser.showDialog(fileStage);
        directoryChooser.setTitle("选择主目录");
        if (fileSelected != null) {
            fileTextField.setText(fileSelected.getAbsolutePath());
        }
    }

    private void preTreat(String opt) {
        String filePath = this.fileTextField.getText();
        if (StringUtils.isNotBlank(filePath)) {
            if ( !filePath.endsWith("#")) {
                new Alert(Alert.AlertType.WARNING, "文件目录必须是 # ", null).show();
                System.out.println(retainOrigin.isSelected());
            }else {
                VideoKitService.mainVideoFix(opt, filePath);
                new Alert(AlertType.INFORMATION, "操作完成",  null).show();
            }
        }else {
            new Alert(Alert.AlertType.WARNING, "请先选择文件目录", null).show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileTextField.setText("F:\\迅雷\\#");
    }
}