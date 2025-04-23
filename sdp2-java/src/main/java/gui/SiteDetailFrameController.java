package gui;

import dto.MachineDTO;
import dto.SiteDTO;
import enums.MachineStatus;
import enums.ProductionStatus;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SiteDetailFrameController extends VBox {

    @FXML private Label lblSiteNaam;
    @FXML private Label lblVerantwoordelijke;
    @FXML private Label lblAantalMachines;
    @FXML private TableView<MachineDTO> machineTable;
    @FXML private TableColumn<MachineDTO, String> locatieCol;
    @FXML private TableColumn<MachineDTO, String> statusCol;
    @FXML private TableColumn<MachineDTO, String> prodStatusCol;

    private SiteDTO site;

    public SiteDetailFrameController(SiteDTO site) {
        this.site = site;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SiteDetailFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException("Kon SiteDetailFrame.fxml niet laden", ex);
        }

        populateDetails();
    }

    private void populateDetails() {
        if (site != null) {
            lblSiteNaam.setText("Site Naam: " + site.naam());

            String verantwNaam = "Niet toegewezen";
            if (site.verantwoordelijke() != null) {
                verantwNaam = site.verantwoordelijke().voornaam() + " " + site.verantwoordelijke().achternaam();
            }
            lblVerantwoordelijke.setText("Verantwoordelijke: " + verantwNaam);

            int aantalMachines = (site.machines() != null) ? site.machines().size() : 0;
            lblAantalMachines.setText("Aantal Machines: " + aantalMachines);

            locatieCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().locatie()));
            statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status().name()));
            prodStatusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().productieStatus().name()));

            if (site.machines() != null) {
                machineTable.setItems(FXCollections.observableArrayList(site.machines()));
            } else {
                machineTable.setItems(FXCollections.observableArrayList());
            }

        }
    }
}