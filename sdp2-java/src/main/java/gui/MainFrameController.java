package gui;

import java.io.IOException;

import domain.GebruikerController;
import domain.MachineController;
import domain.OnderhoudController;
import domain.SiteController;
import dto.GebruikerDTO;
import enums.Rol;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.AlertHelper;

public class MainFrameController extends BorderPane {

    @FXML
    private Button logUitBtn;

    @FXML
    private Label loggedInGebruiker;
    
    @FXML
    private StackPane mainView;

    @FXML
    private VBox sidebar;
    
    @FXML
    private BorderPane root;
    
    private Button selectedButton;
    
    private MachineListFrameController machineListController;
    private OnderhoudFrameController onderhoudFrameController;
    private SiteOverzichtFrameController siteOverzichtController;
    private GebruikersListFrameController gebruikersListController;

    private Stage stage;

    
    GebruikerDTO gebruiker;
    
    public MainFrameController(GebruikerDTO gebruiker, Stage stage) {
    	
    	this.stage = stage;
    	this.gebruiker = gebruiker;
    	
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainFrame.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
        this.gebruiker = gebruiker;
    	
    	
    	init();
    }

    private void init() {
    	
    	loggedInGebruiker.setText(gebruiker.voornaam() + " " + gebruiker.achternaam());
    	
    	
        machineListController = new MachineListFrameController(new MachineController(), new OnderhoudController(), gebruiker);
        onderhoudFrameController = new OnderhoudFrameController(new OnderhoudController(), gebruiker);
        siteOverzichtController = new SiteOverzichtFrameController(new SiteController(), gebruiker);

        Button machinesButton = new Button("Machines");
        Button onderhoudButton = new Button("Onderhoud");
        Button sitesButton = new Button("Sites");

        sidebar.getChildren().addAll(sitesButton, machinesButton, onderhoudButton);
        
        machinesButton.getStyleClass().add("sidebar-button");
        onderhoudButton.getStyleClass().add("sidebar-button");
        sitesButton.getStyleClass().add("sidebar-button");
        
        sidebar.setPadding(new Insets(20));
        root.setLeft(sidebar);

        machinesButton.setOnAction(e -> onButtonClick(machinesButton));
        onderhoudButton.setOnAction(e -> onButtonClick(onderhoudButton));
        sitesButton.setOnAction(e -> onButtonClick(sitesButton));
        
        if(gebruiker.rol().equals(Rol.ADMINISTRATOR)) {
            gebruikersListController = new GebruikersListFrameController(new GebruikerController());
            Button gebruikersButton = new Button("Gebruikers");
            gebruikersButton.getStyleClass().add("sidebar-button");
            gebruikersButton.setOnAction(e -> onButtonClick(gebruikersButton));
            sidebar.getChildren().add(gebruikersButton);
            
            mainView.getChildren().setAll(gebruikersListController);
            gebruikersButton.setStyle("-fx-underline: true;");
            selectedButton = gebruikersButton;

        } else {
        	mainView.getChildren().setAll(siteOverzichtController);
            sitesButton.setStyle("-fx-underline: true;");
            selectedButton = sitesButton;
        }

        
    }

    private void onButtonClick(Button clickedButton) {
        if (selectedButton != null) {
            selectedButton.setStyle("-fx-underline: false;");
        }

        clickedButton.setStyle("-fx-underline: true;");

        selectedButton = clickedButton;

        if (clickedButton.getText().equals("Machines")) {
            mainView.getChildren().setAll(machineListController);
        } else if (clickedButton.getText().equals("Onderhoud")) {
            mainView.getChildren().setAll(onderhoudFrameController);
        } else if (clickedButton.getText().equals("Sites")) {
            mainView.getChildren().setAll(siteOverzichtController);
        } else if (clickedButton.getText().equals("Gebruikers")) {
            mainView.getChildren().setAll(gebruikersListController);
        }
    }

    @FXML
    void LogOut(ActionEvent event) {
    	boolean confirmed = AlertHelper.showConfirmationAndWait("Bevestiging", "Weet je zeker dat je wilt uitloggen?");
        
        if (!confirmed) return; 
    	
    	Scene scene = new Scene(new LoginFrameController(new GebruikerController(), stage));
    	
    	stage.setScene(scene);
    	stage.setFullScreen(false);
    	stage.setTitle("Login");
        
    }



}
