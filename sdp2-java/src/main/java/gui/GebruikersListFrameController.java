package gui;

import java.io.IOException;

import domain.Gebruiker;
import domain.GebruikerController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

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
	private TextField txtFilter;
	
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
		
	}	
	
	@FXML
    private void filter(KeyEvent event) {
        String newValue = txtFilter.getText();
        dc.changeFilter(newValue);
    }


}
