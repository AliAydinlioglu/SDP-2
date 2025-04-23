package gui;

import domain.Adres;
import domain.GebruikerController;
import dto.GebruikerDTO;
import enums.Rol;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
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

    public void initData(GebruikerController controller) {
        initData(controller, null);
        rolBox.setItems(FXCollections.observableArrayList(Rol.values()));
    }

    public void initData(GebruikerController controller, GebruikerDTO gebruiker) {
        this.dc = controller;
        this.bewerkteGebruiker = gebruiker;

        rolBox.setItems(FXCollections.observableArrayList(Rol.values()));

        if (gebruiker != null) {
            voornaamField.setText(gebruiker.voornaam());
            achternaamField.setText(gebruiker.achternaam());
            emailField.setText(gebruiker.email());
            gsmNrField.setText(gebruiker.gsm());
            straatField.setText(gebruiker.adres().getStraat());
            huisNrField.setText(gebruiker.adres().getHuis_nr());
            postcodeField.setText(gebruiker.adres().getPostcode());
            stadField.setText(gebruiker.adres().getStad());
            landField.setText(gebruiker.adres().getLand());
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
                		new Adres(straat, huisNr, postcode, stad, land),
                		email,
                		gsm,
                		rol,
                		actief
                		);
                dc.updateGebruiker(bewerkteDTO);
            } else {
                // CREATE
                dc.addGebruiker(naam, voornaam, geboorteDatumPicker.getValue(),
                        straat, huisNr, postcode, stad, land, email, gsm, rol, actief);
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
