package gui;

import domain.SiteController;
import dto.GebruikerDTO;
import dto.SiteDTO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

    public SiteOverzichtFrameController(SiteController siteController, GebruikerDTO gebruiker) {
        this.siteController = siteController;
        this.gebruiker = gebruiker;

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
                    openSiteDetailPopup(selectedSite);
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

    private void openSiteDetailPopup(SiteDTO selectedSite) {
        try {
            if (this.siteController == null) {
                AlertHelper.showError("Fout", "SiteController is niet geïnitialiseerd.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SiteDetailFrame.fxml"));

            SiteDetailFrameController detailController = new SiteDetailFrameController(this.siteController, selectedSite, this.gebruiker);
            loader.setController(detailController);
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner((Stage) siteTable.getScene().getWindow());
            popupStage.setTitle("Site Details: " + selectedSite.naam());

            Scene popupScene = new Scene(detailController);

            popupScene.getStylesheets().addAll(siteTable.getScene().getStylesheets());

            popupStage.setScene(popupScene);
            popupStage.showAndWait();

        } catch (Exception e) {
            AlertHelper.showError("Kon site details niet openen in popup.", e.getMessage());
            e.printStackTrace();
        }
    }
}
