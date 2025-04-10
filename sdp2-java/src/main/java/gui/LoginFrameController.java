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
import utils.AlertHelper;

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
    
    @FXML
    private Label emailError;
    
    @FXML
    private Label wachtwoordError;

    
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

        // Clear old errors
        emailError.setText("");
        wachtwoordError.setText("");

        if (email == null || email.isBlank()) {
            emailError.setText("Vul een email in");
            return;
        } else if (!email.matches("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")) {
            emailError.setText("Ongeldige email");
            return;
        }

        if (wachtwoord == null || wachtwoord.isBlank()) {
            wachtwoordError.setText("Vul een wachtwoord in");
            return;
        }

        try {
            Gebruiker gebruiker = dc.login(email, wachtwoord);
            if (gebruiker != null) {
                System.out.println("Login succesvol");
                openMainView();
            } else {
                System.out.println("Login mislukt");
            }
        } catch (Exception e) {
        	AlertHelper.showError("Login mislukt", "Controleer uw email en wachtwoord.");
        }
    }

    
    private void openMainView() {
        SiteController siteController = new SiteController();
        MachineController machineController = new MachineController();
        OnderhoudController onderhoudController = new OnderhoudController();
        
        GebruikersListFrameController gebruikersView = new GebruikersListFrameController(dc);
        SiteOverzichtFrameController siteView = new SiteOverzichtFrameController(siteController);
        MachineListFrameController machineView = new MachineListFrameController(machineController);
        OnderhoudFrameController onderhoudView = new OnderhoudFrameController(onderhoudController);
        
        TabPane tabPane = new TabPane();

        Tab gebruikersTab = new Tab("Gebruikers", gebruikersView);
        gebruikersTab.setClosable(false);

        Tab sitesTab = new Tab("Sites", siteView);
        sitesTab.setClosable(false);

        Tab machineTab = new Tab("Machines", machineView);
        machineTab.setClosable(false);
        
        Tab onderhoudTab = new Tab("Onderhoud", onderhoudView);
        onderhoudTab.setClosable(false);

        tabPane.getTabs().addAll(gebruikersTab, sitesTab, machineTab, onderhoudTab);

        Scene mainScene = new Scene(tabPane, 800, 600);
        stage.setTitle("Beheer Applicatie");
        stage.setScene(mainScene);
    }

}