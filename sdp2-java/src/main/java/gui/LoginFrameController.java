package gui;

import java.io.IOException;

import domain.Gebruiker;
import domain.GebruikerController;
import domain.MachineController;
import domain.SiteController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginFrameController extends AnchorPane {
	
	@FXML
    private Label LoginLabel;

    @FXML
    private TextField emailField;

    @FXML
    private Label emailLabel;

    @FXML
    private Button submitBtn;

    @FXML
    private PasswordField wachtwoordField;

    @FXML
    private Label wachtwoordLabel;
    
    private GebruikerController dc;
    private Stage stage;
    
    public LoginFrameController(GebruikerController dc, Stage stage) {
    	this.dc = dc;
    	this.stage = stage;
    	
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LoginFrame.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
    }
    
    

    @FXML
    void LogIn(ActionEvent event) {
    	
    	String email = emailField.getText();
		String wachtwoord = wachtwoordField.getText();
		Gebruiker gebruiker = dc.login(email, wachtwoord);
		
		if (gebruiker != null) {
			// Login succesvol, ga naar de volgende view
			// Hier kan je de code toevoegen om de volgende view te laden
			System.out.println("Login succesvol");
			openMainView();
			
			// Sluit het login venster
			//stage.close();
			
		} else {
			// Login mislukt, geef een foutmelding weer
			System.out.println("Login mislukt");
		}

    }
    
    private void openMainView() {
        SiteController siteController = new SiteController();
        MachineController machineController = new MachineController();

        GebruikersListFrameController gebruikersView = new GebruikersListFrameController(dc);
        SiteOverzichtFrameController siteView = new SiteOverzichtFrameController(siteController);
        MachineListFrameController machineView = new MachineListFrameController(machineController);

        TabPane tabPane = new TabPane();

        Tab gebruikersTab = new Tab("Gebruikers", gebruikersView);
        gebruikersTab.setClosable(false);

        Tab sitesTab = new Tab("Sites", siteView);
        sitesTab.setClosable(false);

        Tab machineTab = new Tab("Machines", machineView);
        machineTab.setClosable(false);

        tabPane.getTabs().addAll(gebruikersTab, sitesTab, machineTab);

        Scene mainScene = new Scene(tabPane, 800, 600);
        stage.setTitle("Beheer Applicatie");
        stage.setScene(mainScene);
    }

}
