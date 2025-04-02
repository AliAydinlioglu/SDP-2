package gui;

import java.io.IOException;

import domain.Gebruiker;
import domain.GebruikerController;
import enums.Rol;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class GebruikersListFrameController extends VBox {
	
	@FXML
	private TableView<Gebruiker> gebruikersTable;
	
	@FXML
	private TableColumn<Gebruiker, String> voornaamCol;
	
	@FXML
	private TableColumn<Gebruiker, String> achternaamCol;
	
	@FXML
	private TableColumn<Gebruiker, String> emailCol;
	
	@FXML
	private TableColumn<Gebruiker, Void> actionCol;
	
	@FXML
	private TextField txtFilter;
	
	@FXML
	private TextField voornaamField;
	
	@FXML
	private TextField achternaamField;
	
	@FXML
	private TextField emailField;
	
	@FXML
	private TextField gsmNrField;
	
	@FXML
	private TextField straatField;
	
	@FXML
	private TextField huisNrField;
	
	@FXML
	private TextField postcodeField;
	
	@FXML
	private TextField stadField;
	
	@FXML
	private TextField landField;
	
	@FXML
	private DatePicker geboorteDatumPicker;
	
	@FXML
	private ChoiceBox<Rol> rolBox;

	private GebruikerController dc;
	

	
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
        
        voornaamCol.setCellValueFactory(cellData -> cellData.getValue().voornaamProperty());
        achternaamCol.setCellValueFactory(cellData -> cellData.getValue().achternaamProperty());
        emailCol.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        
        gebruikersTable.setItems(dc.getAll());
        
        rolBox.setItems(FXCollections.observableArrayList(Rol.values()));
        
        addButtonToTable();
		
	}	
	
	@FXML
    private void filter(KeyEvent event) {
        String newValue = txtFilter.getText();
        dc.changeFilter(newValue);
    }
	
	private void addButtonToTable() {
	    actionCol.setCellFactory(param -> new TableCell<>() {
	        private final Button btn = new Button("Remove");

	        {
	            btn.setOnAction(event -> {
	                Gebruiker gebruiker = getTableView().getItems().get(getIndex());
	                dc.removeGebruiker(gebruiker);
	                getTableView().getItems().remove(gebruiker);
	            });
	        }

	        @Override
	        protected void updateItem(Void item, boolean empty) {
	            super.updateItem(item, empty);
	            if (empty) {
	                setGraphic(null);
	            } else {
	                setGraphic(btn);
	            }
	        }
	    });
	}

	
	@FXML
	private void addGebruiker() {
		String naam = achternaamField.getText();
		String voornaam = voornaamField.getText();
		String email = emailField.getText();
		String gsm = gsmNrField.getText();
		String straat = straatField.getText();
		String huisNr = huisNrField.getText();
		String postcode = postcodeField.getText();
		String stad = stadField.getText();
		String land = landField.getText();
		Rol rol = rolBox.getValue();
		
	   if (rol == null) {
	        // Handle the case where rol is not selected
	        System.out.println("Please select a role.");
	        return;
	    }
		
		dc.addGebruiker(naam, voornaam, geboorteDatumPicker.getValue(), straat, huisNr, postcode, stad, land, email, gsm, rol);
		
	}


}
