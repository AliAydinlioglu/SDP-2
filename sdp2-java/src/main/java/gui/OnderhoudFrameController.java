package gui;

import java.io.IOException;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import domain.LogController;
import domain.MachineController;
import domain.OnderhoudController;
import domain.SiteController;
import dto.GebruikerDTO;
import dto.OnderhoudDTO;
import dto.SiteDTO;
import enums.OnderhoudStatus;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import utils.AlertHelper;

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
    @FXML private Button btnTerug;
    @FXML private CheckBox laatsteCB;
    @FXML private CheckBox minderDanDrieMaandenCB;
    @FXML private ComboBox<OnderhoudStatus> statusFilterBox;

    private OnderhoudController onderhoudController;
    private GebruikerDTO ingelogdeGebruiker;
    private MachineController machineController;
    private LogController logController;
	private SiteDTO selectedSite;
	
    
    public OnderhoudFrameController(OnderhoudController onderhoudController, GebruikerDTO ingelogdeGebruiker, SiteDTO selectedSite, LogController logcontroller) {
    	this.onderhoudController = onderhoudController;
        this.ingelogdeGebruiker = ingelogdeGebruiker;
        this.machineController = new MachineController();
        this.logController = logcontroller;
        this.selectedSite = selectedSite;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/OnderhoudFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        initializeTable();
        initializeEventListeners();
	}

    private void initializeTable() {
        datumCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().datum().toString()));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status().name()));
        techniekerCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().technieker() != null
                        ? cellData.getValue().technieker().voornaam() + " " + cellData.getValue().technieker().achternaam()
                        : "Onbekend"));
        machineCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().machine().naam()));
        
        onderhoudTable.setItems(onderhoudController.getAllOnderhoud());
    }
    
    private void initializeEventListeners() {
        btnTerug.setOnAction(event -> handleBackButton());
        btnEdit.setOnAction(event -> editOnderhoud());
        btnDelete.setOnAction(event -> deleteOnderhoud());
        
        onderhoudTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click
                OnderhoudDTO selectedOnderhoud = onderhoudTable.getSelectionModel().getSelectedItem();
                if (selectedOnderhoud != null) {
                    openOnderhoudDetail(selectedOnderhoud);
                }
            }
        });

        laatsteCB.setSelected(true);
        minderDanDrieMaandenCB.setSelected(true);

        statusFilterBox.getItems().add(null); // Add "All Statuses" option
        statusFilterBox.getItems().addAll(OnderhoudStatus.values());
        statusFilterBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(OnderhoudStatus status) {
                return status == null ? "Alle Statussen" : status.name();
            }

            @Override
            public OnderhoudStatus fromString(String string) {
                return null; // Not needed
            }
        });
        statusFilterBox.setValue(OnderhoudStatus.VOLTOOID); // Default to "All Statuses"
        statusFilterBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        laatsteCB.selectedProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        minderDanDrieMaandenCB.selectedProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        applyFilters();
    }

    private void handleBackButton() {
    	try {
            // Retrieve the MainFrameController from the current scene
            MainFrameController mainFrame = (MainFrameController) this.getScene().getRoot();

            // Navigate back to the SiteDetailFrameController
            SiteDetailFrameController siteDetailFrame = new SiteDetailFrameController(
                new SiteController(), 
                selectedSite, 
                ingelogdeGebruiker, 
                logController
            );
            mainFrame.getMainView().getChildren().setAll(siteDetailFrame);
        } catch (Exception e) {
            AlertHelper.showError("Could not navigate back.", e.getMessage());
            e.printStackTrace();
        }
	}
    
    private void openOnderhoudDetail(OnderhoudDTO onderhoud) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/OnderhoudDetailFrame.fxml"));
            Parent root = loader.load();

            OnderhoudDetailFrameController controller = loader.getController();
            controller.initData(onderhoud, onderhoudController, logController, ingelogdeGebruiker);

            Stage detailStage = new Stage();
            detailStage.setTitle("Onderhoud Details");
            detailStage.initModality(Modality.APPLICATION_MODAL);
            detailStage.initOwner(this.getScene().getWindow());
            detailStage.setScene(new Scene(root));
            detailStage.setResizable(false);
            detailStage.showAndWait();
        } catch (IOException e) {
            AlertHelper.showError("Fout", "Kan onderhoud details niet openen: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void editOnderhoud() {
        OnderhoudDTO geselecteerd = onderhoudTable.getSelectionModel().getSelectedItem();
        if (geselecteerd == null) {
            AlertHelper.showWarning("Geen onderhoud geselecteerd", "Selecteer een onderhoud om te bewerken.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/AddOrEditOnderhoudFrame.fxml"));
            Parent root = loader.load();

            AddOrEditOnderhoudFrameController controller = loader.getController();
            controller.initData(onderhoudController, ingelogdeGebruiker, geselecteerd, machineController, logController);

            Stage dialog = new Stage();
            dialog.setTitle("Onderhoud Bewerken");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(this.getScene().getWindow());
            dialog.setScene(new Scene(root));
            dialog.setResizable(false);
            dialog.showAndWait();

            onderhoudTable.refresh();
            applyFilters();
        } catch (IOException e) {
            AlertHelper.showError("Fout", "Kan onderhoud niet bewerken: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void deleteOnderhoud() {
        OnderhoudDTO geselecteerd = onderhoudTable.getSelectionModel().getSelectedItem();
        if (geselecteerd != null) {
            onderhoudController.deleteOnderhoud(geselecteerd);
            onderhoudTable.refresh();
            logController.addLog(ingelogdeGebruiker, String.format("Onderhoud met id %d verwijderd", geselecteerd.id()),"");
            applyFilters();
        } else {
        	AlertHelper.showWarning("Onderhoud niet geselecteerd", "Selecteer een onderhoud om te verwijderen.");
        }
    }
    
    private void applyFilters() {
        boolean laatste = laatsteCB.isSelected();
        boolean minderDanDrieMaanden = minderDanDrieMaandenCB.isSelected();
        OnderhoudStatus selectedStatus = statusFilterBox.getValue();

        onderhoudTable.setItems(onderhoudController.filterOnderhoud(
            laatste, minderDanDrieMaanden, selectedStatus, ingelogdeGebruiker, selectedSite.id()
        ));
    }

}
