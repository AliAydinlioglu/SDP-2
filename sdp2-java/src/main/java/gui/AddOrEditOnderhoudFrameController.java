package gui;

import domain.OnderhoudController;
import dto.GebruikerDTO;
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

import com.fasterxml.jackson.databind.ObjectMapper;

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
    
	private final ObjectMapper objectMapper = new ObjectMapper();
    private OnderhoudController onderhoudController;
    private OnderhoudDTO bewerktOnderhoud; // null if creating new
    private GebruikerDTO ingelogdeGebruiker;
    private MachineDTO machine;

    public void initData(OnderhoudController onderhoudController, GebruikerDTO ingelogdeGebruiker, OnderhoudDTO geselecteerd) {
		initData(onderhoudController, ingelogdeGebruiker, null, geselecteerd);
	}

    public void initData(OnderhoudController controller, GebruikerDTO gebruiker, MachineDTO machine) {
        initData(controller, gebruiker, machine, null);
    }

    public void initData(OnderhoudController controller, GebruikerDTO gebruiker, MachineDTO machine, OnderhoudDTO onderhoud) {
        this.onderhoudController = controller;
        this.bewerktOnderhoud = onderhoud;
        this.ingelogdeGebruiker = gebruiker;
        this.machine = machine;

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
            String rapport = objectMapper.writeValueAsString(rapportField.getText());
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
                        ingelogdeGebruiker.id(),
                        reden,
                        rapport,
                        opmerkingen,
                        status,
                        machine.id()
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
