package gui;

import java.time.LocalDate;

import domain.GebruikerController;
import domain.MachineController;
import domain.SiteController;
import dto.GebruikerDTO;
import dto.MachineDTO;
import dto.SiteDTO;
import enums.MachineStatus;
import enums.ProductionStatus;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.AlertHelper;

public class AddOrEditMachineFrameController {

    @FXML
    private ComboBox<SiteDTO.SiteSummaryDTO> cboSite;
    @FXML
    private TextField machineIdField;
    @FXML
    private TextField machineLocationField;
    @FXML
    private TextField txtProductInfo;
    @FXML
    private ComboBox<MachineStatus> machineStatusComboBox;
    @FXML
    private ComboBox<ProductionStatus> productionStatusComboBox;
    @FXML
    private Spinner<Integer> spnUptime;
    @FXML
    private ComboBox<GebruikerDTO> cboTechnician;
    @FXML
    private Label lblLastMaintenanceDate;
    @FXML
    private Hyperlink hlLastMaintenanceDetails;
    @FXML
    private Label lblDaysSinceMaintenance;
    @FXML
    private DatePicker dpVolgendOnderhoud;
    @FXML
    private Button btnOpslaan;
    @FXML
    private Button btnAnnuleren;

    private MachineController machineController;
    private MachineDTO existingMachine;
    private GebruikerDTO ingelogdeGebruiker;
    private boolean isEditMode = false;

    public void initData(MachineController mc, MachineDTO m, GebruikerDTO user) {
        this.machineController = mc;
        this.existingMachine = m;
        this.ingelogdeGebruiker = user;
        this.isEditMode = (m != null);
        initializeControls();
        if (isEditMode) populateFields();
    }

    private void initializeControls() {
        // Load sites from MachineController
        cboSite.setItems(machineController.getAllSites());

        // Set up status comboboxes with Dutch tooltips
        machineStatusComboBox.setItems(FXCollections.observableArrayList(MachineStatus.values()));
        machineStatusComboBox.setTooltip(new Tooltip("Status van de machine: Draait, Gestopt (auto of manueel), etc."));

        productionStatusComboBox.setItems(FXCollections.observableArrayList(ProductionStatus.values()));
        productionStatusComboBox.setTooltip(new Tooltip("Productiestatus: Gezond, Nood aan onderhoud, Falend"));

        // Load technicians from MachineController
        cboTechnician.setItems(machineController.getAllTechnicians());

        // Set up uptime spinner
        SpinnerValueFactory<Integer> vf = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0);
        spnUptime.setValueFactory(vf);

        // Set default next maintenance date to 1 month from now
        dpVolgendOnderhoud.setValue(LocalDate.now().plusMonths(1));

        // Set up maintenance details link
        // This would typically open a details view for the maintenance
        hlLastMaintenanceDetails.setOnAction(e -> {
            if (existingMachine != null) {
                // This method needs to be implemented in MachineController
                // machineController.openMaintenanceDetails(existingMachine.id());
                AlertHelper.showInfo("Onderhoud Details", "Details voor onderhoud van machine " + existingMachine.id());
            }
        });

        // Set up button actions
        btnOpslaan.setOnAction(e -> handleOpslaan());
        btnAnnuleren.setOnAction(e -> closeStage());
    }

    private void populateFields() {
        cboSite.setValue(existingMachine.site());
        machineIdField.setText(String.valueOf(existingMachine.id()));
        machineLocationField.setText(existingMachine.locatie());
        txtProductInfo.setText(existingMachine.productInfo());
        machineStatusComboBox.setValue(existingMachine.status());
        productionStatusComboBox.setValue(existingMachine.productieStatus());
        spnUptime.getValueFactory().setValue(existingMachine.uptime());
        cboTechnician.setValue(existingMachine.technieker());
        // compute last maintenance date & days since
        LocalDate last = LocalDate.now().minusDays(existingMachine.dagenSindsOnderhoud());
        lblLastMaintenanceDate.setText(last.toString());
        lblDaysSinceMaintenance.setText(String.valueOf(existingMachine.dagenSindsOnderhoud()));
        if (existingMachine.volgendOnderhoud() != null)
            dpVolgendOnderhoud.setValue(existingMachine.volgendOnderhoud());
    }

    @FXML
    private void handleOpslaan() {
        try {
            // Create a DTO with the form data
            MachineDTO dto = new MachineDTO(
                    isEditMode ? existingMachine.id() : 0,
                    machineIdField.getText(),
                    txtProductInfo.getText(),
                    machineLocationField.getText(),
                    machineStatusComboBox.getValue(),
                    productionStatusComboBox.getValue(),
                    spnUptime.getValue(),
                    isEditMode ? existingMachine.dagenSindsOnderhoud() : 0,
                    dpVolgendOnderhoud.getValue(),
                    cboTechnician.getValue(),
                    cboSite.getValue()
            );

            // Use the controller to update or add the machine
            if (isEditMode) {
                machineController.updateMachine(dto);
                AlertHelper.showInfo("Bijgewerkt", "Machine bijgewerkt.");
            } else {
                // Use the addMachineFromDTO method to add a new machine
                machineController.addMachineFromDTO(dto);
                AlertHelper.showInfo("Toegevoegd", "Machine toegevoegd.");
            }
            closeStage();
        } catch (Exception ex) {
            AlertHelper.showError("Fout", ex.getMessage());
        }
    }

    @FXML
    void handleAnnuleren(ActionEvent event) {
        closeStage();
    }

    private void closeStage() {
        ((Stage) btnAnnuleren.getScene().getWindow()).close();
    }
}
