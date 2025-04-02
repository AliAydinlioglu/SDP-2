package main;

import domain.GebruikerController;
import domain.SiteController;
import gui.GebruikersListFrameController;
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
    public void start(Stage primaryStage)
    {
        GebruikerController gebruikerController = new GebruikerController();
        SiteController siteController = new SiteController();

        GebruikersListFrameController gebruikersView = new GebruikersListFrameController(gebruikerController);
        SiteOverzichtFrameController siteOverzichtView = new SiteOverzichtFrameController(siteController);

        TabPane tabPane = new TabPane();

        Tab gebruikersTab = new Tab("Gebruikers", gebruikersView);
        gebruikersTab.setClosable(false);

        Tab sitesTab = new Tab("Sites", siteOverzichtView);
        sitesTab.setClosable(false);

        tabPane.getTabs().addAll(gebruikersTab, sitesTab);
        Scene scene = new Scene(tabPane, 800, 600);
        //scene.getStylesheets().add("application.css");

        primaryStage.setTitle("Beheer Applicatie");

        primaryStage.setOnShown((WindowEvent t) -> {
            primaryStage.setMinWidth(primaryStage.getWidth());
            primaryStage.setMinHeight(primaryStage.getHeight());
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void start(String[] args)
    {
        launch(args);
    }
}