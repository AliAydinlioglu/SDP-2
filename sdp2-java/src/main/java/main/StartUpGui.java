package main;

import domain.GebruikerController;
import gui.GebruikersListFrameController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class StartUpGui extends Application {

	@Override
    public void start(Stage primaryStage)
    {
         GebruikerController domainController = new GebruikerController();         
         Scene scene = new Scene(new GebruikersListFrameController(domainController));
         scene.getStylesheets().add("application.css");
         
         primaryStage.setTitle("Address Book"); 
         
         // The stage will not get smaller than its preferred (initial) size.
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
