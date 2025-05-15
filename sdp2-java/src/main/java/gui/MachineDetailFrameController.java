package gui;

import domain.LogController;
import domain.MachineController;
import domain.OnderhoudController;
import dto.GebruikerDTO;
import dto.MachineDTO;
import dto.OnderhoudDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import utils.AlertHelper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class MachineDetailFrameController extends HBox { // Adjust layout type if needed

    @FXML
    private Label lblMachineNaam;
    @FXML
    private Label lblMachineCode; // Assuming this is machine ID
    @FXML
    private Label lblMachineLoc;
    @FXML
    private Label lblMachineProduct;
    @FXML
    private Label lblMachineStatus;
    @FXML
    private Label lblMachineProdStatus;
    @FXML
    private Label lblMachineUptime;
    @FXML
    private Label lblMachineTechnieker;
    @FXML
    private Label lblVolgendOnderhoud;
    @FXML
    private Label lblDagenSindsLaatsteOnderhoud;

    @FXML
    private TableView<OnderhoudDTO> onderhoudTable;
    @FXML
    private TableColumn<OnderhoudDTO, String> onderhoudDatumCol;
    @FXML
    private TableColumn<OnderhoudDTO, String> onderhoudStatusCol;
    @FXML
    private TableColumn<OnderhoudDTO, String> onderhoudTechniekerCol;
    @FXML
    private TableColumn<OnderhoudDTO, String> onderhoudRedenCol;

    @FXML
    private Button btnTerug;

    private MachineController machineController;
    private OnderhoudController onderhoudController;
    private MachineDTO selectedMachine;
    private GebruikerDTO ingelogdeGebruiker;
    private LogController logController;

    public MachineDetailFrameController(MachineController machineController, OnderhoudController onderhoudController,
                                        MachineDTO selectedMachine, GebruikerDTO ingelogdeGebruiker, LogController logController) {
        this.machineController = machineController;
        this.onderhoudController = onderhoudController;
        this.selectedMachine = selectedMachine;
        this.ingelogdeGebruiker = ingelogdeGebruiker;
        this.logController = logController;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MachineDetailFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load MachineDetailFrame.fxml: " + ex.getMessage(), ex);
        }

        initializeMachineDetails();
        initializeOnderhoudTable();
        initializeEventListeners();
    }

    private void initializeMachineDetails() {
        if (selectedMachine != null) {
            lblMachineNaam.setText(selectedMachine.naam() != null ? selectedMachine.naam() : "N/B");
            lblMachineCode.setText(String.valueOf(selectedMachine.id()));
            lblMachineLoc.setText(selectedMachine.locatie() != null ? selectedMachine.locatie() : "N/B");
            lblMachineProduct.setText(selectedMachine.productInfo() != null ? selectedMachine.productInfo() : "N/B");
            lblMachineStatus.setText(selectedMachine.status() != null ? selectedMachine.status().name() : "N/B");
            lblMachineProdStatus.setText(
                    selectedMachine.productieStatus() != null ? selectedMachine.productieStatus().name() : "N/B");
            lblMachineUptime.setText(String.valueOf(selectedMachine.uptime()));
            lblDagenSindsLaatsteOnderhoud.setText(
                    selectedMachine.dagenSindsOnderhoud() >= 0 ? String.valueOf(selectedMachine.dagenSindsOnderhoud())
                            : "N/B");
            if (selectedMachine.technieker() != null) {
                lblMachineTechnieker.setText(
                        selectedMachine.technieker().voornaam() + " " + selectedMachine.technieker().achternaam());
            } else {
                lblMachineTechnieker.setText("Niet toegewezen");
            }
            lblVolgendOnderhoud.setText(selectedMachine.volgendOnderhoud() != null
                    ? selectedMachine.volgendOnderhoud().format(DateTimeFormatter.ISO_LOCAL_DATE)
                    : "Niet gepland");
        }
    }

    private void initializeOnderhoudTable() {
        onderhoudDatumCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().datum().format(DateTimeFormatter.ISO_LOCAL_DATE)));
        onderhoudStatusCol
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status().name()));
        onderhoudTechniekerCol.setCellValueFactory(cellData -> {
            GebruikerDTO technieker = cellData.getValue().technieker();
            return new SimpleStringProperty(
                    technieker != null ? technieker.voornaam() + " " + technieker.achternaam() : "N/B");
        });
        onderhoudRedenCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().reden()));

        if (selectedMachine != null) {
            // Assuming OnderhoudController has a method to get all maintenance,
            // or you fetch all and filter.
            // For a more optimized approach, add a method in OnderhoudController:
            // getOnderhoudByMachineId(int machineId)
            ObservableList<OnderhoudDTO> alleOnderhoud = onderhoudController.getAllOnderhoud(); // Placeholder
            ObservableList<OnderhoudDTO> machineOnderhoud = alleOnderhoud.stream()
                    .filter(o -> o.machine() != null && o.machine().id() == selectedMachine.id())
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            onderhoudTable.setItems(machineOnderhoud);
        } else {
            onderhoudTable.setItems(FXCollections.emptyObservableList());
        }
    }

    private void initializeEventListeners() {
        btnTerug.setOnAction(event -> handleBackButton());
    }

    private void handleBackButton() {
        try {
            MainFrameController mainFrame = (MainFrameController) this.getScene().getRoot();
            MachineListFrameController machineListController = new MachineListFrameController(
                    this.machineController, // Pass existing controller instances
                    this.onderhoudController,
                    this.ingelogdeGebruiker,
                    this.logController);
            mainFrame.getMainView().getChildren().setAll(machineListController);
        } catch (Exception e) {
            AlertHelper.showError("Could not return to the machine list.", e.getMessage());
            e.printStackTrace();
        }
    }
}
