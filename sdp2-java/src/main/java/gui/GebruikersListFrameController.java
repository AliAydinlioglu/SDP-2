package gui;

import java.io.IOException;

import domain.Gebruiker;
import domain.GebruikerController;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
    private Button addBtn;

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
        
        voornaamCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVoornaam()));
        achternaamCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAchternaam()));
        emailCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        
        gebruikersTable.setItems(dc.findAll());
        
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
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/AddGebruikerFrame.fxml"));
			Parent root = loader.load();

			AddGebruikerFrameController controller = loader.getController();
			controller.initData(this.dc);

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Gebruiker Toevoegen");
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			dialogStage.setScene(new Scene(root));
			dialogStage.showAndWait();


	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		
	}


}
