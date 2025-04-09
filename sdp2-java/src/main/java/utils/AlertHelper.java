package utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public final class AlertHelper {
	
	private AlertHelper() {}
	
	public static void showError(String title, String message) {
        showAlert(AlertType.ERROR, title, message);
    }

    public static void showInfo(String title, String message) {
        showAlert(AlertType.INFORMATION, title, message);
    }

    public static void showWarning(String title, String message) {
        showAlert(AlertType.WARNING, title, message);
    }

    public static void showConfirmation(String title, String message) {
        showAlert(AlertType.CONFIRMATION, title, message);
    }

    private static void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
