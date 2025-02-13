package aed.firematch.app;

import aed.firematch.ui.controllers.LoginController;
import atlantafx.base.theme.CupertinoLight;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FireMatchApp extends Application {

    private LoginController loginController = new LoginController();



    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(loginController.getLoginRoot());
        Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
        primaryStage.setScene(scene);
        primaryStage.setTitle("FireMatch");
        primaryStage.show();
    }
}
