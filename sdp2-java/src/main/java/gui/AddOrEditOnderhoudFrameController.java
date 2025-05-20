package gui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.databind.ObjectMapper;

import domain.LogController;
import domain.MachineController;
import domain.OnderhoudController;
import dto.GebruikerDTO;
import dto.MachineDTO;
import dto.OnderhoudDTO;
import enums.OnderhoudStatus;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.AlertHelper;

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
    
    private LogController logController;

    public void initData(OnderhoudController onderhoudController, GebruikerDTO ingelogdeGebruiker, OnderhoudDTO geselecteerd, MachineController machineController, LogController logController) {
		initData(onderhoudController, ingelogdeGebruiker, null, geselecteerd, machineController, logController);
	}

    public void initData(OnderhoudController controller, GebruikerDTO gebruiker, MachineDTO machine, MachineController machineController, LogController logController) {
        initData(controller, gebruiker, machine, null, machineController, logController);
    }

    public void initData(OnderhoudController controller, GebruikerDTO gebruiker, MachineDTO machine, OnderhoudDTO onderhoud, MachineController machineController, LogController logController) {
        this.onderhoudController = controller;
        this.bewerktOnderhoud = onderhoud;
        this.ingelogdeGebruiker = gebruiker;
        this.machine = machine;
        this.machineController = machineController;
        this.logController = logController;

        statusBox.setItems(FXCollections.observableArrayList(OnderhoudStatus.values()));

        if (bewerktOnderhoud != null) {

            startTijdField.setText(bewerktOnderhoud.startTijd().toString());
            eindTijdField.setText(bewerktOnderhoud.eindTijd().toString());
            redenField.setText(bewerktOnderhoud.reden());
            rapportField.setText(bewerktOnderhoud.rapport());
            opmerkingenArea.setText(bewerktOnderhoud.opmerkingen());
            statusBox.setValue(bewerktOnderhoud.status());

            submitBtn.setText("Opslaan");
        }
    }

    @FXML
    private void saveOnderhoud() {
        try {
        	validateOnderhoud();
            
        	LocalDate datum = datumPicker.getValue();

            LocalTime startTijd = LocalTime.parse(startTijdField.getText());
            LocalTime eindTijd = LocalTime.parse(eindTijdField.getText());

            String reden = redenField.getText();
            String rapport = objectMapper.writeValueAsString(rapportField.getText());
            String opmerkingen = opmerkingenArea.getText();
            OnderhoudStatus status = statusBox.getValue();

            if (bewerktOnderhoud != null) {
                OnderhoudDTO updatedOnderhoud = new OnderhoudDTO(
                        bewerktOnderhoud.id(),
                        datum,
                        startTijd,
                        eindTijd,
                        reden,
                        rapport,
                        opmerkingen,
                        status,
                        bewerktOnderhoud.machine(),
                        bewerktOnderhoud.technieker()
                );
                onderhoudController.updateOnderhoud(updatedOnderhoud);
                logController.addLog(ingelogdeGebruiker, String.format("Onderhoud met id %d aangepast", updatedOnderhoud.id()), "");
                if (status == OnderhoudStatus.VOLTOOID) {;
                	machineController.stopOnderhoud(bewerktOnderhoud.machine());
                    logController.addLog(ingelogdeGebruiker, String.format("Machine met id %d is startbaar", bewerktOnderhoud.machine().id()), "");

                }
                
            } else {
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
                machineController.startOnderhoud(machine);
                logController.addLog(ingelogdeGebruiker, String.format("Onderhoud voor machine %d aangemaakt", machine.id()), "");

            }

            ((Stage) submitBtn.getScene().getWindow()).close();
            
            AlertHelper.showInfo("Opslaan gelukt", "Het onderhoud is succesvol opgeslagen.");

        } catch (DateTimeParseException dtp) {
        	AlertHelper.showError("Ongeldige tijd", "De tijd moet in het formaat HH:mm zijn.");        
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Opslaan mislukt", e.getMessage());
        }
    }

    @FXML
    void cancel(ActionEvent event) {
        ((Stage) cancelBtn.getScene().getWindow()).close();
    }
    
    public void validateOnderhoud() {
		if (datumPicker.getValue() == null)
			throw new IllegalArgumentException("Datum mag niet leeg zijn.");
		if (startTijdField.getText() == null || startTijdField.getText().isEmpty())
			throw new IllegalArgumentException("Starttijd mag niet leeg zijn.");
		if (eindTijdField.getText() == null || eindTijdField.getText().isEmpty())
			throw new IllegalArgumentException("Eindtijd mag niet leeg zijn.");
		if (redenField.getText() == null || redenField.getText().isEmpty())
			throw new IllegalArgumentException("Reden mag niet leeg zijn.");
		if (rapportField.getText() == null || rapportField.getText().isEmpty())
			throw new IllegalArgumentException("Rapport mag niet leeg zijn.");
		if (statusBox.getValue() == null)
			throw new IllegalArgumentException("Status mag niet leeg zijn.");
    }

}
