package gui;

import domain.LogController;
import domain.SiteController;
import dto.GebruikerDTO;
import dto.SiteDTO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import utils.AlertHelper;

import java.io.IOException;

public class SiteOverzichtFrameController extends VBox {

    @FXML private TableView<SiteDTO> siteTable;
    @FXML private TableColumn<SiteDTO, String> naamCol;
    @FXML private TableColumn<SiteDTO, String> verantwoordelijkeCol;
    @FXML private TableColumn<SiteDTO, Number> aantalMachinesCol;
    @FXML private Label lblStatus;

    private final SiteController siteController;
    private final GebruikerDTO gebruiker;
    private LogController logController;

    public SiteOverzichtFrameController(SiteController siteController, GebruikerDTO gebruiker, LogController logController) {
        this.siteController = siteController;
        this.gebruiker = gebruiker;
        this.logController = logController;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SiteOverzichtFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        initializeTableColumns();
        initializeEventListeners();
        loadSites();
    }

    private void initializeTableColumns() {
        naamCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().naam()));
        verantwoordelijkeCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().verantwoordelijke().voornaam() + " " + cellData.getValue().verantwoordelijke().achternaam()));
        aantalMachinesCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().machines().size()));
    }

    private void initializeEventListeners() {
        siteTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                SiteDTO selectedSite = siteController.getSiteDetails(newSelection);
                lblStatus.setText("Geselecteerd: " + selectedSite.naam());
            } else {
                lblStatus.setText("");
            }
        });

        siteTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                SiteDTO selectedSite = siteTable.getSelectionModel().getSelectedItem();
                if (selectedSite != null) {
                    openSiteDetailFrame(selectedSite);
                }
            }
        });
    }

    private void loadSites() {
        siteTable.setItems(siteController.getSitesByUserId(gebruiker.id()));
        if (siteController.getAllSites().isEmpty()) {
            lblStatus.setText("Geen sites gevonden.");
        }
    }

    private void openSiteDetailFrame(SiteDTO selectedSite) {
        try {
            SiteDetailFrameController detailFrame = new SiteDetailFrameController(siteController, selectedSite, gebruiker, logController);
            
            // Zoek de MainFrameController via de huidige Scene
            MainFrameController mainFrame = (MainFrameController) this.getScene().getRoot();
            
            // Update alleen de mainView van de MainFrameController
            mainFrame.getMainView().getChildren().setAll(detailFrame);
        } catch (Exception e) {
            AlertHelper.showError("Could not open site details.", e.getMessage());
            e.printStackTrace();
        }
    }

}
