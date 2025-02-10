package aed.firematch.app;

import aed.firematch.ui.controllers.LoginController;
import atlantafx.base.theme.NordDark;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FireMatchApp extends Application {

    private LoginController loginController = new LoginController();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(loginController.getLoginRoot());
        Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("FireMatch");
        primaryStage.show();
    }
}
