package aed.firematch;

import aed.firematch.ui.LoginController;
import atlantafx.base.theme.NordDark;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FireMatchApp extends Application {

    private LoginController loginController = new LoginController();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(loginController.getLoginRoot());
        Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
        primaryStage.setScene(scene);
        primaryStage.setTitle("FireMatch");
        primaryStage.show();
    }
}
