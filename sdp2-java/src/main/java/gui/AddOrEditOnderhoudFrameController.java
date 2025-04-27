package gui;

import domain.Machine;
import domain.MachineController;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
    private MachineController machineController;

    public void initData(OnderhoudController onderhoudController, GebruikerDTO ingelogdeGebruiker, OnderhoudDTO geselecteerd, MachineController machineController) {
		initData(onderhoudController, ingelogdeGebruiker, null, geselecteerd, machineController);
	}

    public void initData(OnderhoudController controller, GebruikerDTO gebruiker, MachineDTO machine, MachineController machineController) {
        initData(controller, gebruiker, machine, null, machineController);
    }

    public void initData(OnderhoudController controller, GebruikerDTO gebruiker, MachineDTO machine, OnderhoudDTO onderhoud, MachineController machineController) {
        this.onderhoudController = controller;
        this.bewerktOnderhoud = onderhoud;
        this.ingelogdeGebruiker = gebruiker;
        this.machine = machine;
        this.machineController = machineController;

        statusBox.setItems(FXCollections.observableArrayList(OnderhoudStatus.values()));

        if (onderhoud != null) {
            // Converteer datum van UTC naar lokale tijd
            ZonedDateTime datumLocal = onderhoud.datum().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault());
            datumPicker.setValue(datumLocal.toLocalDate());

            // Converteer tijden van UTC naar lokale tijd
            ZonedDateTime startTijdLocal = onderhoud.startTijd().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault());
            ZonedDateTime eindTijdLocal = onderhoud.eindTijd().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault());

            startTijdField.setText(startTijdLocal.toLocalTime().toString());
            eindTijdField.setText(eindTijdLocal.toLocalTime().toString());
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
            ZonedDateTime datumUTC = datum.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));

            LocalDateTime startTijd = LocalDateTime.parse(datumPicker.getValue() + "T" + startTijdField.getText());
            LocalDateTime eindTijd = LocalDateTime.parse(datumPicker.getValue() + "T" + eindTijdField.getText());

            // Converteer naar UTC
            ZonedDateTime startTijdUTC = startTijd.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
            ZonedDateTime eindTijdUTC = eindTijd.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));

            String reden = redenField.getText();
            String rapport = objectMapper.writeValueAsString(rapportField.getText());
            String opmerkingen = opmerkingenArea.getText();
            OnderhoudStatus status = statusBox.getValue();

            if (bewerktOnderhoud != null) {
                OnderhoudDTO updatedOnderhoud = new OnderhoudDTO(
                        bewerktOnderhoud.id(),
                        datumUTC.toLocalDateTime(),
                        startTijdUTC.toLocalDateTime(),
                        eindTijdUTC.toLocalDateTime(),
                        reden,
                        rapport,
                        opmerkingen,
                        status,
                        bewerktOnderhoud.machineId(),
                        bewerktOnderhoud.technieker()
                );
                onderhoudController.updateOnderhoud(updatedOnderhoud);
                
                if (status == OnderhoudStatus.VOLTOOID) {
                	MachineDTO m = machineController.getMachine(bewerktOnderhoud.machineId());
                	machineController.stopOnderhoud(m);
                }
                
            } else {
                onderhoudController.addOnderhoud(
                        datumUTC.toLocalDateTime(),
                        startTijdUTC.toLocalDateTime(),
                        eindTijdUTC.toLocalDateTime(),
                        ingelogdeGebruiker.id(),
                        reden,
                        rapport,
                        opmerkingen,
                        status,
                        machine.id()
                );
                machineController.startOnderhoud(machine);
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
