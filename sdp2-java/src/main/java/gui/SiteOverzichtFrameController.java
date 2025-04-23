package gui;

import domain.Gebruiker;
import domain.Site;
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

        naamCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().naam()));
        verantwoordelijkeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().verantwoordelijke().voornaam() + " " + cellData.getValue().verantwoordelijke().achternaam()));
        aantalMachinesCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().machines().size()));

        //sites ophalen
        siteTable.setItems(siteController.getAllSites());
        //siteTable.setItems(siteController.getSitesByUserId(gebruiker.id()));

        siteTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                lblStatus.setText("Geselecteerd: " + newSelection.naam());

                try {
                    SiteDetailFrameController detailView = new SiteDetailFrameController(newSelection);
                    Stage detailStage = new Stage();
                    detailStage.setTitle("Details voor " + newSelection.naam());

                    detailStage.initModality(Modality.APPLICATION_MODAL);
                    Scene scene = new Scene(detailView);

                    detailStage.setScene(scene);

                    detailStage.showAndWait();

                } catch (Exception e) {
                    e.printStackTrace();
                    AlertHelper.showError("Fout", "Kon site details niet laden: " + e.getMessage());
                }

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