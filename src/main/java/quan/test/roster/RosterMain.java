package quan.test.roster;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * JavaFX测试样例:员工名册
 * Created by quanchangnai on 2020/5/11.
 */
public class RosterMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane rootPane = FXMLLoader.load(RosterMain.class.getResource("roster.fxml"));
        primaryStage.setScene(new Scene(rootPane));
        setUserAgentStylesheet(STYLESHEET_MODENA);
        primaryStage.show();
    }

}
