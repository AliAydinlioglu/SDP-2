package gui;

import domain.Machine;
import domain.MachineController;
import dto.GebruikerDTO;
import dto.MachineDTO;
import dto.SiteDTO;
import enums.MachineStatus;
import enums.ProductionStatus;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.AlertHelper;

import java.time.LocalDate;

public class AddOrEditMachineFrameController {

    @FXML
    private TextField txtNaam;
    @FXML
    private TextField txtProductInfo;
    @FXML
    private TextField txtLocatie;
    @FXML
    private ComboBox<MachineStatus> cboStatus;
    @FXML
    private ComboBox<ProductionStatus> cboProductieStatus;
    @FXML
    private Spinner<Integer> spnUptime;
    @FXML
    private DatePicker dpVolgendOnderhoud;
    @FXML
    private ComboBox<SiteDTO> cboSite;
    @FXML
    private Button btnOpslaan;
    @FXML
    private Button btnAnnuleren;

    private MachineController machineController;
    private MachineDTO existingMachine;
    private GebruikerDTO ingelogdeGebruiker;
    private boolean isEditMode = false;

    public void initData(MachineController machineController, MachineDTO existingMachine, GebruikerDTO ingelogdeGebruiker) {
        this.machineController = machineController;
        this.existingMachine = existingMachine;
        this.ingelogdeGebruiker = ingelogdeGebruiker;
        this.isEditMode = (existingMachine != null);

        initializeControls();
        
        if (isEditMode) {
            populateFields();
        }
    }

    private void initializeControls() {
        cboStatus.setItems(FXCollections.observableArrayList(MachineStatus.values()));
        cboStatus.getSelectionModel().selectFirst();
        
        cboProductieStatus.setItems(FXCollections.observableArrayList(ProductionStatus.values()));
        cboProductieStatus.getSelectionModel().selectFirst();
        
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000000, 0);
        spnUptime.setValueFactory(valueFactory);
        
        dpVolgendOnderhoud.setValue(LocalDate.now().plusMonths(1));
        
        btnOpslaan.setOnAction(e -> saveHandler());
        btnAnnuleren.setOnAction(e -> ((Stage) btnAnnuleren.getScene().getWindow()).close());
        
        cboSite.setItems(FXCollections.observableArrayList());
    }

    private void populateFields() {
        txtNaam.setText(existingMachine.naam());
        txtProductInfo.setText(existingMachine.productInfo());
        txtLocatie.setText(existingMachine.locatie());
        cboStatus.setValue(existingMachine.status());
        cboProductieStatus.setValue(existingMachine.productieStatus());
        spnUptime.getValueFactory().setValue(existingMachine.uptime());
        
        if (existingMachine.volgendOnderhoud() != null) {
            dpVolgendOnderhoud.setValue(existingMachine.volgendOnderhoud());
        }
        
        if (existingMachine.site() != null) {
        }
    }

    private void saveHandler() {
        try {
            if (txtNaam.getText().trim().isEmpty()) {
                AlertHelper.showError("Invoerfout", "Naam is verplicht.");
                return;
            }
            
            if (isEditMode) {
                MachineDTO updatedMachine = new MachineDTO(
                        existingMachine.id(),
                        txtNaam.getText(),
                        txtProductInfo.getText(),
                        txtLocatie.getText(),
                        cboStatus.getValue(),
                        cboProductieStatus.getValue(),
                        spnUptime.getValue(),
                        existingMachine.dagenSindsOnderhoud(),
                        dpVolgendOnderhoud.getValue(),
                        existingMachine.technieker(),
                        existingMachine.site()
                );
                
                machineController.updateMachine(updatedMachine);
                AlertHelper.showInfo("Machine bijgewerkt", "Machine \"" + updatedMachine.naam() + "\" is bijgewerkt.");
            } else {
                Machine newMachine = new Machine(
                        txtNaam.getText(),
                        txtProductInfo.getText(),
                        txtLocatie.getText(),
                        cboStatus.getValue(),
                        cboProductieStatus.getValue(),
                        spnUptime.getValue(),
                        null,
                        0,
                        dpVolgendOnderhoud.getValue(),
                        null
                );
                
                machineController.addMachine(newMachine);
                AlertHelper.showInfo("Machine toegevoegd", "Machine \"" + newMachine.getNaam() + "\" is toegevoegd.");
            }
            
            ((Stage) btnOpslaan.getScene().getWindow()).close();
        } catch (Exception e) {
            AlertHelper.showError("Fout", e.getMessage());
        }
    }
}
