package gui;

import domain.GebruikerController;
import enums.Rol;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.AlertHelper;


public class AddGebruikerFrameController {

	@FXML
    private TextField achternaamField;

    @FXML
    private TextField emailField;

    @FXML
    private DatePicker geboorteDatumPicker;

    @FXML
    private TextField gsmNrField;

    @FXML
    private TextField huisNrField;

    @FXML
    private TextField landField;

    @FXML
    private TextField postcodeField;

    @FXML
    private ChoiceBox<Rol> rolBox;

    @FXML
    private TextField stadField;

    @FXML
    private TextField straatField;

    @FXML
    private Button submitBtn;

    @FXML
    private TextField voornaamField;
    
    @FXML
    private Button cancelBtn;
    
    private GebruikerController dc;

    public void initData(GebruikerController controller) {
        this.dc = controller;
        rolBox.setItems(FXCollections.observableArrayList(Rol.values()));
    }

    @FXML
    private void addGebruiker() {
		String naam = achternaamField.getText();
		String voornaam = voornaamField.getText();
		String email = emailField.getText();
		String gsm = gsmNrField.getText();
		String straat = straatField.getText();
		String huisNr = huisNrField.getText();
		String postcode = postcodeField.getText();
		String stad = stadField.getText();
		String land = landField.getText();
		Rol rol = rolBox.getValue();
		
		
	
		try {
			dc.addGebruiker(naam, voornaam, geboorteDatumPicker.getValue(), straat, huisNr, postcode, stad, land, email, gsm, rol);

		} catch (Exception e) {
			AlertHelper.showError("Gebruiker maken mislukt", e.getMessage());
		} finally {
			((Stage) submitBtn.getScene().getWindow()).close(); 
		}
		
	}
    
    @FXML
    void Cancel(ActionEvent event) {
    	((Stage) voornaamField.getScene().getWindow()).close();
    }
}
