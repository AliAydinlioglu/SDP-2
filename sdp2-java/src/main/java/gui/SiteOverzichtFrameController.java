package gui;

import domain.Gebruiker;
import domain.Site;
import domain.SiteController;
import dto.GebruikerDTO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SiteOverzichtFrameController extends VBox {

    @FXML private TableView<Site> siteTable;
    @FXML private TableColumn<Site, String> naamCol;
    @FXML private TableColumn<Site, String> verantwoordelijkeCol;
    @FXML private TableColumn<Site, Number> aantalMachinesCol;
    @FXML private Label lblStatus;

    private SiteController siteController;
    private GebruikerDTO gebruiker;

    public SiteOverzichtFrameController(SiteController siteController, GebruikerDTO gebruiker) {
        this.siteController = siteController;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SiteOverzichtFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        naamCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNaam()));
        verantwoordelijkeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVerantwoordelijke().getVoornaam() + " " + cellData.getValue().getVerantwoordelijke().getAchternaam()));
        aantalMachinesCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMachines().size()));

        //sites ophalen
        //siteTable.setItems(siteController.getAllSites());
        siteTable.setItems(siteController.getSitesByUserId(gebruiker.id()));

        siteTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Site selectedSite = siteController.getSiteDetails(newSelection);
                lblStatus.setText("Geselecteerd: " + selectedSite.getNaam());
            } else {
                lblStatus.setText("");
            }
        });

        if (siteController.getAllSites().isEmpty()) {
            lblStatus.setText("Geen sites gevonden.");
        }
    }

    private void showSiteDetails(Site site) {
        System.out.println("Details voor: " + site.getNaam());
    }
}