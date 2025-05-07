package gui;

import java.io.IOException;
import java.time.LocalDateTime;

import domain.GebruikerController;
import domain.LogController;
import dto.GebruikerDTO;
import enums.Rol;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
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
	@FXML
    private ChoiceBox<Rol> rolBox;
	
	@FXML
	private CheckBox actiefCB;
	
	@FXML
	private CheckBox nonActiefCB;
	
	private GebruikerDTO ingelogdeGebruiker;
	
	private LogController logController;
	

	
	public GebruikersListFrameController(GebruikerController controller, GebruikerDTO gebruiker, LogController logController) {
		dc = controller;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GebruikersListFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        
        ingelogdeGebruiker = gebruiker;
        this.logController = logController;
        
        voornaamCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().voornaam()));
        achternaamCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().achternaam()));
        emailCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().email()));
        rolCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().rol().toString().toLowerCase()));
        
        
        SortedList<GebruikerDTO> sortedList = (SortedList<GebruikerDTO>) dc.findAll(); // or wrap it in a new SortedList if it's not
        sortedList.comparatorProperty().bind(gebruikersTable.comparatorProperty());
        gebruikersTable.setItems(sortedList);
        

        
        gebruikersTable.setRowFactory(new Callback<>() {
            @Override
            public TableRow<GebruikerDTO> call(TableView<GebruikerDTO> tableView) {
                return new TableRow<>() {
                    @Override
                    protected void updateItem(GebruikerDTO gebruiker, boolean empty) {
                        super.updateItem(gebruiker, empty);
                        

                        if (gebruiker == null || empty) {
                            setStyle("");
                        } else if (!gebruiker.actief()) {
                            // Only apply grey background if not selected
                            if (!isSelected()) {
                                setStyle("-fx-background-color: #e0e0e0;");
                            } else {
                                setStyle(""); // clear style so selection color shows
                            }
                        } else {
                            setStyle(""); // default style for active users
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
        		gebruikersTable.getSelectionModel().clearSelection();
            }
        });
        
//        gebruikersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
//            gebruikersTable.refresh(); 
//        });
        
        rolBox.getItems().add(null);
        rolBox.getItems().addAll(Rol.values());
        
        rolBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            filter(null);
        });
        
        rolBox.setConverter(new StringConverter<Rol>() {
			
			@Override
			public String toString(Rol rol) {
				return rol == null ? "alle rollen": rol.toString().toLowerCase();

			}

			@Override
			public Rol fromString(String string) {
				return null;
			}
		});
        
        actiefCB.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                nonActiefCB.setSelected(false);
            }
            filter(null);
        });

        nonActiefCB.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                actiefCB.setSelected(false);
            }
            filter(null);
        });
        
        gebruikersTable.setPlaceholder(new Label("Geen gebruikers gevonden voor de opgegeven filters."));

        
		
	}	
	
	@FXML
    private void filter(KeyEvent event) {
		gebruikersTable.getSelectionModel().clearSelection();
        String newValue = txtFilter.getText();
        Rol rol = rolBox.getValue();
        dc.changeFilter(newValue, rol, actiefCB.isSelected(), nonActiefCB.isSelected());
    }
	
	@FXML
	private void clearSelectedGebruiker() {
		gebruikersTable.getSelectionModel().clearSelection();
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
			controller.initData(this.dc, logController, ingelogdeGebruiker);

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Gebruiker Toevoegen");
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			dialogStage.initOwner(this.getScene().getWindow());
			dialogStage.setScene(new Scene(root));
			dialogStage.setResizable(false);
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
			controller.initData(this.dc, gebruiker, logController, ingelogdeGebruiker);

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Gebruiker Aanpassen");
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			dialogStage.initOwner(this.getScene().getWindow());
			dialogStage.setScene(new Scene(root));
			dialogStage.setResizable(false);
			dialogStage.showAndWait();
			
			gebruikersTable.refresh();
			


	    } catch (IOException e) {
	        AlertHelper.showError("Gebruiker aanpassen mislukt", e.getMessage());
	    }
	}
	
	private void showDetails(GebruikerDTO g) {
	    lblFullName.setText("Naam: " + g.voornaam() + " " + g.achternaam());
	    lblEmail.setText("Email: " + g.email());
	    lblRol.setText("Rol: " + g.rol().name().toLowerCase());
	    lblGeboorteDatum.setText("Geboortedatum: " + g.geboortedatum().toString());
	    lblAdres.setText("Adres: " + g.adres().toString());
	    lblGsm.setText("Telefoon: " + g.gsm());

	    detailBox.setVisible(true);
	}


}
