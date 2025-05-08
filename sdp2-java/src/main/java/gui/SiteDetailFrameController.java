package gui;

import domain.LogController;
import domain.MachineController;
import domain.OnderhoudController;
import domain.SiteController;
import dto.GebruikerDTO;
import dto.MachineDTO;
import dto.OnderhoudDTO;
import dto.SiteDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.AlertHelper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SiteDetailFrameController extends HBox {

    @FXML private Label lblSiteNaam;
    @FXML private Label lblVerantwoordelijke;
    @FXML private Label lblAantalMachines;
    @FXML private TableView<MachineDTO> machineTable;
    @FXML private TableColumn<MachineDTO, String> naamCol;
    @FXML private TableColumn<MachineDTO, String> statusCol;
    @FXML private TableColumn<MachineDTO, String> productiestatusCol;
    @FXML private TableColumn<MachineDTO, String> locatieCol;
    @FXML private Button btnTerug;
    @FXML private Button btnOnderhoud;

    // Labels for machine details
    @FXML private Label lblMachineNaam;
    @FXML private Label lblMachineCode;
    @FXML private Label lblMachineLoc;
    @FXML private Label lblMachineProduct;
    @FXML private Label lblMachineStatus;
    @FXML private Label lblMachineProdStatus;
    @FXML private Label lblMachineUptime;
    @FXML private Label lblMachineTechnieker;
    @FXML private Label lblLaatsteOnderhoud;
    @FXML private Label lblAantalDagenOnderhoud;
    @FXML private Label lblDatumToekomstigOnderhoud;

    private SiteController siteController;
    private SiteDTO selectedSite;
    private SiteOverzichtFrameController siteOverzichtFrameController;
    private OnderhoudController onderhoudController;
    private GebruikerDTO ingelogdeGebruiker;
    private MachineController machineController;
    private LogController logController;

    public SiteDetailFrameController(SiteController siteController, SiteDTO selectedSite, GebruikerDTO ingelogdeGebruiker, LogController logController) {
        this.siteController = siteController;
        this.selectedSite = selectedSite;
        this.onderhoudController = new OnderhoudController();
        this.ingelogdeGebruiker = ingelogdeGebruiker;
        this.machineController = new MachineController();
        this.logController = logController;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SiteDetailFrame.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        initializeSiteDetails();
        initializeMachineTable();
        initializeEventListeners();
    }

    private void initializeSiteDetails() {
        lblSiteNaam.setText(selectedSite.naam());
        lblVerantwoordelijke.setText(selectedSite.verantwoordelijke().voornaam() + " " + selectedSite.verantwoordelijke().achternaam());
        lblAantalMachines.setText(String.valueOf(selectedSite.machines().size()));
    }
    
    private void initializeMachineDetails(MachineDTO machine) {
		lblMachineNaam.setText(machine.naam());
		lblMachineCode.setText(String.valueOf(machine.id()));
		lblMachineLoc.setText(machine.locatie());
//		lblMachineProduct.setText(machine.product().naam());
		lblMachineStatus.setText(machine.status().name());
		lblMachineProdStatus.setText(machine.productieStatus().name());
		lblMachineUptime.setText(String.valueOf(machine.uptime()));
		lblMachineTechnieker.setText(machine.technieker().voornaam() + " " + machine.technieker().achternaam());
		
	}
    
    private void initializeOnderhoudDetails(MachineDTO machine) {
    	OnderhoudDTO onderhoud = onderhoudController.getLaatsteVoltooideOnderhoudVanMachine(machine.id());
    	if (onderhoud == null) {
			lblLaatsteOnderhoud.setText("Geen onderhoud gevonden");
			lblAantalDagenOnderhoud.setText("N/A");
			lblDatumToekomstigOnderhoud.setText("N/A");
			return;
		} else {
	    	lblLaatsteOnderhoud.setText(String.valueOf(onderhoud.datum()));
	    	long aantalDagen = ChronoUnit.DAYS.between(onderhoud.datum(), LocalDate.now());
	        lblAantalDagenOnderhoud.setText(String.valueOf(aantalDagen));
		}

    }

    private void initializeMachineTable() {
        naamCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().naam()));
        locatieCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().locatie()));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status().name()));
        productiestatusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().productieStatus().name()));
        machineTable.setItems(FXCollections.observableArrayList(selectedSite.machines()));
    }

    private void initializeEventListeners() {
        btnTerug.setOnAction(event -> handleBackButton());
        
        machineTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                initializeMachineDetails(newSelection);
                initializeOnderhoudDetails(newSelection);
            }
        });
        
        btnOnderhoud.setOnAction(event -> handleOnderhoudButton());
    }

    private void handleOnderhoudButton() {
        try {
            System.out.println("SiteDetail: " + selectedSite);
            OnderhoudFrameController onderhoudFrameController = new OnderhoudFrameController(onderhoudController, ingelogdeGebruiker, selectedSite, logController);


            // Retrieve the MainFrameController from the current scene
            MainFrameController mainFrame = (MainFrameController) this.getScene().getRoot();

            // Update only the mainView of the MainFrameController
            mainFrame.getMainView().getChildren().setAll(onderhoudFrameController);
        } catch (Exception e) {
            AlertHelper.showError("Could not open the Onderhoud page.", e.getMessage());
            e.printStackTrace();
        }
    }



	private void handleBackButton() {
        try {
            Stage stage = (Stage) this.getScene().getWindow();
            MainFrameController mainFrame = new MainFrameController(selectedSite.verantwoordelijke(), stage);
            Scene currentScene = this.getScene();
            currentScene.setRoot(mainFrame);
        } catch (Exception e) {
            AlertHelper.showError("Could not return to the main screen.", e.getMessage());
            e.printStackTrace();
        }
    }
    
}
