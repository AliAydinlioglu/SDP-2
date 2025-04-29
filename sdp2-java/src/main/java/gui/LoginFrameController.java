package gui;

import java.io.IOException;

import domain.Gebruiker;
import domain.GebruikerController;
import domain.MachineController;
import domain.OnderhoudController;
import domain.SiteController;
import dto.GebruikerDTO;
import enums.Rol;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
            GebruikerDTO gebruiker = dc.login(email, wachtwoord);
            openMainView(gebruiker);
        } catch (Exception e) {
        	e.printStackTrace();
        	AlertHelper.showError("Login mislukt", e.getMessage());
        	
        }
    }

    
    private void openMainView(GebruikerDTO gebruiker) { 
    	Scene scene = new Scene(new MainFrameController(gebruiker, stage));
    	scene.getStylesheets().add(getClass().getResource("/styles/index.css").toExternalForm());
    	stage.setScene(scene);
    	stage.setFullScreen(true);
    	stage.setTitle("Delaware");
    	stage.show();
    }

}