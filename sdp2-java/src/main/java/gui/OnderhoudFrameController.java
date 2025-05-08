package gui;

import domain.Gebruiker;
import domain.LogController;
import domain.MachineController;
import domain.Onderhoud;
import domain.OnderhoudController;
import domain.SiteController;
import dto.GebruikerDTO;
import dto.OnderhoudDTO;
import dto.SiteDTO;
import enums.OnderhoudStatus;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    @FXML private Button btnTerug;

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
        initializeForm();
        initializeEventListeners();
	}
    
//    public OnderhoudFrameController(OnderhoudController onderhoudController, GebruikerDTO ingelogdeGebruiker, LogController logcontroller) {
//        this.onderhoudController = onderhoudController;
//        this.ingelogdeGebruiker = ingelogdeGebruiker;
//        this.machineController = new MachineController();
//        this.logController = logcontroller;
//
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/OnderhoudFrame.fxml"));
//        loader.setRoot(this);
//        loader.setController(this);
//        try {
//            loader.load();
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
//
//        initializeTable();
//        initializeForm();
//        initializeEventListeners();
//    }

    private void initializeTable() {
        datumCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().datum().toString()));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status().name()));
        techniekerCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().technieker() != null
                        ? cellData.getValue().technieker().voornaam() + " " + cellData.getValue().technieker().achternaam()
                        : "Onbekend"));
        machineCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().machine().naam()));
        
        onderhoudTable.setItems(onderhoudController.getFilteredOnderhoudByUserAndSite(ingelogdeGebruiker, selectedSite.id()));
    }
    
    private void initializeEventListeners() {
		btnTerug.setOnAction(event -> handleBackButton());
	}

	private void initializeForm() {

        btnEdit.setOnAction(event -> editOnderhoud());
        btnDelete.setOnAction(event -> deleteOnderhoud());
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


    @FXML
    private void editOnderhoud() {
        OnderhoudDTO geselecteerd = onderhoudTable.getSelectionModel().getSelectedItem();
        if (geselecteerd != null) {
            try {
            	FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/AddOrEditOnderhoudFrame.fxml"));
                Parent root = loader.load();

                AddOrEditOnderhoudFrameController controller = loader.getController();
                controller.initData(onderhoudController, ingelogdeGebruiker, geselecteerd, machineController, logController); // Geef de geselecteerde machine mee

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
            onderhoudTable.refresh();
            logController.addLog(ingelogdeGebruiker, String.format("Onderhoud met id %d verwijderd", geselecteerd.id()),"");
        } else {
        	AlertHelper.showWarning("Onderhoud niet geselecteerd", "Selecteer een onderhoud om te verwijderen.");
        }
    }
    
//    public List<OnderhoudDTO> getFilteredOnderhouden() {
//        // Onderhouden van de gebruiker
//        List<OnderhoudDTO> onderhoudenGebruiker = onderhoudController.filterByUser(ingelogdeGebruiker);
//        
//        System.out.println(onderhoudenGebruiker);
//
//        // Gemeenschappelijke onderhouden van de laatste 3 maanden en de gebruiker
//        List<OnderhoudDTO> onderhouden3Maanden = onderhoudController.getVoltooideOnderhoudLaatste3Maanden();
//        List<OnderhoudDTO> gemeenschappelijkeOnderhouden = onderhoudenGebruiker.stream()
//                .filter(onderhouden3Maanden::contains)
//                .collect(Collectors.toList());
//        
//        System.out.println(onderhouden3Maanden);
//        System.out.println(gemeenschappelijkeOnderhouden);
//
//        // Onderhouden van laatst voltooid die alleen voorkomen in die van de gebruiker
//        List<OnderhoudDTO> onderhoudenLaatstVoltooid = onderhoudController.getLaatsteVoltooideOnderhoudPerMachine();
//        List<OnderhoudDTO> uniekeOnderhouden = onderhoudenLaatstVoltooid.stream()
//                .filter(onderhoud -> !gemeenschappelijkeOnderhouden.contains(onderhoud))
//                .collect(Collectors.toList());
//        
//        System.out.println(onderhoudenLaatstVoltooid);
//        System.out.println(uniekeOnderhouden);
//
//        // Voeg de unieke onderhouden toe aan de gemeenschappelijke lijst
//        gemeenschappelijkeOnderhouden.addAll(uniekeOnderhouden);
//        
//        System.out.println(gemeenschappelijkeOnderhouden);
//
//        return gemeenschappelijkeOnderhouden;
//    }

}
