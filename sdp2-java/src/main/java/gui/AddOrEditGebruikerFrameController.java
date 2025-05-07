package gui;

import java.time.LocalDateTime;

import domain.Adres;
import domain.GebruikerController;
import domain.LogController;
import dto.AdresDTO;
import dto.GebruikerDTO;
import enums.Rol;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import utils.AlertHelper;

public class AddOrEditGebruikerFrameController {

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
    private Label hoofdLabel;

    @FXML
    private Button cancelBtn;
    
    @FXML
    private CheckBox actiefBox;

    private GebruikerController dc;
    private GebruikerDTO bewerkteGebruiker; // null if creating new
    
    private GebruikerDTO ingelogdeGebruiker;
    private LogController logController;

    public void initData(GebruikerController controller, LogController logController, GebruikerDTO ingelogdeGebruiker) {
        initData(controller, null, logController, ingelogdeGebruiker);
    }

    public void initData(GebruikerController controller, GebruikerDTO gebruiker, LogController logController, GebruikerDTO ingelogdeGebruiker) {
        this.dc = controller;
        this.bewerkteGebruiker = gebruiker;
        this.logController = logController;
        this.ingelogdeGebruiker = ingelogdeGebruiker;

        rolBox.getItems().addAll(Rol.values());
        rolBox.setConverter(new StringConverter<Rol>() {
			
			@Override
			public String toString(Rol rol) {
				return rol.toString().toLowerCase();

			}

			@Override
			public Rol fromString(String string) {
				return null;
			}
		});

        if (gebruiker != null) {
            voornaamField.setText(gebruiker.voornaam());
            achternaamField.setText(gebruiker.achternaam());
            emailField.setText(gebruiker.email());
            gsmNrField.setText(gebruiker.gsm());
            straatField.setText(gebruiker.adres().straat());
            huisNrField.setText(gebruiker.adres().huis_nr());
            postcodeField.setText(gebruiker.adres().postcode());
            stadField.setText(gebruiker.adres().stad());
            landField.setText(gebruiker.adres().land());
            geboorteDatumPicker.setValue(gebruiker.geboortedatum());
            rolBox.setValue(gebruiker.rol());
            actiefBox.setSelected(gebruiker.actief());
            

            submitBtn.setText("Opslaan");
            emailField.setDisable(true); // Optional: disable editing email
            
            hoofdLabel.setText("Gebruiker Aanpassen");
        }
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
        boolean actief = actiefBox.isSelected();
        Rol rol = rolBox.getValue();

        try {
            if (bewerkteGebruiker != null) {
                GebruikerDTO bewerkteDTO = new GebruikerDTO(
                		bewerkteGebruiker.id(),
                		voornaam,
                		naam,
                		geboorteDatumPicker.getValue(),
                		new AdresDTO(straat, huisNr, postcode, stad, land),
                		email,
                		gsm,
                		rol,
                		actief
                		);
                dc.updateGebruiker(bewerkteDTO);
                if(!bewerkteDTO.actief()) {
                	logController.addLog(ingelogdeGebruiker, String.format("gebruiker met id %d op non-actief gezet ", bewerkteDTO.id()),"");
                }
                logController.addLog(ingelogdeGebruiker, String.format("gebruiker met id %d aangepast", bewerkteDTO.id()),"");
            } else {
                // CREATE
                dc.addGebruiker(naam, voornaam, geboorteDatumPicker.getValue(),
                        straat, huisNr, postcode, stad, land, email, gsm, rol, actief);
                
                logController.addLog(ingelogdeGebruiker, "gebruiker aangemaakt met email: %s", email);
            }
            ((Stage) submitBtn.getScene().getWindow()).close();

        } catch (Exception e) {
        	e.printStackTrace();
            AlertHelper.showError("Gebruiker opslaan mislukt", e.getMessage());
            return;
        }

        
    }

    @FXML
    void Cancel(ActionEvent event) {
        ((Stage) cancelBtn.getScene().getWindow()).close();
    }
}
