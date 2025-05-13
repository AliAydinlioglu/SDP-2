package gui;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;

import domain.LogController;
import dto.LogDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class LogListController extends VBox {
	
	@FXML
    private TableView<LogDTO> logTable;
	
	@FXML
    private TableColumn<LogDTO, String> actieCol;

    @FXML
    private TableColumn<LogDTO, String> datumCol;

    @FXML
    private TableColumn<LogDTO, String> emailCol;

    @FXML
    private TableColumn<LogDTO, String> opmerkingCol;

    @FXML
    private TableColumn<LogDTO, String> tijdCol;
    
    @FXML
    private TextField txtFilter;
    
    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<String> presetRangeBox;
    
    private LogController logController;
    
    public LogListController(LogController logController) {
    	this.logController = logController;
    	
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LogListFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        
        emailCol.setCellValueFactory(celldata -> new SimpleStringProperty(celldata.getValue().gebruiker().email()));
        actieCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().actie()));
        datumCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().date().toLocalDate().toString()));
        tijdCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().date().toLocalTime().toString()));
        opmerkingCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().opmerking()));
        
        logTable.setItems(logController.getAll());
        
        presetRangeBox.setValue("Voorbije Week");
        startDatePicker.valueProperty().set(LocalDate.now().minusDays(7));
        endDatePicker.valueProperty().set(LocalDate.now());
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> filter(null));
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> filter(null));
        logTable.setPlaceholder(new Label("Geen logs gevonden voor de opgegeven filters."));

        initialize();
    }
    
    
    @FXML
    private void filter(KeyEvent event) {
        String newValue = txtFilter.getText();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
//        System.out.println(end);
//        System.out.println(start);
        logController.changeFilter(newValue, start, end);
    }
    
    
    private void initialize() {
        presetRangeBox.setOnAction(e -> {
            String selected = presetRangeBox.getValue();
            LocalDate today = LocalDate.now();
            LocalDate start = null, end = null;

            switch (selected) {
                case "Vandaag" -> start = end = today;
                case "Deze Week" -> {
                    start = today.with(DayOfWeek.MONDAY);
                    end = today.with(DayOfWeek.SUNDAY);
                }
                case "Vorige Week" -> {
                    start = today.minusWeeks(1).with(DayOfWeek.MONDAY);
                    end = start.plusDays(6);
                }
                case "Deze Maand" -> {
                    start = today.withDayOfMonth(1);
                    end = today.withDayOfMonth(today.lengthOfMonth());
                }
                case "Vorige Maand" -> {
                    LocalDate firstLastMonth = today.minusMonths(1).withDayOfMonth(1);
                    start = firstLastMonth;
                    end = firstLastMonth.withDayOfMonth(firstLastMonth.lengthOfMonth());
                }
                case "Voorbije Week" ->{
                	start = today.minusDays(7);
                	end = today;
                }
                default -> {
                	startDatePicker.setValue(null);
                	endDatePicker.setValue(null);
                	filter(null);
                    return;
                }
            }

            startDatePicker.setValue(start);
            endDatePicker.setValue(end);
        });
    }

}
