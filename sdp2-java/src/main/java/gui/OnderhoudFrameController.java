package gui;

import domain.Onderhoud;
import domain.OnderhoudController;
import enums.OnderhoudStatus;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDateTime;

public class OnderhoudFrameController extends VBox {

    @FXML private TableView<Onderhoud> onderhoudTable;
    @FXML private TableColumn<Onderhoud, String> datumCol;
    @FXML private TableColumn<Onderhoud, String> statusCol;
    @FXML private TableColumn<Onderhoud, String> techniekerCol;
    @FXML private TableColumn<Onderhoud, String> machineCol;

    @FXML private TextField txtReden;
    @FXML private TextField txtRapport;
    @FXML private TextArea txtOpmerkingen;
    @FXML private ComboBox<OnderhoudStatus> statusBox;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private OnderhoudController onderhoudController;

    public OnderhoudFrameController(OnderhoudController onderhoudController) {
        this.onderhoudController = onderhoudController;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/OnderhoudFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        initializeTable();
        initializeForm();
    }

    private void initializeTable() {
        datumCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDatum().toString()));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));
        techniekerCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getTechnieker() != null
                        ? cellData.getValue().getTechnieker().getVoornaam() + " " + cellData.getValue().getTechnieker().getAchternaam()
                        : "Onbekend"));
        machineCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                "Machine ID: " + cellData.getValue().getMachineId()));

        onderhoudTable.setItems(onderhoudController.getAllOnderhoud());
    }

    private void initializeForm() {
        statusBox.getItems().addAll(OnderhoudStatus.values());

        btnAdd.setOnAction(event -> addOnderhoud());
        btnEdit.setOnAction(event -> editOnderhoud());
        btnDelete.setOnAction(event -> deleteOnderhoud());
    }

    private void addOnderhoud() {
        try {
            Onderhoud nieuwOnderhoud = new Onderhoud(
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    LocalDateTime.now().plusHours(1),
                    1, // Technieker ID (voorbeeld)
                    txtReden.getText(),
                    txtRapport.getText(),
                    txtOpmerkingen.getText(),
                    statusBox.getValue(),
                    1 // Machine ID (voorbeeld)
            );
            onderhoudController.registerOnderhoud(nieuwOnderhoud);
            onderhoudTable.getItems().add(nieuwOnderhoud);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void editOnderhoud() {
        Onderhoud geselecteerd = onderhoudTable.getSelectionModel().getSelectedItem();
        if (geselecteerd != null) {
            try {
                geselecteerd.setReden(txtReden.getText());
                geselecteerd.setRapport(txtRapport.getText());
                geselecteerd.setOpmerkingen(txtOpmerkingen.getText());
                geselecteerd.setStatus(statusBox.getValue());
                onderhoudController.updateOnderhoud(geselecteerd);
                onderhoudTable.refresh();
            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
            }
        } else {
            showError("Selecteer een onderhoud om te bewerken.");
        }
    }

    private void deleteOnderhoud() {
        Onderhoud geselecteerd = onderhoudTable.getSelectionModel().getSelectedItem();
        if (geselecteerd != null) {
            onderhoudController.deleteOnderhoud(geselecteerd);
            onderhoudTable.getItems().remove(geselecteerd);
        } else {
            showError("Selecteer een onderhoud om te verwijderen.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fout");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
