package gui;

import java.io.IOException;

import domain.GebruikerController;
import domain.LogController;
import domain.MachineController;
import domain.NotificatiesController;
import domain.OnderhoudController;
import domain.SiteController;
import domain.Gebruiker;
import dto.GebruikerDTO;
import enums.Rol;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import utils.AlertHelper;

public class MainFrameController extends BorderPane {

    @FXML
    private Button logUitBtn;

    @FXML
    private Button notificationsBtn;

    @FXML
    private Label loggedInGebruiker;

    @Getter
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
    private LogListController logListFrameController;
    private NotificatiesOverzichtFrameController notificatiesOverzichtFrameController;

    private Stage stage;
    private GebruikerDTO gebruikerDTO;
    private Gebruiker currentGebruikerEntity;

    private GebruikerController gebruikerController;
    private NotificatiesController notificatiesController;
    private LogController logController;
    private MachineController machineController;
    private OnderhoudController onderhoudController;
    private SiteController siteController;

    public MainFrameController(GebruikerDTO gebruikerDTO, Stage stage) {
        this.stage = stage;
        this.gebruikerDTO = gebruikerDTO;

        this.gebruikerController = new GebruikerController();
        this.notificatiesController = new NotificatiesController();
        this.logController = new LogController();
        this.machineController = new MachineController();
        this.onderhoudController = new OnderhoudController();
        this.siteController = new SiteController(this.gebruikerController, this.logController);

        this.currentGebruikerEntity = this.gebruikerController.getRealGebruiker(this.gebruikerDTO.id());
        if (this.currentGebruikerEntity == null) {
            AlertHelper.showError("Fout", "Ingelogde gebruiker niet gevonden.");
            Platform.exit();
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        init();
        updateNotificationBadge();
        if (currentGebruikerEntity != null) {
            loggedInGebruiker
                    .setText(currentGebruikerEntity.getVoornaam() + " " + currentGebruikerEntity.getAchternaam());
        }
    }

    private void init() {
        if (loggedInGebruiker == null) {
            System.err.println("loggedInGebruiker is null in init()");
        } else {
            loggedInGebruiker
                    .setText(currentGebruikerEntity.getVoornaam() + " " + currentGebruikerEntity.getAchternaam());
        }

        machineListController = new MachineListFrameController(machineController, onderhoudController, gebruikerDTO,
                logController);
        siteOverzichtController = new SiteOverzichtFrameController(siteController, gebruikerDTO, gebruikerController,
                logController);
        notificatiesOverzichtFrameController = new NotificatiesOverzichtFrameController(notificatiesController,
                gebruikerController, machineController, onderhoudController, siteController, currentGebruikerEntity,
                this);

        Button machinesButton = new Button("Machines");
        Button sitesButton = new Button("Sites");

        machinesButton.getStyleClass().add("sidebar-button");
        sitesButton.getStyleClass().add("sidebar-button");

        sidebar.setPadding(new Insets(20));
        root.setLeft(sidebar);

        if (currentGebruikerEntity.getRol().equals(Rol.MANAGER)
                || currentGebruikerEntity.getRol().equals(Rol.VERANTWOORDELIJKE)) {
            sidebar.getChildren().add(sitesButton);
        }
        sidebar.getChildren().add(machinesButton);

        machinesButton.setOnAction(e -> onButtonClick(machinesButton));
        sitesButton.setOnAction(e -> onButtonClick(sitesButton));

        if (currentGebruikerEntity.getRol().equals(Rol.ADMINISTRATOR)) {
            gebruikersListController = new GebruikersListFrameController(gebruikerController, gebruikerDTO,
                    logController);
            Button gebruikersButton = new Button("Gebruikers");

            logListFrameController = new LogListController(logController);
            Button logButton = new Button("Logs");

            gebruikersButton.getStyleClass().add("sidebar-button");
            gebruikersButton.setOnAction(e -> onButtonClick(gebruikersButton));

            logButton.getStyleClass().add("sidebar-button");
            logButton.setOnAction(e -> onButtonClick(logButton));

            sidebar.getChildren().addAll(gebruikersButton, logButton);

            mainView.getChildren().setAll(gebruikersListController);
            gebruikersButton.setStyle("-fx-underline: true;");
            selectedButton = gebruikersButton;

        } else {
            if (sidebar.getChildren().contains(sitesButton) && (currentGebruikerEntity.getRol().equals(Rol.MANAGER)
                    || currentGebruikerEntity.getRol().equals(Rol.VERANTWOORDELIJKE))) {
                mainView.getChildren().setAll(siteOverzichtController);
                sitesButton.setStyle("-fx-underline: true;");
                selectedButton = sitesButton;
            } else if (sidebar.getChildren().contains(machinesButton)) {
                mainView.getChildren().setAll(machineListController);
                machinesButton.setStyle("-fx-underline: true;");
                selectedButton = machinesButton;
            } else {
                mainView.getChildren().setAll(new Label("U heeft geen toegang tot de beschikbare modules."));
            }
        }

        ImageView logoView = new ImageView(new Image(getClass().getResourceAsStream("/images/Delawarelogo.png")));
        logoView.setFitHeight(100);
        logoView.setFitWidth(150);
        logoView.setPreserveRatio(true);

        VBox spacer = new VBox();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        HBox logoContainer = new HBox(logoView);
        logoContainer.setAlignment(Pos.CENTER);

        sidebar.getChildren().addAll(spacer, logoContainer);

        if (notificationsBtn != null) {
            ImageView bellIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/bell.png")));
            bellIcon.setFitHeight(20);
            bellIcon.setFitWidth(20);
            notificationsBtn.setGraphic(bellIcon);
            notificationsBtn.setText("");
        } else {
            System.err.println("notificationsBtn is null in init(). Check FXML and @FXML annotation.");
        }
    }

    private void onButtonClick(Button clickedButton) {
        if (selectedButton != null) {
            selectedButton.setStyle("");
        }
        clickedButton.setStyle("-fx-underline: true;");
        selectedButton = clickedButton;

        if (clickedButton.getText().equals("Machines")) {
            mainView.getChildren().setAll(machineListController);
        } else if (clickedButton.getText().equals("Sites")) {
            mainView.getChildren().setAll(siteOverzichtController);
        } else if (clickedButton.getText().equals("Gebruikers") && gebruikersListController != null) {
            mainView.getChildren().setAll(gebruikersListController);
        } else if (clickedButton.getText().equals("Logs") && logListFrameController != null) {
            mainView.getChildren().setAll(logListFrameController);
        }
    }

    @FXML
    void LogOut(ActionEvent event) {
        try {
            if (logController != null && currentGebruikerEntity != null) {
                logController.addLog(gebruikerDTO, "Gebruiker uitgelogd",
                        "Gebruiker " + currentGebruikerEntity.getEmail() + " is uitgelogd.");
            }

            Stage currentStage = (Stage) root.getScene().getWindow();
            LoginFrameController loginScreen = new LoginFrameController(new GebruikerController(), currentStage);
            Scene scene = new Scene(loginScreen);
            scene.getStylesheets().add(getClass().getResource("/styles/general.css").toExternalForm());
            currentStage.setScene(scene);
            currentStage.setMaximized(false);
            currentStage.setWidth(600);
            currentStage.setHeight(400);
            currentStage.centerOnScreen();
            currentStage.setTitle("Login");
        } catch (Exception e) {
            AlertHelper.showError("Uitloggen mislukt", "Er is een fout opgetreden bij het uitloggen.");
            e.printStackTrace();
        }
    }

    @FXML
    void showNotifications(ActionEvent event) {
        if (notificatiesOverzichtFrameController == null) {
            notificatiesOverzichtFrameController = new NotificatiesOverzichtFrameController(notificatiesController,
                    gebruikerController, machineController, onderhoudController, siteController, currentGebruikerEntity,
                    this);
        }
        mainView.getChildren().setAll(notificatiesOverzichtFrameController);
        if (selectedButton != null) {
            selectedButton.setStyle("");
        }
    }

    public void updateNotificationBadge() {
        if (notificationsBtn == null || notificatiesController == null || currentGebruikerEntity == null)
            return;

        long unreadCount = notificatiesController.getUnreadNotificatieCountForGebruiker(currentGebruikerEntity);
        Platform.runLater(() -> {
            if (unreadCount > 0) {
                notificationsBtn.setText(String.valueOf(unreadCount));
                notificationsBtn.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            } else {
                notificationsBtn.setText("");
                notificationsBtn.setStyle("");
                ImageView bellIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/bell.png")));
                bellIcon.setFitHeight(20);
                bellIcon.setFitWidth(20);
                notificationsBtn.setGraphic(bellIcon);
            }
        });
    }

    public void navigateToMachineDetail(int machineId) {
        if (machineListController != null) {
            mainView.getChildren().setAll(machineListController);
            sidebar.getChildren().stream()
                    .filter(node -> node instanceof Button && ((Button) node).getText().equals("Machines"))
                    .findFirst()
                    .ifPresent(node -> {
                        if (selectedButton != node) {
                            onButtonClick((Button) node);
                        }
                    });
            machineListController.showMachineDetailsById(machineId);
        } else {
            AlertHelper.showWarning("Navigatiefout",
                    "Kan niet naar machine details navigeren. Machine view niet geladen.");
        }
    }

    public void navigateToOnderhoudDetail(int onderhoudId) {
        if (onderhoudFrameController == null) {
            onderhoudFrameController = new OnderhoudFrameController(onderhoudController, gebruikerDTO, null,
                    logController);
        }
        mainView.getChildren().setAll(onderhoudFrameController);
        if (selectedButton != null && !selectedButton.getText().equals("Onderhoud")) {
            selectedButton.setStyle("-fx-underline: false;");
            selectedButton = null;
        }
        onderhoudFrameController.showOnderhoudDetailsById(onderhoudId);
    }
}
