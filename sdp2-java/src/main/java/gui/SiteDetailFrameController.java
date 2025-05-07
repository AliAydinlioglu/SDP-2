package gui;

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
    private GebruikerDTO ingelogdeGebruikerVoorTerugNavigatie;
    private OnderhoudController onderhoudController;

    public SiteDetailFrameController(SiteController siteController, SiteDTO site, GebruikerDTO ingelogdeGebruiker) {
        this.siteController = siteController;
        this.site = site;
        this.onderhoudController = new OnderhoudController();

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
        btnTerug.setText("Sluiten");
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
    	OnderhoudDTO laatsteOnderhoud = onderhoudController.getLaatsteOnderhoudVanMachine(machine.id());
    	if (laatsteOnderhoud == null) {
            lblLaatsteOnderhoud.setText("Geen voltooid onderhoud gevonden");
            lblAantalDagenOnderhoud.setText("N/A");
        } else {
            lblLaatsteOnderhoud.setText(laatsteOnderhoud.datum().toString());
            long aantalDagen = ChronoUnit.DAYS.between(laatsteOnderhoud.datum(), LocalDate.now());
            lblAantalDagenOnderhoud.setText(String.valueOf(aantalDagen) + " dagen geleden");
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
        btnTerug.setOnAction(event -> {
            Stage stage = (Stage) btnTerug.getScene().getWindow();
            stage.close();
        });

        machineTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            initializeMachineDetails(newSelection);
        });
    }
}
