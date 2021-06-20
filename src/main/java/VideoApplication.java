

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import kit.RenameKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;


/**
 * refer to https://github.com/SW-Fantastic/Reader
 */
@SuppressWarnings("ConstantConditions")
public class VideoApplication extends Application {

    public static final String FXML_NAME = "fx.fxml";
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoApplication.class);

    @Override
    public void start(Stage primaryStage) {
        String path = "/src/main/resources/" + FXML_NAME;
        try {
            URL resource = getClass().getResource(path);
            if (resource == null) {
                resource = getClass().getResource(findFxmlFile());
            }
            Parent root = FXMLLoader.load(resource);
            primaryStage.setTitle("和光同尘");
            primaryStage.setScene(new Scene(root, 960, 640));
            primaryStage.show();
        }catch (IOException e) {
            LOGGER.error("FXML Configuration File Path is invalid {}", e.getMessage());
        }
    }

    public static void main(String[] args) {
        VideoApplication.launch(args);
    }

    private String findFxmlFile() {
        String path = getClass().getResource("/").getPath();
        Pair<Boolean, String> result = RenameKit.findSpecificFile(FXML_NAME, path);
        if (result.getKey()) {
            return "/" + result.getValue().substring(path.length()-1).replace('\\', '/');
        }
        LOGGER.error("There is no file named 【{}】 in 【{}】", FXML_NAME, path);
        return null;
    }
}
