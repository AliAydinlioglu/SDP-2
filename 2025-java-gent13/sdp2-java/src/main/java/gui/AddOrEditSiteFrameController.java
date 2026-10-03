package gui;

import java.util.stream.Collectors;

import domain.GebruikerController;
import domain.LogController;
import domain.SiteController;
import dto.GebruikerDTO;
import enums.Rol;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import utils.AlertHelper;

public class AddOrEditSiteFrameController {

    @FXML private Label hoofdLabel; // Kan nu altijd "Nieuwe Site Toevoegen" zijn
    @FXML private TextField naamField;
    @FXML private ComboBox<GebruikerDTO> verantwoordelijkeBox;
    @FXML private Button cancelBtn;
    @FXML private Button submitBtn; // Tekst kan altijd "Toevoegen" zijn
    @FXML private Label errorLabel;

    private SiteController siteController;
    private GebruikerController gebruikerController;
    private LogController logController;
    private GebruikerDTO ingelogdeGebruiker;

    /**
     * Initialiseert de controller voor het toevoegen van een nieuwe site.
     */
    // Naam aangepast, alleen initData is nu voldoende.
    public void initData(SiteController siteController, GebruikerController gebruikerController, LogController logController, GebruikerDTO ingelogdeGebruiker) {
        this.siteController = siteController;
        this.gebruikerController = gebruikerController;
        this.logController = logController;
        this.ingelogdeGebruiker = ingelogdeGebruiker;

        hoofdLabel.setText("Nieuwe Site Toevoegen");
        submitBtn.setText("Toevoegen");

        vulVerantwoordelijkeComboBox();
    }

    private void vulVerantwoordelijkeComboBox() {
        if (gebruikerController == null) {
            errorLabel.setText("Fout: GebruikerController niet beschikbaar.");
            return;
        }

        ObservableList<GebruikerDTO> alleGebruikers = gebruikerController.findAll();

        // Filter op rollen die verantwoordelijk mogen zijn (Pas deze lijst aan indien nodig)
        ObservableList<GebruikerDTO> mogelijkeVerantwoordelijken = alleGebruikers.stream()
                .filter(g -> g.actief() &&
                        (g.rol() == Rol.VERANTWOORDELIJKE || g.rol() == Rol.MANAGER))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        verantwoordelijkeBox.setItems(mogelijkeVerantwoordelijken);

        verantwoordelijkeBox.setConverter(new StringConverter<GebruikerDTO>() {
            @Override
            public String toString(GebruikerDTO gebruiker) {
                return (gebruiker == null) ? null : gebruiker.voornaam() + " " + gebruiker.achternaam();
            }
            @Override
            public GebruikerDTO fromString(String string) { return null; }
        });

        verantwoordelijkeBox.setCellFactory(new Callback<ListView<GebruikerDTO>, ListCell<GebruikerDTO>>() {
            @Override
            public ListCell<GebruikerDTO> call(ListView<GebruikerDTO> l) {
                return new ListCell<GebruikerDTO>() {
                    @Override
                    protected void updateItem(GebruikerDTO gebruiker, boolean empty) {
                        super.updateItem(gebruiker, empty);
                        if (gebruiker == null || empty) {
                            setText(null);
                        } else {
                            setText(gebruiker.voornaam() + " " + gebruiker.achternaam() + " (" + gebruiker.email() + ")");
                        }
                    }
                };
            }
        });
    }

    @FXML
    void handleOpslaan(ActionEvent event) {
        String naam = naamField.getText();
        GebruikerDTO geselecteerdeVerantwoordelijke = verantwoordelijkeBox.getValue();

        if (naam == null || naam.trim().isEmpty()) {
            errorLabel.setText("Site naam mag niet leeg zijn.");
            return;
        }
        if (geselecteerdeVerantwoordelijke == null) {
            errorLabel.setText("Selecteer een verantwoordelijke.");
            return;
        }

        try {
            siteController.addSite(naam, geselecteerdeVerantwoordelijke, ingelogdeGebruiker);
            AlertHelper.showInfo("Succes", "Site '" + naam + "' succesvol toegevoegd.");
            closeStage();

        } catch (RuntimeException e) {
            errorLabel.setText("Fout bij opslaan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleAnnuleren(ActionEvent event) {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }
}