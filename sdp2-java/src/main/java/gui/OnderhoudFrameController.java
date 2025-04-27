package gui;

import domain.Gebruiker;
import domain.MachineController;
import domain.Onderhoud;
import domain.OnderhoudController;
import dto.GebruikerDTO;
import dto.OnderhoudDTO;
import enums.OnderhoudStatus;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.AlertHelper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OnderhoudFrameController extends VBox {
	
	private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = Logger.getLogger(OnderhoudFrameController.class.getName());

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
    private MachineController machineController;
    
    public OnderhoudFrameController(OnderhoudController onderhoudController, GebruikerDTO ingelogdeGebruiker, MachineController machineController) {
        this.onderhoudController = onderhoudController;
        this.ingelogdeGebruiker = ingelogdeGebruiker;
        this.machineController = machineController;

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

        onderhoudTable.setItems(onderhoudController.filterByUser(ingelogdeGebruiker));
    }

    private void initializeForm() {

        btnEdit.setOnAction(event -> editOnderhoud());
        btnDelete.setOnAction(event -> deleteOnderhoud());
    }

    @FXML
    private void editOnderhoud() {
        OnderhoudDTO geselecteerd = onderhoudTable.getSelectionModel().getSelectedItem();
        if (geselecteerd != null) {
            try {
            	FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/AddOrEditOnderhoudFrame.fxml"));
                Parent root = loader.load();

                AddOrEditOnderhoudFrameController controller = loader.getController();
                controller.initData(onderhoudController, ingelogdeGebruiker, geselecteerd, machineController); // Geef de geselecteerde machine mee

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Onderhoud Toevoegen");
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setScene(new Scene(root));
                dialogStage.showAndWait();
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
