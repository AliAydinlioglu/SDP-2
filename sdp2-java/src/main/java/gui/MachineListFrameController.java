package gui;

import domain.Machine;
import domain.MachineController;
import dto.MachineDTO;
import enums.MachineStatus;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.IOException;

public class MachineListFrameController extends VBox {

    @FXML private TableView<MachineDTO> machineTable;
    @FXML private TableColumn<MachineDTO, String> nameCol;
    @FXML private TableColumn<MachineDTO, String> locationCol;
    @FXML private TableColumn<MachineDTO, String> statusCol;
    @FXML private TableColumn<MachineDTO, Number> uptimeCol;
    @FXML private Label lblStatus;

    private MachineController machineController;

    public MachineListFrameController(MachineController machineController) {
        this.machineController = machineController;

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
                MachineDTO selectedMachine = machineController.getMachine(newSelection.id());
                lblStatus.setText("Geselecteerd: " + selectedMachine.naam() + " - Status: " + selectedMachine.status());
            } else {
                lblStatus.setText("");
            }
        });

        if (machineController.getAll().isEmpty()) {
            lblStatus.setText("Geen machines gevonden.");
        }
    }
}
