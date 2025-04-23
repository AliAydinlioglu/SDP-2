package gui;

import java.io.IOException;
import java.net.URL;

import domain.Gebruiker;
import domain.GebruikerController;
import dto.GebruikerDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import utils.AlertHelper;

public class GebruikersListFrameController extends VBox {
	
	@FXML
	private TableView<GebruikerDTO> gebruikersTable;
	
	@FXML
	private TableColumn<GebruikerDTO, String> voornaamCol;
	
	@FXML
	private TableColumn<GebruikerDTO, String> achternaamCol;
	
	@FXML
	private TableColumn<GebruikerDTO, String> emailCol;
	
	@FXML
    private TableColumn<GebruikerDTO, String> rolCol;
	
	@FXML
	private TextField txtFilter;
	
	@FXML private VBox detailBox;
	@FXML private Label lblFullName;
	@FXML private Label lblEmail;
	@FXML private Label lblRol;
	@FXML private Label lblGeboorteDatum;
	@FXML private Label lblAdres;
	@FXML private Label lblGsm;
	
	
    @FXML
    private Button addBtn;

	private GebruikerController dc;
	
	private GebruikerDTO selectedGebruiker;
	

	
	public GebruikersListFrameController(GebruikerController controller) {
		dc = controller;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GebruikersListFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        
        voornaamCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().voornaam()));
        achternaamCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().achternaam()));
        emailCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().email()));
        rolCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().rol().toString().toLowerCase()));
        
        gebruikersTable.setItems(dc.findAll());
        
        gebruikersTable.setRowFactory(new Callback<TableView<GebruikerDTO>, TableRow<GebruikerDTO>>() {
            @Override
            public TableRow<GebruikerDTO> call(TableView<GebruikerDTO> tableView) {
                return new TableRow<GebruikerDTO>() {
                    @Override
                    protected void updateItem(GebruikerDTO gebruiker, boolean empty) {
                        super.updateItem(gebruiker, empty);

                        if (gebruiker == null || empty) {
                            setStyle("");
                        } else if (!gebruiker.actief() && !isSelected()) {
                            setStyle("-fx-background-color: #e0e0e0;"); // light grey for inactive users
                        } else {
                            setStyle("");
                        }
                    }
                };
            }
        });
        gebruikersTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                GebruikerDTO selected = gebruikersTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showDetails(selected);
                    selectedGebruiker = selected;
                }
            }
        });
        
		
	}	
	
	@FXML
    private void filter(KeyEvent event) {
        String newValue = txtFilter.getText();
        dc.changeFilter(newValue);
    }
	
	@FXML
	private void clearSelectedGebruiker() {
	    detailBox.setVisible(false);
	}

	@FXML
	private void editSelectedGebruiker() {
	    if (selectedGebruiker != null) {
	    	updateGebruiker(selectedGebruiker);
	    }
	}


	
	@FXML
	private void addGebruiker() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/AddOrEditGebruikerFrame.fxml"));
			Parent root = loader.load();

			AddOrEditGebruikerFrameController controller = loader.getController();
			controller.initData(this.dc);

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Gebruiker Toevoegen");
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			dialogStage.setScene(new Scene(root));
			dialogStage.showAndWait();
			
			gebruikersTable.refresh();


	    } catch (IOException e) {
	        AlertHelper.showError("Gebruiker opslaan mislukt", e.getMessage());
	    }
		
	}
	
	private void updateGebruiker(GebruikerDTO gebruiker) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/AddOrEditGebruikerFrame.fxml"));
			Parent root = loader.load();

			AddOrEditGebruikerFrameController controller = loader.getController();
			controller.initData(this.dc, gebruiker);

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Gebruiker Aanpassen");
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			dialogStage.setScene(new Scene(root));
			dialogStage.showAndWait();
			
			gebruikersTable.refresh();
		


	    } catch (IOException e) {
	        AlertHelper.showError("Gebruiker aanpassen mislukt", e.getMessage());
	    }
	}
	
	private void showDetails(GebruikerDTO g) {
	    lblFullName.setText("Naam: " + g.voornaam() + " " + g.achternaam());
	    lblEmail.setText("Email: " + g.email());
	    lblRol.setText("Rol: " + g.rol().name());
	    lblGeboorteDatum.setText("Geboortedatum: " + g.geboortedatum().toString());
	    lblAdres.setText("Adres: " + g.adres().toString());
	    lblGsm.setText("GSM: " + g.gsm());

	    detailBox.setVisible(true);
	}


}
