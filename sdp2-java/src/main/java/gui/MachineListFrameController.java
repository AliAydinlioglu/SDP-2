package gui;

// replace VBox with StackPane:

import javafx.scene.layout.StackPane;

import domain.LogController;
import domain.MachineController;
import domain.OnderhoudController;
import dto.GebruikerDTO;
import dto.MachineDTO;
import enums.MachineStatus;
import enums.Rol;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
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
    private Label lblStatus;
    @FXML
    private Button addOnderhoudBtn;
    @FXML
    private Button addMachineBtn;
    @FXML
    private Button updateMachineBtn;
    @FXML
    private Button removeMachineBtn;

    private MachineController machineController;
    private OnderhoudController onderhoudController;
    private LogController logController;
    private GebruikerDTO ingelogdeGebruiker;
    private MachineDTO selectedMachine;
    private ObservableList<MachineDTO> machineList;

    public MachineListFrameController(MachineController machineController,
                                      OnderhoudController onderhoudController,
                                      GebruikerDTO ingelogdeGebruiker,
                                      LogController logController) {
        this.machineController = machineController;
        this.onderhoudController = onderhoudController;
        this.ingelogdeGebruiker = ingelogdeGebruiker;
        this.logController = logController;

        if (ingelogdeGebruiker.rol() == Rol.ADMINISTRATOR) {
            this.machineList = machineController.getAll();
        } else {
            this.machineList = machineController.getMachinesForTechnieker(ingelogdeGebruiker.id());
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MachineListFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        nameCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().naam()));
        locationCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().locatie()));
        statusCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().status().name()));
        uptimeCol.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().uptime()));

        machineTable.setItems(machineList);
        machineTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    if (newSel != null) {
                        this.selectedMachine = machineController.getMachine(newSel.id());
                        lblStatus.setText("Geselecteerd: "
                                + selectedMachine.naam()
                                + " - Status: "
                                + selectedMachine.status());
                    } else {
                        lblStatus.setText("");
                    }
                });

        if (machineList.isEmpty()) {
            lblStatus.setText("Geen machines gevonden.");
        }

        initializeForm();
    }

    private void initializeForm() {
        if (ingelogdeGebruiker.rol() != Rol.TECHNIEKER &&
                ingelogdeGebruiker.rol() != Rol.ADMINISTRATOR) {
            addOnderhoudBtn.setVisible(false);
            addMachineBtn.setVisible(false);
            updateMachineBtn.setVisible(false);
            removeMachineBtn.setVisible(false);
        }
        addOnderhoudBtn.setOnAction(e -> addOnderhoud());
        addMachineBtn.setOnAction(e -> addMachine());
        updateMachineBtn.setOnAction(e -> updateMachine());
        removeMachineBtn.setOnAction(e -> removeMachine());

        updateMachineBtn.setDisable(true);
        removeMachineBtn.setDisable(true);

        machineTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    boolean hasSelection = newSel != null;
                    updateMachineBtn.setDisable(!hasSelection);
                    removeMachineBtn.setDisable(!hasSelection);
                });
    }


    private void refreshMachineList() {
        if (ingelogdeGebruiker.rol() == Rol.ADMINISTRATOR) {
            machineList = machineController.getAll();
        } else {
            machineList = machineController.getMachinesForTechnieker(ingelogdeGebruiker.id());
        }
        machineTable.setItems(machineList);

        machineTable.getSelectionModel().clearSelection();
        selectedMachine = null;
        lblStatus.setText("");

        if (machineList.isEmpty()) {
            lblStatus.setText("Geen machines gevonden.");
        }
    }

    @FXML
    private void addOnderhoud() {
        if (selectedMachine == null) {
            AlertHelper.showWarning("Geen machine geselecteerd",
                    "Selecteer een machine om onderhoud toe te voegen.");
        } else if (selectedMachine.status() == MachineStatus.DRAAIT) {
            AlertHelper.showWarning("Machine draait",
                    "Onderhoud kan niet worden toegevoegd terwijl de machine draait.");
        } else {
            try {
                FXMLLoader loader =
                        new FXMLLoader(getClass().getResource("/gui/AddOrEditOnderhoudFrame.fxml"));
                Parent root = loader.load();
                AddOrEditOnderhoudFrameController controller = loader.getController();
                controller.initData(onderhoudController,
                        ingelogdeGebruiker,
                        selectedMachine,
                        machineController,
                        logController);

                Stage dialog = new Stage();
                dialog.setTitle("Onderhoud Toevoegen");
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.setScene(new Scene(root));
                dialog.showAndWait();
            } catch (IOException e) {
                AlertHelper.showError("Fout",
                        "Kan onderhoud niet toevoegen: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void updateMachine() {
        if (selectedMachine == null) {
            AlertHelper.showWarning("Geen machine geselecteerd",
                    "Selecteer een machine om te bewerken.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/AddOrEditMachineFrame.fxml"));
            Parent root = loader.load();
            AddOrEditMachineFrameController controller = loader.getController();

            controller.initData(machineController, selectedMachine, ingelogdeGebruiker);

            Stage dialog = new Stage();
            dialog.setTitle("Machine Bewerken");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            refreshMachineList();
        } catch (IOException e) {
            AlertHelper.showError("Fout", "Kan machine niet bewerken: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void addMachine() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/AddOrEditMachineFrame.fxml"));
            Parent root = loader.load();
            AddOrEditMachineFrameController controller = loader.getController();

            controller.initData(machineController, null, ingelogdeGebruiker);

            Stage dialog = new Stage();
            dialog.setTitle("Machine Toevoegen");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            refreshMachineList();
        } catch (IOException e) {
            AlertHelper.showError("Fout", "Kan machine niet toevoegen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void removeMachine() {
        if (selectedMachine == null) {
            javafx.scene.control.Alert warningAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            warningAlert.setTitle("Geen machine geselecteerd");
            warningAlert.setHeaderText(null);
            warningAlert.setContentText("Selecteer een machine om te verwijderen.");
            warningAlert.showAndWait();
            return;
        }
        javafx.scene.control.Alert confirmAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Machine verwijderen");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Weet u zeker dat u machine \"" + selectedMachine.naam() + "\" wilt verwijderen?");

        if (confirmAlert.showAndWait().filter(response -> response == javafx.scene.control.ButtonType.OK).isPresent()) {
            try {
                machineController.deleteMachine(selectedMachine.id());

                if (logController != null) {
                    logController.addLog(ingelogdeGebruiker,"Machine verwijderd: " + selectedMachine.naam(),
                            ingelogdeGebruiker.voornaam() + " " + ingelogdeGebruiker.achternaam());
                }

                javafx.scene.control.Alert infoAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                infoAlert.setTitle("Machine verwijderd");
                infoAlert.setHeaderText(null);
                infoAlert.setContentText("Machine \"" + selectedMachine.naam() + "\" is verwijderd.");
                infoAlert.showAndWait();

                refreshMachineList();
            } catch (Exception e) {
                javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                errorAlert.setTitle("Fout bij verwijderen");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Kan machine niet verwijderen: " + e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }
}