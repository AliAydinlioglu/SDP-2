package gui;

import domain.Machine;
import domain.MachineController;
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

    @FXML private TableView<Machine> machineTable;
    @FXML private TableColumn<Machine, String> nameCol;
    @FXML private TableColumn<Machine, String> locationCol;
    @FXML private TableColumn<Machine, String> statusCol;
    @FXML private TableColumn<Machine, Number> uptimeCol;
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

        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNaam()));
        locationCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocatie()));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));
        uptimeCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getUptime()));

        machineTable.setItems(machineController.getAll());

        machineTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Machine selectedMachine = machineController.getMachine(newSelection.getMachineID());
                lblStatus.setText("Geselecteerd: " + selectedMachine.getNaam() + " - Status: " + selectedMachine.getStatus());
            } else {
                lblStatus.setText("");
            }
        });

        if (machineController.getAll().isEmpty()) {
            lblStatus.setText("Geen machines gevonden.");
        }
    }
}
