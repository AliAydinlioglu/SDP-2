package gui;

import domain.LogController;
import domain.OnderhoudController;
import dto.GebruikerDTO;
import dto.OnderhoudDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class OnderhoudDetailFrameController {

    @FXML private Label lblDatum;
    @FXML private Label lblStartTijd;
    @FXML private Label lblEindTijd;
    @FXML private Label lblStatus;
    @FXML private Label lblTechnieker;
    @FXML private Label lblMachine;
    @FXML private Label lblReden;
    @FXML private Label lblRapport;
    @FXML private Label lblOpmerkingen;

    private OnderhoudController onderhoudController;
    private LogController logController;
    private GebruikerDTO ingelogdeGebruiker;

    public void initData(OnderhoudDTO onderhoud, OnderhoudController onderhoudController, LogController logController, GebruikerDTO ingelogdeGebruiker) {
        this.onderhoudController = onderhoudController;
        this.logController = logController;
        this.ingelogdeGebruiker = ingelogdeGebruiker;

        lblDatum.setText(onderhoud.datum().toString());
        lblStartTijd.setText(onderhoud.startTijd().toString());
        lblEindTijd.setText(onderhoud.eindTijd().toString());
        lblStatus.setText(onderhoud.status().name());
        lblTechnieker.setText(onderhoud.technieker() != null
                ? onderhoud.technieker().voornaam() + " " + onderhoud.technieker().achternaam()
                : "Onbekend");
        lblMachine.setText(onderhoud.machine().naam());
        lblReden.setText(onderhoud.reden());
        lblRapport.setText(onderhoud.rapport());
        lblOpmerkingen.setText(onderhoud.opmerkingen());
    }
}
