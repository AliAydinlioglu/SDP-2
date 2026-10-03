package gui;

import domain.*;
import enums.NotificatieStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.FontPosture;
import utils.AlertHelper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificatiesOverzichtFrameController extends VBox {

    @FXML
    private ListView<Notificatie> notificatiesListView;

    @FXML
    private Label titleLabel;

    private NotificatiesController notificatiesController;
    private Gebruiker currentGebruiker;
    private MainFrameController mainFrameController;

    private ObservableList<Notificatie> notificatiesObservableList;

    public NotificatiesOverzichtFrameController(NotificatiesController notificatiesController,
            GebruikerController gebruikerController,
            MachineController machineController,
            OnderhoudController onderhoudController,
            SiteController siteController,
            Gebruiker currentGebruiker,
            MainFrameController mainFrameController) {
        this.notificatiesController = notificatiesController;
        this.currentGebruiker = currentGebruiker;
        this.mainFrameController = mainFrameController;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/NotificatiesOverzichtFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        titleLabel.setText("Mijn Notificaties");
        notificatiesObservableList = FXCollections.observableArrayList();
        notificatiesListView.setItems(notificatiesObservableList);
        setupListViewCellFactory();
        refreshNotifications();
    }

    private void setupListViewCellFactory() {
        notificatiesListView.setCellFactory(param -> new ListCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            @Override
            protected void updateItem(Notificatie notificatie, boolean empty) {
                super.updateItem(notificatie, empty);
                if (empty || notificatie == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    VBox contentBox = new VBox(5);
                    Label titelLabel = new Label(notificatie.getTitel());
                    titelLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
                    titelLabel.setStyle("-fx-text-fill: black;");

                    Label messageLabel = new Label(notificatie.getMessage());
                    messageLabel.setWrapText(true);
                    messageLabel.setStyle("-fx-text-fill: black;");

                    Label timestampLabel = new Label("Tijd: " + notificatie.getTimestamp().format(formatter));
                    timestampLabel.setFont(Font.font("System", 10));
                    timestampLabel.setStyle("-fx-text-fill: #4A4A4A;");

                    Label statusLabel = new Label("Status: " + notificatie.getStatus().toString());
                    statusLabel.setFont(Font.font("System", FontPosture.ITALIC, 10));
                    statusLabel.setStyle("-fx-text-fill: #4A4A4A;");

                    HBox buttonBox = new HBox(10);
                    Button markAsReadButton = new Button("Markeer als gelezen");
                    markAsReadButton.getStyleClass().add("general");
                    markAsReadButton.setStyle("-fx-pref-width: 140px;");
                    markAsReadButton.setOnAction(e -> {
                        notificatiesController.markeerAlsGelezen(notificatie);
                        refreshNotifications();
                        mainFrameController.updateNotificationBadge();
                    });
                    markAsReadButton.setDisable(notificatie.getStatus() == NotificatieStatus.GELEZEN);

                    Button goToItemButton = new Button("Ga naar item");
                    goToItemButton.getStyleClass().add("general");
                    goToItemButton.setOnAction(e -> handleGoToItem(notificatie));

                    Button deleteButton = new Button("Verwijder");
                    deleteButton.getStyleClass().add("delete-notification");
                    deleteButton.setOnAction(e -> {
                        if (AlertHelper.showConfirmationAndWait("Verwijderen", "Notificatie verwijderen?")) {
                            notificatiesController.verwijderNotificatie(notificatie);
                            refreshNotifications();
                            mainFrameController.updateNotificationBadge();
                        }
                    });

                    buttonBox.getChildren().addAll(markAsReadButton, goToItemButton, deleteButton);

                    contentBox.getChildren().addAll(titelLabel, messageLabel, timestampLabel, statusLabel, buttonBox);
                    setGraphic(contentBox);

                    if (notificatie.getStatus() == NotificatieStatus.NIEUW
                            || notificatie.getStatus() == NotificatieStatus.ONGELEZEN) {
                        setStyle("-fx-background-color: #e6f3ff;");
                    } else {
                        setStyle("-fx-background-color: #f4f4f4;");
                    }
                }
            }
        });
    }

    private void handleGoToItem(Notificatie notificatie) {
        if (notificatie.getItemType() != null && notificatie.getItemId() != 0) {
            if (!notificatie.getStatus().equals(NotificatieStatus.GELEZEN)) {
                notificatiesController.markeerAlsGelezen(notificatie);
                refreshNotifications();
                mainFrameController.updateNotificationBadge();
            }
            switch (notificatie.getItemType().toUpperCase()) {
                case "MACHINE":
                    mainFrameController.navigateToMachineDetail(notificatie.getItemId());
                    break;
                case "ONDERHOUD":
                    mainFrameController.navigateToOnderhoudDetail(notificatie.getItemId());
                    break;
                default:
                    AlertHelper.showWarning("Navigatie", "Kan item type niet herkennen: " + notificatie.getItemType());
                    break;
            }
        } else {
            AlertHelper.showInfo("Navigatie", "Geen item gekoppeld aan deze notificatie.");
        }
    }

    public void refreshNotifications() {
        if (currentGebruiker != null) {
            notificatiesController.handleNieuwStatusVoorSessie(currentGebruiker);

            List<Notificatie> userNotifications = notificatiesController.getNotificatiesVoorGebruiker(currentGebruiker);
            notificatiesObservableList.setAll(userNotifications);
            mainFrameController.updateNotificationBadge();
        }
    }
}
