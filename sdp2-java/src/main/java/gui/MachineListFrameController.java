package gui;

import java.io.IOException;

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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.AlertHelper;

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

        if (ingelogdeGebruiker.rol() == Rol.ADMINISTRATOR || ingelogdeGebruiker.rol() == Rol.MANAGER) {
            this.machineList = machineController.getAll();
        } else if (ingelogdeGebruiker.rol() == Rol.TECHNIEKER) {
            this.machineList = machineController.getMachinesForTechnieker(ingelogdeGebruiker.id());
        } else {
            this.machineList = machineController.getAll();
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
                                + (this.selectedMachine != null ? this.selectedMachine.naam() : newSel.naam())
                                + " - Status: "
                                + (this.selectedMachine != null ? this.selectedMachine.status() : newSel.status()));
                    } else {
                        this.selectedMachine = null;
                        lblStatus.setText("");
                    }
                });

        machineTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                MachineDTO clickedMachine = machineTable.getSelectionModel().getSelectedItem();
                if (clickedMachine != null) {
                    openMachineDetailPopup(machineController.getMachine(clickedMachine.id()));
                }
            }
        });

        if (machineList.isEmpty()) {
            lblStatus.setText("Geen machines gevonden.");
        }

        initializeForm();
    }

    private void initializeForm() {
        boolean canManageMachines = ingelogdeGebruiker.rol() == Rol.ADMINISTRATOR
                || ingelogdeGebruiker.rol() == Rol.MANAGER;
        boolean canAddOnderhoud = ingelogdeGebruiker.rol() == Rol.TECHNIEKER || canManageMachines;

        addMachineBtn.setVisible(canManageMachines);
        addMachineBtn.setManaged(canManageMachines);

        updateMachineBtn.setVisible(canManageMachines);
        updateMachineBtn.setManaged(canManageMachines);

        removeMachineBtn.setVisible(canManageMachines);
        removeMachineBtn.setManaged(canManageMachines);

        addOnderhoudBtn.setVisible(canAddOnderhoud);
        addOnderhoudBtn.setManaged(canAddOnderhoud);

        addOnderhoudBtn.setOnAction(e -> addOnderhoud());
        addMachineBtn.setOnAction(e -> addMachine());
        updateMachineBtn.setOnAction(e -> updateMachine());
        removeMachineBtn.setOnAction(e -> removeMachine());

        updateMachineBtn.disableProperty().bind(machineTable.getSelectionModel().selectedItemProperty().isNull());
        removeMachineBtn.disableProperty().bind(machineTable.getSelectionModel().selectedItemProperty().isNull());
        addOnderhoudBtn.disableProperty().bind(machineTable.getSelectionModel().selectedItemProperty().isNull());
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

    private void openMachineDetailPopup(MachineDTO machineToDisplay) {
        if (machineToDisplay == null) {
            AlertHelper.showWarning("Machine Details", "Geselecteerde machine kon niet worden geladen.");
            return;
        }
        try {
            MachineDetailFrameController detailController = new MachineDetailFrameController(
                    this.machineController,
                    this.onderhoudController,
                    machineToDisplay,
                    this.ingelogdeGebruiker,
                    this.logController);

            MainFrameController mainFrame = (MainFrameController) this.getScene().getRoot();
            mainFrame.getMainView().getChildren().setAll(detailController);

        } catch (Exception e) {
            AlertHelper.showError("Kon machine details niet openen.", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void addOnderhoud() {
        selectedMachine = machineTable.getSelectionModel().getSelectedItem();
        if (selectedMachine == null) {
            AlertHelper.showWarning("Geen machine geselecteerd",
                    "Selecteer een machine om onderhoud toe te voegen.");
        } else if (selectedMachine.status() == MachineStatus.DRAAIT) {
            AlertHelper.showWarning("Machine draait",
                    "Onderhoud kan niet worden toegevoegd terwijl de machine draait.");
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/AddOrEditOnderhoudFrame.fxml"));
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
                dialog.initOwner(this.getScene().getWindow());
                dialog.setScene(new Scene(root));
                dialog.setResizable(false);
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
        selectedMachine = machineTable.getSelectionModel().getSelectedItem();
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
        selectedMachine = machineTable.getSelectionModel().getSelectedItem();
        MachineDTO priviousMachine = machineController.getMachine(selectedMachine.id());
        if (selectedMachine == null) {
            AlertHelper.showWarning("Geen selectie", "Selecteer eerst een machine om te verwijderen.");
            return;
        }
        boolean bevestigd = AlertHelper.showConfirmationAndWait("Machine Verwijderen",
                "Zeker dat u machine '" + selectedMachine.naam() + "' wilt verwijderen?");

        if (bevestigd) {
            try {
                machineController.deleteMachine(selectedMachine.id());

                if (logController != null) {
                    logController.addLog(ingelogdeGebruiker, "Machine verwijderd: " + priviousMachine.naam(),
                            ingelogdeGebruiker.voornaam() + " " + ingelogdeGebruiker.achternaam());
                }

                AlertHelper.showConfirmationAndWait("Machine verwijderd",
                        "Machine '" + priviousMachine.naam() + "' is verwijderd.");

                refreshMachineList();
            } catch (IllegalStateException e) {
                AlertHelper.showError("Verwijderen Mislukt", e.getMessage());
            } catch (IllegalArgumentException e) {
                AlertHelper.showWarning("Verwijderen Mislukt", e.getMessage());
            } catch (RuntimeException e) {
                AlertHelper.showError("Fout bij verwijderen", "Kon de machine niet verwijderen: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}