package gui;

import domain.LogController;
import domain.SiteController;
import domain.GebruikerController;
import domain.LogController;
import enums.Rol;
import dto.GebruikerDTO;
import dto.SiteDTO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import utils.AlertHelper;
import javafx.stage.Window;


import java.io.IOException;

public class SiteOverzichtFrameController extends VBox {

    @FXML private TableView<SiteDTO> siteTable;
    @FXML private TableColumn<SiteDTO, String> naamCol;
    @FXML private TableColumn<SiteDTO, String> verantwoordelijkeCol;
    @FXML private TableColumn<SiteDTO, Number> aantalMachinesCol;
    @FXML private Label lblStatus;
    @FXML private Button btnSiteToevoegen;
    @FXML private Button btnSiteVerwijderen;

    private final SiteController siteController;
    private final GebruikerDTO gebruiker;
    private GebruikerController gebruikerController;
    private LogController logController;

    private FilteredList<SiteDTO> filteredSiteList;
    private SortedList<SiteDTO> sortedSiteList;

    public SiteOverzichtFrameController(SiteController siteController, GebruikerDTO gebruiker,GebruikerController gebruikerController, LogController logController ) {
        this.siteController = siteController;
        this.gebruiker = gebruiker;
        this.gebruikerController = gebruikerController;
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
        initializeTableDataBindingAndFiltering();
        initializeEventListeners();
        setupKnoppenAutorisatieEnBindings();
        updateStatusLabel();
    }

