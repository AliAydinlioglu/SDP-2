package gui;

import domain.MachineController;
import dto.MachineDTO;
import enums.MachineStatus;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import utils.AlertHelper;

import java.io.IOException;

public class MachineListFrameController extends VBox {

    @FXML
    private TableView<MachineDTO> machineTable;
    @FXML
    private TableColumn<MachineDTO, String> nameCol;
    @FXML
    private TableColumn<MachineDTO, String> locationCol;
    @FXML
    private TableColumn<MachineDTO, String> statusCol;
    @FXML
    private TableColumn<MachineDTO, Number> uptimeCol;
    @FXML
    private TextField txtFilter;
    @FXML
    private VBox detailBox;
    @FXML
    private Label lblNaam;
    @FXML
    private Label lblLocatie;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblUptime;
    @FXML
    private Button addBtn;

    private MachineController machineController;
    private MachineDTO selectedMachine;

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

        machineTable.setItems(machineController.getAllMachines());

        machineTable.setRowFactory(new Callback<>() {
            @Override
            public TableRow<MachineDTO> call(TableView<MachineDTO> tableView) {
                return new TableRow<>() {
                    @Override
                    protected void updateItem(MachineDTO machine, boolean empty) {
                        super.updateItem(machine, empty);
                        if (machine == null || empty) {
                            setStyle("");
                        } else if (machine.status() == MachineStatus.GESTOPT_AUTO && !isSelected()) {
                            setStyle("-fx-background-color: #ffcccc;");
                        } else {
                            setStyle("");
                        }
                    }
                };
            }
        });

        machineTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                MachineDTO selected = machineTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showDetails(selected);
                    selectedMachine = selected;
                }
            }
        });

        if (machineController.getAllMachines().isEmpty()) {
            lblNaam.setText("Geen machines gevonden.");
        }
    }

    @FXML
    private void filter(KeyEvent event) {
        String newValue = txtFilter.getText();
        machineController.changeFilter(newValue);
    }

    @FXML
    private void clearSelectedMachine() {
        detailBox.setVisible(false);
    }

    @FXML
    private void editSelectedMachine() {
        if (selectedMachine != null) {
            updateMachine(selectedMachine);
        }
    }

    @FXML
    private void addMachine() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/AddOrEditMachineFrame.fxml"));
            Parent root = loader.load();

            AddOrEditMachineFrameController controller = loader.getController();
            controller.initData(this.machineController);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Machine Toevoegen");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            machineTable.refresh();

        } catch (IOException e) {
            AlertHelper.showError("Machine opslaan mislukt", e.getMessage());
        }
    }

    private void updateMachine(MachineDTO machine) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/AddOrEditMachineFrame.fxml"));
            Parent root = loader.load();

            AddOrEditMachineFrameController controller = loader.getController();
            controller.initData(this.machineController, machine);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Machine Aanpassen");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            machineTable.refresh();

        } catch (IOException e) {
            AlertHelper.showError("Machine aanpassen mislukt", e.getMessage());
        }
    }

    private void showDetails(MachineDTO m) {
        lblNaam.setText("Naam: " + m.naam());
        lblLocatie.setText("Locatie: " + m.locatie());
        lblStatus.setText("Status: " + m.status().name());
        lblUptime.setText("Uptime: " + m.uptime() + "u");

        detailBox.setVisible(true);
    }
}
