package domain;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import dto.GebruikerDTO;
import dto.LogDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import repository.LogDao;
import repository.LogDaoJpa;
import utils.AlertHelper;

public class LogController {

	private LogDao logRepo;
	private GebruikerController gebruikerController;
	
	private List<Log> data;
	private ObservableList<LogDTO> logList;
	private FilteredList<LogDTO> filteredLogList;
	private SortedList<LogDTO> sortedLogList;
	
	private final Comparator<LogDTO> byFirstName = (p1, p2)
            -> p1.gebruiker().voornaam().compareToIgnoreCase(p2.gebruiker().voornaam());

    private final Comparator<LogDTO> byLastName = (p1, p2)
            ->  p1.gebruiker().achternaam().compareToIgnoreCase(p2.gebruiker().achternaam());
	
    private final Comparator<LogDTO> byDate = (p1, p2)
			-> p1.date().compareTo(p2.date());
			
	private final Comparator<LogDTO> sortOrder = byFirstName.thenComparing(byLastName).
		            thenComparing(byDate);		
	
	public LogController() {
		logRepo = new LogDaoJpa();
		initData();
	}
	
	public LogController(LogDao repo) {
		logRepo = repo;
		initData();
	}
	
	private void initData() {
		try {
            
            data = logRepo.findAll();
        } catch (Exception e) {
            AlertHelper.showError("Connectie met databank mislukt", e.getMessage());     
        }
		
		logList = FXCollections.observableArrayList(data.stream()
				.map(LogDTO::fromEntity)
				.collect(Collectors.toList()));
		filteredLogList = new FilteredList<>(logList, p -> true);
		sortedLogList = new SortedList<>(filteredLogList, sortOrder);
		gebruikerController = new GebruikerController();
	}
	
	public LogDTO getLog(int id) {
		return LogDTO.fromEntity(logRepo.get(id));
	}
	
	public ObservableList<LogDTO> getAll(){
		return sortedLogList;
	}
	
	public void addLog(GebruikerDTO g, String actie, String opmerking) {
		Gebruiker gebruiker = gebruikerController.getRealGebruiker(g.id());
		try {
			Log l = Log.builder()
					.actie(actie)
					.gebruiker(gebruiker)
					.opmerking(opmerking)
					.build();
			logRepo.startTransaction();
			logRepo.insert(l);
			logRepo.commitTransaction();
			logList.add(LogDTO.fromEntity(l));
			data.add(l);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			logRepo.rollbackTransaction();
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	public void changeFilter(String filterValue, LocalDate startDate, LocalDate endDate) {
	    filteredLogList.setPredicate(log -> {
	        // TEXT FILTER
	        boolean matchesText = true;
	        if (filterValue != null && !filterValue.isBlank()) {
	            String lowerCaseValue = filterValue.toLowerCase();
	            matchesText = log.gebruiker().email().toLowerCase().contains(lowerCaseValue)
	                       || log.actie().toLowerCase().contains(lowerCaseValue);
	        }

	        // DATE FILTER
	        boolean matchesDate = true;
	        if (startDate != null && endDate != null) {
	            LocalDate logDate = log.date().toLocalDate();
	            matchesDate = !logDate.isBefore(startDate) && !logDate.isAfter(endDate);
	        }

	        return matchesText && matchesDate;
	    });
	}
	
	
}
