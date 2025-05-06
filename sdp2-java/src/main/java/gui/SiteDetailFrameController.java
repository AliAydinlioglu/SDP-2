package gui;

import domain.SiteController;
import dto.MachineDTO;
import dto.SiteDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utils.AlertHelper;

import java.io.IOException;

public class SiteDetailFrameController extends HBox {

    @FXML private Label lblSiteNaam;
    @FXML private Label lblVerantwoordelijke;
    @FXML private Label lblAantalMachines;
    @FXML private TableView<MachineDTO> machineTable;
    @FXML private TableColumn<MachineDTO, String> naamCol;
    @FXML private TableColumn<MachineDTO, String> statusCol;
    @FXML private TableColumn<MachineDTO, String> productiestatusCol;
    @FXML private TableColumn<MachineDTO, String> locatieCol;
    @FXML private Button btnTerug;

    private SiteController siteController;
    private SiteDTO selectedSite;
    private SiteOverzichtFrameController siteOverzichtFrameController;

    public SiteDetailFrameController(SiteController siteController, SiteDTO selectedSite, SiteOverzichtFrameController siteOverzichtFrameController) {
        this.siteController = siteController;
        this.selectedSite = selectedSite;
        this.siteOverzichtFrameController = siteOverzichtFrameController;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SiteDetailFrame.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        initializeSiteDetails();
        initializeMachineTable();
        initializeEventListeners();
    }

    private void initializeSiteDetails() {
        
        System.out.println("Selected Site: " + selectedSite);

        lblSiteNaam.setText(selectedSite.naam());
        lblVerantwoordelijke.setText(selectedSite.verantwoordelijke().voornaam() + " " + selectedSite.verantwoordelijke().achternaam());
        lblAantalMachines.setText(String.valueOf(selectedSite.machines().size()));
    }

    private void initializeMachineTable() {
    	
        System.out.println("Initializing machine table with machines: " + selectedSite.machines());

        // Set up the cell value factories for each column
        naamCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().naam()));
        locatieCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().locatie()));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status().name())); // Assuming status is an enum
        productiestatusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().productieStatus().name()));

        // Populate the table with the list of machines from the selected site
        machineTable.setItems(FXCollections.observableArrayList(selectedSite.machines()));
    }
    
    private void initializeEventListeners() {
		btnTerug.setOnAction(event -> handleBackButton());
	}
    
    private void handleBackButton() {
    	try {
    		this.getScene().setRoot(siteOverzichtFrameController);
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Error navigating back to the previous screen: ", e.getMessage());
        }
        
    }

}
