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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
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
        this.siteController = new SiteController();

        this.currentGebruikerEntity = this.gebruikerController.getRealGebruiker(this.gebruikerDTO.id());
        if (this.currentGebruikerEntity == null) {
            AlertHelper.showError("Gebruikersfout", "Kan huidige gebruiker niet laden.");
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
            notificatiesController.handleNieuwStatusVoorSessie(currentGebruikerEntity);
            updateNotificationBadge();
        }
    }

    private void init() {
        loggedInGebruiker.setText(gebruikerDTO.voornaam() + " " + gebruikerDTO.achternaam());

        machineListController = new MachineListFrameController(machineController, onderhoudController, gebruikerDTO,
                logController);
        siteOverzichtController = new SiteOverzichtFrameController(siteController, gebruikerDTO, gebruikerController,
                logController);
        notificatiesOverzichtFrameController = new NotificatiesOverzichtFrameController(notificatiesController,
                gebruikerController, machineController, onderhoudController, siteController, currentGebruikerEntity,
                this);

        Button machinesButton = new Button("Machines");
        Button sitesButton = new Button("Sites");

        sidebar.getChildren().addAll(sitesButton, machinesButton);

        machinesButton.getStyleClass().add("sidebar-button");
        sitesButton.getStyleClass().add("sidebar-button");

        sidebar.setPadding(new Insets(20));
        root.setLeft(sidebar);

        machinesButton.setOnAction(e -> onButtonClick(machinesButton));
        sitesButton.setOnAction(e -> onButtonClick(sitesButton));

        if (gebruikerDTO.rol().equals(Rol.ADMINISTRATOR)) {
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
            mainView.getChildren().setAll(siteOverzichtController);
            sitesButton.setStyle("-fx-underline: true;");
            selectedButton = sitesButton;
        }

        ImageView logoView = new ImageView(
                new Image(getClass().getResource("/images/delaware-logo-opengraph.png").toExternalForm()));
        logoView.setFitWidth(75);
        logoView.setPreserveRatio(true);
        logoView.setSmooth(true);
        logoView.setCache(true);

        VBox spacer = new VBox();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        sidebar.getChildren().addAll(spacer, logoView);

        ImageView bellIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/bell.png")));
        bellIcon.setFitHeight(20);
        bellIcon.setFitWidth(20);
        notificationsBtn.setGraphic(bellIcon);
        notificationsBtn.setText("");
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
            if (onderhoudFrameController == null) {
                onderhoudFrameController = new OnderhoudFrameController(onderhoudController, gebruikerDTO, null,
                        logController);
            }
            mainView.getChildren().setAll(onderhoudFrameController);
        } else if (clickedButton.getText().equals("Sites")) {
            mainView.getChildren().setAll(siteOverzichtController);
        } else if (clickedButton.getText().equals("Gebruikers")) {
            mainView.getChildren().setAll(gebruikersListController);
        } else if (clickedButton.getText().equals("Logs")) {
            mainView.getChildren().setAll(logListFrameController);
        }
    }

    @FXML
    void LogOut(ActionEvent event) {
        boolean confirmed = AlertHelper.showConfirmationAndWait("Bevestiging", "Weet je zeker dat je wilt uitloggen?");
        if (!confirmed)
            return;

        Scene scene = new Scene(new LoginFrameController(gebruikerController, stage));
        stage.setScene(scene);
        stage.setFullScreen(false);
        stage.setTitle("Login");
    }

    @FXML
    void showNotifications(ActionEvent event) {
        if (currentGebruikerEntity == null) {
            AlertHelper.showError("Fout", "Kan gebruiker niet laden voor notificaties.");
            return;
        }
        notificatiesOverzichtFrameController.refreshNotifications();
        mainView.getChildren().setAll(notificatiesOverzichtFrameController);

        if (selectedButton != null) {
            selectedButton.setStyle("-fx-underline: false;");
            selectedButton = null;
        }
    }

    public void updateNotificationBadge() {
        if (currentGebruikerEntity == null)
            return;

        long unreadCount = notificatiesController.getUnreadNotificatieCountForGebruiker(currentGebruikerEntity);
        Platform.runLater(() -> {
            if (unreadCount > 0) {
                notificationsBtn.setText(String.valueOf(unreadCount));
                notificationsBtn.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            } else {
                notificationsBtn.setText("");
                notificationsBtn.setStyle(null);
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
