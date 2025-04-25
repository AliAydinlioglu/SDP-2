package gui;

import domain.OnderhoudController;
import dto.MachineDTO;
import dto.OnderhoudDTO;
import enums.OnderhoudStatus;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.AlertHelper;

import java.time.LocalDateTime;

public class AddOrEditOnderhoudFrameController {

    @FXML
    private DatePicker datumPicker;

    @FXML
    private TextField startTijdField;

    @FXML
    private TextField eindTijdField;

    @FXML
    private TextField redenField;

    @FXML
    private TextField rapportField;

    @FXML
    private TextArea opmerkingenArea;

    @FXML
    private ComboBox<OnderhoudStatus> statusBox;

    @FXML
    private Button submitBtn;

    @FXML
    private Button cancelBtn;

    private OnderhoudController onderhoudController;
    private OnderhoudDTO bewerktOnderhoud; // null if creating new

    public void initData(OnderhoudController controller, MachineDTO machine) {
        initData(controller, machine, null);
        statusBox.setItems(FXCollections.observableArrayList(OnderhoudStatus.values()));
    }

    public void initData(OnderhoudController controller, MachineDTO machine, OnderhoudDTO onderhoud) {
        this.onderhoudController = controller;
        this.bewerktOnderhoud = onderhoud;

        statusBox.setItems(FXCollections.observableArrayList(OnderhoudStatus.values()));

        if (onderhoud != null) {
            datumPicker.setValue(onderhoud.datum().toLocalDate());
            startTijdField.setText(onderhoud.startTijd().toLocalTime().toString());
            eindTijdField.setText(onderhoud.eindTijd().toLocalTime().toString());
            redenField.setText(onderhoud.reden());
            rapportField.setText(onderhoud.rapport());
            opmerkingenArea.setText(onderhoud.opmerkingen());
            statusBox.setValue(onderhoud.status());

            submitBtn.setText("Opslaan");
        }
    }

    @FXML
    private void saveOnderhoud() {
        try {
            LocalDateTime datum = datumPicker.getValue().atStartOfDay();
            LocalDateTime startTijd = LocalDateTime.parse(datumPicker.getValue() + "T" + startTijdField.getText());
            LocalDateTime eindTijd = LocalDateTime.parse(datumPicker.getValue() + "T" + eindTijdField.getText());
            String reden = redenField.getText();
            String rapport = rapportField.getText();
            String opmerkingen = opmerkingenArea.getText();
            OnderhoudStatus status = statusBox.getValue();

            if (bewerktOnderhoud != null) {
                // Update bestaand onderhoud
                OnderhoudDTO updatedOnderhoud = new OnderhoudDTO(
                        bewerktOnderhoud.id(),
                        datum,
                        startTijd,
                        eindTijd,
                        reden,
                        rapport,
                        opmerkingen,
                        status,
                        bewerktOnderhoud.machineId(),
                        bewerktOnderhoud.technieker()
                );
                onderhoudController.updateOnderhoud(updatedOnderhoud);
            } else {
                // Voeg nieuw onderhoud toe
                onderhoudController.addOnderhoud(
                        datum,
                        startTijd,
                        eindTijd,
                        1, // Voorbeeld technieker ID
                        reden,
                        rapport,
                        opmerkingen,
                        status,
                        1 // Voorbeeld machine ID
                );
            }

            ((Stage) submitBtn.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Opslaan mislukt", e.getMessage());
        }
    }

    @FXML
    void cancel(ActionEvent event) {
        ((Stage) cancelBtn.getScene().getWindow()).close();
    }
}
