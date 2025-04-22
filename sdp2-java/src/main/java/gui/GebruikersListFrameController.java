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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class GebruikersListFrameController extends VBox {
	
	@FXML
	private TableView<GebruikerDTO> gebruikersTable;
	
	@FXML
	private TableColumn<GebruikerDTO, String> voornaamCol;
	
	@FXML
	private TableColumn<GebruikerDTO, String> achternaamCol;
	
	@FXML
	private TableColumn<GebruikerDTO, String> emailCol;
	
//	@FXML
//	private TableColumn<GebruikerDTO, Void> verwijderCol;
//	
//	@FXML TableColumn<GebruikerDTO, Void> UpdateCol;
	
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
        
        voornaamCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().voornaam()));
        achternaamCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().achternaam()));
        emailCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().email()));
        
        gebruikersTable.setItems(dc.findAll());
        
        addActionButtonsToTable();
		
	}	
	
	@FXML
    private void filter(KeyEvent event) {
        String newValue = txtFilter.getText();
        dc.changeFilter(newValue);
    }
	
	@FXML
	private TableColumn<GebruikerDTO, Void> actionCol; // rename this to something general

	private void addActionButtonsToTable() {
	    Callback<TableColumn<GebruikerDTO, Void>, TableCell<GebruikerDTO, Void>> cellFactory = new Callback<>() {
	        @Override
	        public TableCell<GebruikerDTO, Void> call(final TableColumn<GebruikerDTO, Void> param) {
	            return new TableCell<>() {

	                private final Button updateBtn = new Button("✎");
	                private final Button deleteBtn = new Button("🗑");
	                private final HBox hbox = new HBox(5, updateBtn, deleteBtn);

	                {
	                    updateBtn.setOnAction(event -> {
	                        GebruikerDTO gebruiker = getTableView().getItems().get(getIndex());
	                        updateGebruiker(gebruiker); // call your update logic
	                    });

	                    deleteBtn.setOnAction(event -> {
	                        GebruikerDTO gebruiker = getTableView().getItems().get(getIndex());
	                        dc.removeGebruiker(gebruiker); // call your delete logic
	                    });

	                    hbox.setStyle("-fx-alignment: CENTER;");
	                }

	                @Override
	                protected void updateItem(Void item, boolean empty) {
	                    super.updateItem(item, empty);
	                    if (empty) {
	                        setGraphic(null);
	                    } else {
	                        setGraphic(hbox);
	                    }
	                }
	            };
	        }
	    };

	    actionCol.setCellFactory(cellFactory);
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
	        e.printStackTrace();
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
	        e.printStackTrace();
	    }
	}


}
