

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Pair;
import kit.RenameKit;
import kit.ThreadPoolKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;


/**
 * refer to https://github.com/SW-Fantastic/Reader
 */
@SuppressWarnings("ConstantConditions")
public class JavaFxApplication extends Application {

    public static final String FXML_NAME = "fx.fxml";
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaFxApplication.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            URL resource = getClass().getResource(FXML_NAME);
            if (resource == null) {
                resource = getClass().getResource(findFxmlFile());
            }
            Parent root = FXMLLoader.load(resource);
            primaryStage.setTitle("和光同尘 v1.4");
            primaryStage.setScene(new Scene(root, 960, 640));
            primaryStage.show();
            //shutdown threadPool when application intend to close
            try {
                primaryStage.setOnCloseRequest(event -> ThreadPoolKit.getInstance().shutDown());
            }catch (Exception e) {
                LOGGER.error(e.getMessage());
                e.printStackTrace();
            }
        }catch (IOException e) {
            LOGGER.error("FXML Configuration File Path is invalid {}", e.getMessage());
        }
    }

    public static void main(String[] args) {
        JavaFxApplication.launch(args);
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
