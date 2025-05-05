package domain;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import domain.builders.LogBuilder;
import dto.GebruikerDTO;
import dto.LogDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import repository.LogDoa;
import repository.LogDoaJpa;
import utils.AlertHelper;

public class LogController {

	private LogDoa logRepo;
	private GebruikerController gebruikerController;
	
	private List<Log> data;
	private ObservableList<LogDTO> logList;
	private ObservableList<LogDTO> filteredLogList;
	private ObservableList<LogDTO> sortedLogList;
	
	private final Comparator<LogDTO> byFirstName = (p1, p2)
            -> p1.gebruiker().voornaam().compareToIgnoreCase(p2.gebruiker().voornaam());

    private final Comparator<LogDTO> byLastName = (p1, p2)
            ->  p1.gebruiker().achternaam().compareToIgnoreCase(p2.gebruiker().achternaam());
	
    private final Comparator<LogDTO> byDate = (p1, p2)
			-> p1.date().compareTo(p2.date());
			
	private final Comparator<LogDTO> sortOrder = byFirstName.thenComparing(byLastName).
		            thenComparing(byDate);		
	
	public LogController() {
		logRepo = new LogDoaJpa();
		initData();
	}
	
	public LogController(LogDoa repo) {
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
			Log l = new LogBuilder()
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
	
	
}
