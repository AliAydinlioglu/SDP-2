package gui;

import domain.Onderhoud;
import domain.OnderhoudController;
import dto.OnderhoudDTO;
import enums.OnderhoudStatus;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDateTime;

public class OnderhoudFrameController extends VBox {

    @FXML private TableView<OnderhoudDTO> onderhoudTable;
    @FXML private TableColumn<OnderhoudDTO, String> datumCol;
    @FXML private TableColumn<OnderhoudDTO, String> statusCol;
    @FXML private TableColumn<OnderhoudDTO, String> techniekerCol;
    @FXML private TableColumn<OnderhoudDTO, String> machineCol;

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
        datumCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().datum().toString()));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status().name()));
        techniekerCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().technieker() != null
                        ? cellData.getValue().technieker().voornaam() + " " + cellData.getValue().technieker().achternaam()
                        : "Onbekend"));
        machineCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                "Machine ID: " + cellData.getValue().machineId()));

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
            //onderhoudTable.getItems().add(nieuwOnderhoud);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void editOnderhoud() {
        OnderhoudDTO geselecteerd = onderhoudTable.getSelectionModel().getSelectedItem();
        if (geselecteerd != null) {
            try {
//                geselecteerd.setReden(txtReden.getText());
//                geselecteerd.setRapport(txtRapport.getText());
//                geselecteerd.setOpmerkingen(txtOpmerkingen.getText());
//                geselecteerd.setStatus(statusBox.getValue());
            	  OnderhoudDTO updatedOnderhoud = new OnderhoudDTO(
						  geselecteerd.id(),
						  geselecteerd.datum(),
						  geselecteerd.startTijd(),
						  geselecteerd.eindTijd(),
						  txtReden.getText(),
						  txtRapport.getText(),
						  txtOpmerkingen.getText(),
						  statusBox.getValue(),
						  geselecteerd.machineId(),
						  geselecteerd.technieker()
				  );
            	  onderhoudController.updateOnderhoud(updatedOnderhoud);
            	  onderhoudTable.refresh();
            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
            }
        } else {
            showError("Selecteer een onderhoud om te bewerken.");
        }
    }

    private void deleteOnderhoud() {
        OnderhoudDTO geselecteerd = onderhoudTable.getSelectionModel().getSelectedItem();
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
