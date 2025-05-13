package utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Window;

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
    
    public static boolean showConfirmationAndWait(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(getActiveWindow());
        return alert.showAndWait().filter(response -> response == javafx.scene.control.ButtonType.OK).isPresent();
    }

    private static void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(getActiveWindow());
        alert.showAndWait();
    }
    
    private static Window getActiveWindow() {
        return javafx.stage.Window.getWindows()
            .stream()
            .filter(Window::isFocused)
            .findFirst()
            .orElse(null);
    }

}
