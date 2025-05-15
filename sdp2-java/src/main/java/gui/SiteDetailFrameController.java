package gui;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import utils.AlertHelper;

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
    private SiteDTO site;
    private OnderhoudController onderhoudController;
    private GebruikerDTO ingelogdeGebruiker;
    private MachineController machineController;
    private LogController logController;

    public SiteDetailFrameController(SiteController siteController, SiteDTO selectedSite, GebruikerDTO ingelogdeGebruiker, LogController logController) {
        this.siteController = siteController;
        this.site = selectedSite;
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
        initializeMachineTableColumns();
        initializeEventListeners();
    }

    private void initializeSiteDetails() {
        if (site != null) {
            lblSiteNaam.setText(site.naam() != null ? site.naam() : "N/B");

            String verantwNaam = "Niet toegewezen";
            if (site.verantwoordelijke() != null) {
                verantwNaam = site.verantwoordelijke().voornaam() + " " + site.verantwoordelijke().achternaam();
            }
            lblVerantwoordelijke.setText(verantwNaam);

            int aantalMachines = (site.machines() != null) ? site.machines().size() : 0;
            lblAantalMachines.setText(String.valueOf(aantalMachines));

            if (site.machines() != null) {
                machineTable.setItems(FXCollections.observableArrayList(site.machines()));
            } else {
                machineTable.setItems(FXCollections.observableArrayList());
            }
        } else {
            lblSiteNaam.setText("N/B");
            lblVerantwoordelijke.setText("N/B");
            lblAantalMachines.setText("N/B");
            machineTable.setItems(FXCollections.observableArrayList());
        }
    }

    private void initializeMachineDetails(MachineDTO machine) {
        if (machine == null) {
            lblMachineNaam.setText(""); lblMachineCode.setText(""); lblMachineLoc.setText("");
            lblMachineProduct.setText(""); lblMachineStatus.setText(""); lblMachineProdStatus.setText("");
            lblMachineUptime.setText(""); lblMachineTechnieker.setText("");
            lblLaatsteOnderhoud.setText(""); lblAantalDagenOnderhoud.setText(""); lblDatumToekomstigOnderhoud.setText("");
            return;
        }
        lblMachineNaam.setText(machine.naam() != null ? machine.naam() : "N/B");
        lblMachineCode.setText(String.valueOf(machine.id()));
        lblMachineLoc.setText(machine.locatie() != null ? machine.locatie() : "N/B");
        lblMachineProduct.setText(machine.productInfo() != null ? machine.productInfo() : "N/B");
        lblMachineStatus.setText(machine.status() != null ? machine.status().name() : "N/B");
        lblMachineProdStatus.setText(machine.productieStatus() != null ? machine.productieStatus().name() : "N/B");
        lblMachineUptime.setText(String.valueOf(machine.uptime()));

        if (machine.technieker() != null) {
            lblMachineTechnieker.setText(machine.technieker().voornaam() + " " + machine.technieker().achternaam());
        } else {
            lblMachineTechnieker.setText("Niet toegewezen");
        }

        initializeOnderhoudDetails(machine);
        lblDatumToekomstigOnderhoud.setText(machine.volgendOnderhoud() != null ? machine.volgendOnderhoud().toString() : "Niet gepland");
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

    private void initializeMachineTableColumns() {
        naamCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().naam()));
        locatieCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().locatie()));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status().name()));
        productiestatusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().productieStatus().name()));
        machineTable.setItems(FXCollections.observableArrayList(site.machines()));
    }

    private void initializeEventListeners() {
        btnTerug.setOnAction(event -> handleBackButton());

        machineTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            initializeMachineDetails(newSelection);
        });
        
        btnOnderhoud.setOnAction(event -> handleOnderhoudButton());
    }

    private void handleOnderhoudButton() {
        try {
            System.out.println("SiteDetail: " + site);
            OnderhoudFrameController onderhoudFrameController = new OnderhoudFrameController(new OnderhoudController(), ingelogdeGebruiker, site, logController);


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
            MainFrameController mainFrame = new MainFrameController(ingelogdeGebruiker, stage);
            Scene currentScene = this.getScene();
            currentScene.setRoot(mainFrame);
        } catch (Exception e) {
            AlertHelper.showError("Could not return to the main screen.", e.getMessage());
            e.printStackTrace();
        }
    }
    
}