    private void initializeTableColumns() {
        naamCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().naam()));
        verantwoordelijkeCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().verantwoordelijke().voornaam() + " " + cellData.getValue().verantwoordelijke().achternaam()));
        aantalMachinesCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().machines().size()));
    }
    private void initializeTableDataBindingAndFiltering() {
        ObservableList<SiteDTO> basisSiteList = siteController.getAllSites();
        filteredSiteList = new FilteredList<>(basisSiteList, p -> true);
        setFilterPredicateBasedOnRole();
        sortedSiteList = new SortedList<>(filteredSiteList);
        sortedSiteList.comparatorProperty().bind(siteTable.comparatorProperty());
        siteTable.setItems(sortedSiteList);
    }
    private void setFilterPredicateBasedOnRole() {
        if (gebruiker == null) {
            filteredSiteList.setPredicate(site -> false);
            return;
        }

        if (gebruiker.rol() == Rol.MANAGER || gebruiker.rol() == Rol.ADMINISTRATOR) {
            filteredSiteList.setPredicate(site -> true);
        } else if (gebruiker.rol() == Rol.VERANTWOORDELIJKE || gebruiker.rol() == Rol.GEBRUIKER ) {
            final int gebruikerId = gebruiker.id();
            filteredSiteList.setPredicate(site ->
                    site.verantwoordelijke() != null && site.verantwoordelijke().id() == gebruikerId
            );
        } else {
            filteredSiteList.setPredicate(site -> false);
        }
    }

    private void updateStatusLabel() {
        if (filteredSiteList == null || filteredSiteList.isEmpty()) {
            if (gebruiker != null && (gebruiker.rol() == Rol.VERANTWOORDELIJKE || gebruiker.rol() == Rol.GEBRUIKER)) {
                lblStatus.setText("U bent niet verantwoordelijk voor sites of er zijn geen sites toegewezen.");
            } else {
                lblStatus.setText("Geen sites gevonden voor de huidige weergave.");
            }
        } else {
            lblStatus.setText("");
        }
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

        if (btnSiteToevoegen != null) {
            btnSiteToevoegen.setOnAction(event -> handleSiteToevoegen());
        } else {
            System.err.println("Waarschuwing: btnSiteToevoegen is null. Controleer fx:id in SiteOverzichtFrame.fxml.");
        }
        if (btnSiteVerwijderen != null) {
            btnSiteVerwijderen.setOnAction(event -> handleSiteVerwijderen());
        } else {
            System.err.println("Waarschuwing: btnSiteVerwijderen is null.");
        }
    }
    private void setupKnoppenAutorisatieEnBindings() {
        boolean magBeheren = gebruiker != null &&
                (gebruiker.rol() == Rol.ADMINISTRATOR || gebruiker.rol() == Rol.MANAGER);

        if (btnSiteToevoegen != null) {
            btnSiteToevoegen.setVisible(magBeheren);
            btnSiteToevoegen.setManaged(magBeheren);
        }

        if (btnSiteVerwijderen != null) {
            btnSiteVerwijderen.setVisible(magBeheren);
            btnSiteVerwijderen.setManaged(magBeheren);
            btnSiteVerwijderen.disableProperty().bind(siteTable.getSelectionModel().selectedItemProperty().isNull());
        }
    }

    private void openSiteDetailPopup(SiteDTO selectedSite) {
        try {
            if (this.siteController == null) {
                AlertHelper.showError("Fout", "SiteController is niet geïnitialiseerd.");
                return;
            }

            // Create the SiteDetailFrameController
            SiteDetailFrameController detailController = new SiteDetailFrameController(
                this.siteController, selectedSite, this.gebruiker, logController
            );

            // Retrieve the MainFrameController from the current scene
            MainFrameController mainFrame = (MainFrameController) this.getScene().getRoot();

            // Update the mainView of the MainFrameController
            mainFrame.getMainView().getChildren().setAll(detailController);

        } catch (Exception e) {
            AlertHelper.showError("Kon site details niet openen in de hoofdweergave.", e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleSiteToevoegen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/AddOrEditSiteFrame.fxml"));
            Parent root = loader.load();

            AddOrEditSiteFrameController controller = loader.getController();

            if (this.siteController == null || this.gebruikerController == null || this.logController == null) {
                AlertHelper.showError("Interne Fout", "Benodigde controllers zijn niet beschikbaar in SiteOverzichtFrameController.");
                return;
            }
            if (this.gebruiker == null) {
                AlertHelper.showError("Interne Fout", "Ingelogde gebruiker niet beschikbaar.");
                return;
            }

            controller.initData(this.siteController, this.gebruikerController, this.logController, this.gebruiker);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Nieuwe Site Toevoegen");
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            Window ownerWindow = this.getScene() != null ? this.getScene().getWindow() : null;
            if (ownerWindow == null && siteTable != null && siteTable.getScene() != null) {
                ownerWindow = siteTable.getScene().getWindow();
            }
            if (ownerWindow instanceof Stage) {
                dialogStage.initOwner((Stage) ownerWindow);
            }

            Scene scene = new Scene(root);
            if(ownerWindow != null && ownerWindow.getScene() != null) {
                scene.getStylesheets().addAll(ownerWindow.getScene().getStylesheets());
            }
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            dialogStage.showAndWait();

        } catch (IOException e) {
            AlertHelper.showError("Fout bij openen formulier", "Kon het formulier voor het toevoegen van een site niet laden: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalStateException e) {
            AlertHelper.showError("Fout bij laden FXML", "Controller niet gevonden of verkeerd geconfigureerd: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AlertHelper.showError("Fout", "Er is een onverwachte fout opgetreden bij het openen van het toevoeg-formulier: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void handleSiteVerwijderen() {
        SiteDTO selectedSite = siteTable.getSelectionModel().getSelectedItem();

        if (selectedSite == null) {
            AlertHelper.showWarning("Geen selectie", "Selecteer eerst een site om te verwijderen.");
            return;
        }
        try {
            siteController.deleteSite(selectedSite.id(), this.gebruiker);
            lblStatus.setText("Site '" + selectedSite.naam() + "' verwijderd.");

        } catch (IllegalStateException e) {
            AlertHelper.showError("Verwijderen Mislukt", e.getMessage());
        } catch (IllegalArgumentException e) {
            AlertHelper.showWarning("Verwijderen Mislukt", e.getMessage());
        } catch (RuntimeException e) {
            AlertHelper.showError("Fout bij verwijderen", "Kon de site niet verwijderen: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
