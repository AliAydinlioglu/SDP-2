package gui;

import domain.Machine;
import domain.MachineController;
import domain.Onderhoud;
import domain.OnderhoudController;
import dto.GebruikerDTO;
import dto.MachineDTO;
import enums.MachineStatus;
import enums.Rol;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.AlertHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MachineListFrameController extends VBox {

    @FXML private TableView<MachineDTO> machineTable;
    @FXML private TableColumn<MachineDTO, String> nameCol;
    @FXML private TableColumn<MachineDTO, String> locationCol;
    @FXML private TableColumn<MachineDTO, String> statusCol;
    @FXML private TableColumn<MachineDTO, Number> uptimeCol;
    @FXML private Label lblStatus;
    @FXML private Button addOnderhoudBtn;

    private MachineController machineController;
    private OnderhoudController onderhoudController;
    
    private GebruikerDTO ingelogdeGebruiker;
    private MachineDTO selectedMachine;

    public MachineListFrameController(MachineController machineController, OnderhoudController onderhoudController, GebruikerDTO ingelogdeGebruiker) {
        this.machineController = machineController;
        this.onderhoudController = onderhoudController;
        this.ingelogdeGebruiker = ingelogdeGebruiker;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MachineListFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().naam()));
        locationCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().locatie()));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status().name()));
        uptimeCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().uptime()));

        machineTable.setItems(machineController.getAll());

        machineTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                this.selectedMachine = machineController.getMachine(newSelection.id());
                lblStatus.setText("Geselecteerd: " + selectedMachine.naam() + " - Status: " + selectedMachine.status());
            } else {
                lblStatus.setText("");
            }
        });

        if (machineController.getAll().isEmpty()) {
            lblStatus.setText("Geen machines gevonden.");
        }
        
        initializeForm();
    }
    
    private void initializeForm() {
    	if (ingelogdeGebruiker.rol() != Rol.TECHNIEKER) {
    		addOnderhoudBtn.setVisible(false);
    	}
		addOnderhoudBtn.setOnAction(event -> addOnderhoud());
	}
    
    @FXML
    private void addOnderhoud() {
        if (selectedMachine == null) {
            AlertHelper.showWarning("Geen machine geselecteerd", "Selecteer een machine om onderhoud toe te voegen.");
        } else if (selectedMachine.status() == MachineStatus.DRAAIT) {
            AlertHelper.showWarning("Machine draait", "Onderhoud kan niet worden toegevoegd terwijl de machine draait.");
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/AddOrEditOnderhoudFrame.fxml"));
                Parent root = loader.load();

                AddOrEditOnderhoudFrameController controller = loader.getController();
                controller.initData(onderhoudController, ingelogdeGebruiker, selectedMachine); // Geef de geselecteerde machine mee

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Onderhoud Toevoegen");
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setScene(new Scene(root));
                dialogStage.showAndWait();
                                
            } catch (IOException e) {
                AlertHelper.showError("Fout", "Kan onderhoud niet toevoegen: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }



}
