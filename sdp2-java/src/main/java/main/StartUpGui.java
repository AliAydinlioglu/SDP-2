package main;

import domain.GebruikerController;
import gui.LoginFrameController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartUpGui extends Application {

    @Override
    public void start(Stage primaryStage) {
    	GebruikerController gebruikerController = new GebruikerController();

        LoginFrameController loginView = new LoginFrameController(gebruikerController, primaryStage);

        Scene loginScene = new Scene(loginView); // You can change width/height as needed
        primaryStage.setTitle("Login");
        primaryStage.setScene(loginScene);
        primaryStage.setFullScreen(true);
        
//        primaryStage.setOnCloseRequest((WindowEvent event) -> {
//			System.out.println("Application closed");
//			System.exit(0);
//		});
//        
        primaryStage.show();
    }

    public static void start(String[] args) {
        launch(args);
    }
}