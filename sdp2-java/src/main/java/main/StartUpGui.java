package main;

import domain.GebruikerController;
import domain.MachineController;
import domain.SiteController;
import gui.GebruikersListFrameController;
import gui.LoginFrameController;
import gui.MachineListFrameController;
import gui.SiteOverzichtFrameController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;         // Import Tab
import javafx.scene.control.TabPane;     // Import TabPane
import javafx.scene.layout.BorderPane; // Optioneel, voor algemene layout
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class StartUpGui extends Application {

    @Override
    public void start(Stage primaryStage) {
    	GebruikerController gebruikerController = new GebruikerController();

        LoginFrameController loginView = new LoginFrameController(gebruikerController, primaryStage);

        Scene loginScene = new Scene(loginView, 400, 300); // You can change width/height as needed
        primaryStage.setTitle("Login");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public static void start(String[] args) {
        launch(args);
    }
}