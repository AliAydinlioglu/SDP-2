package gui;

import domain.Gebruiker;
import domain.Onderhoud;
import domain.OnderhoudController;
import dto.GebruikerDTO;
import dto.OnderhoudDTO;
import enums.OnderhoudStatus;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import utils.AlertHelper;

import java.io.IOException;
import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OnderhoudFrameController extends VBox {
	
	private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML private TableView<OnderhoudDTO> onderhoudTable;
    @FXML private TableColumn<OnderhoudDTO, String> datumCol;
    @FXML private TableColumn<OnderhoudDTO, String> statusCol;
    @FXML private TableColumn<OnderhoudDTO, String> techniekerCol;
    @FXML private TableColumn<OnderhoudDTO, String> machineCol;

    @FXML private TextField txtReden;
    @FXML private TextField txtRapport;
    @FXML private TextArea txtOpmerkingen;
    @FXML private ComboBox<OnderhoudStatus> statusBox;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private OnderhoudController onderhoudController;
    private GebruikerDTO ingelogdeGebruiker;

    public OnderhoudFrameController(OnderhoudController onderhoudController, GebruikerDTO ingelogdeGebruiker) {
        this.onderhoudController = onderhoudController;
        this.ingelogdeGebruiker = ingelogdeGebruiker;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/OnderhoudFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        initializeTable();
        initializeForm();
    }

    private void initializeTable() {
        datumCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().datum().toString()));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status().name()));
        techniekerCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().technieker() != null
                        ? cellData.getValue().technieker().voornaam() + " " + cellData.getValue().technieker().achternaam()
                        : "Onbekend"));
        machineCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                "Machine ID: " + cellData.getValue().machineId()));

        onderhoudTable.setItems(onderhoudController.filterByRole(ingelogdeGebruiker));
    }

    private void initializeForm() {
        statusBox.getItems().addAll(OnderhoudStatus.values());

        btnAdd.setOnAction(event -> addOnderhoud());
        btnEdit.setOnAction(event -> editOnderhoud());
        btnDelete.setOnAction(event -> deleteOnderhoud());
    }

    private void addOnderhoud() {
        try {
        	String rapportJson = objectMapper.writeValueAsString(txtRapport.getText()); // Serialize to JSON
            Onderhoud nieuwOnderhoud = new Onderhoud(
            	LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                ingelogdeGebruiker.id(),
                txtReden.getText(),
                rapportJson, // Use serialized JSON
                txtOpmerkingen.getText(),
                statusBox.getValue(),
                1 // Machine ID (example)
            );
            onderhoudController.registerOnderhoud(nieuwOnderhoud);
//            // Voeg het nieuwe onderhoud toe aan de originele lijst in de controller
//            onderhoudController.getAllOnderhoud().add(nieuwOnderhoud);
        } catch (Exception e) {
        	AlertHelper.showError("Onderhoud toevoegen mislukt", e.getMessage());
        }
    }


    private void editOnderhoud() {
        OnderhoudDTO geselecteerd = onderhoudTable.getSelectionModel().getSelectedItem();
        if (geselecteerd != null) {
            try {
//                geselecteerd.setReden(txtReden.getText());
//                geselecteerd.setRapport(txtRapport.getText());
//                geselecteerd.setOpmerkingen(txtOpmerkingen.getText());
//                geselecteerd.setStatus(statusBox.getValue());
            	  OnderhoudDTO updatedOnderhoud = new OnderhoudDTO(
						  geselecteerd.id(),
						  geselecteerd.datum(),
						  geselecteerd.startTijd(),
						  geselecteerd.eindTijd(),
						  txtReden.getText(),
						  txtRapport.getText(),
						  txtOpmerkingen.getText(),
						  statusBox.getValue(),
						  geselecteerd.machineId(),
						  geselecteerd.technieker()
				  );
            	  onderhoudController.updateOnderhoud(updatedOnderhoud);
            	  onderhoudTable.refresh();
            } catch (Exception e) {
            	AlertHelper.showError("Onderhoud bewerken mislukt", e.getMessage());
            }
        } else {
        	AlertHelper.showWarning("Onderhoud niet geselecteerd", "Selecteer een onderhoud om te bewerken.");
        }
    }

    private void deleteOnderhoud() {
        OnderhoudDTO geselecteerd = onderhoudTable.getSelectionModel().getSelectedItem();
        if (geselecteerd != null) {
            onderhoudController.deleteOnderhoud(geselecteerd);
            onderhoudTable.getItems().remove(geselecteerd);
        } else {
        	AlertHelper.showWarning("Onderhoud niet geselecteerd", "Selecteer een onderhoud om te verwijderen.");
        }
    }

}
