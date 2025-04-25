package domain;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import repository.OnderhoudDaoJpa;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import dto.GebruikerDTO;
import dto.MachineDTO;
import dto.OnderhoudDTO;
import enums.MachineStatus;
import enums.OnderhoudStatus;
import enums.Rol;

public class OnderhoudController {
	
	private List<Onderhoud> data;
    private OnderhoudDaoJpa onderhoudDaoJpa;
    private ObservableList<OnderhoudDTO> onderhoudList;
    private FilteredList<OnderhoudDTO> filteredOnderhoudList;
    private SortedList<OnderhoudDTO> sortedOnderhoudList;

    private final Comparator<OnderhoudDTO> byDate = Comparator.comparing(OnderhoudDTO::datum);
    private final Comparator<OnderhoudDTO> byStatus = Comparator.comparing(OnderhoudDTO::status);
    private final Comparator<OnderhoudDTO> sortOrder = byDate.thenComparing(byStatus);

    public OnderhoudController() {
        onderhoudDaoJpa = new OnderhoudDaoJpa();
        try {
            data = onderhoudDaoJpa.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            onderhoudList = FXCollections.observableArrayList(); // Fallback
        }

        onderhoudList = FXCollections.observableArrayList(data.stream()
				.map(OnderhoudDTO::fromEntity)
				.collect(Collectors.toList()));
        filteredOnderhoudList = new FilteredList<>(onderhoudList, p -> true);
        sortedOnderhoudList = new SortedList<>(filteredOnderhoudList, sortOrder);
    }

    public ObservableList<OnderhoudDTO> getAllOnderhoud() {
        return sortedOnderhoudList;
    }

    public OnderhoudDTO getOnderhoudById(int id) {
        return OnderhoudDTO.fromEntity(onderhoudDaoJpa.get(id));
    }

    public void addOnderhoud(LocalDateTime datum, LocalDateTime startTijd, LocalDateTime eindTijd,
			int techniekerId, String reden, String rapport, String opmerkingen,
			OnderhoudStatus status, int machineId) {
		Onderhoud onderhoud = new Onderhoud(datum, startTijd, eindTijd, techniekerId, reden, rapport, opmerkingen, status, machineId);
		
        validateOnderhoudDetails(onderhoud);

        
        try {
        	onderhoudDaoJpa.startTransaction();
            onderhoudDaoJpa.insert(onderhoud);
            onderhoudDaoJpa.commitTransaction();
            onderhoudList.add(OnderhoudDTO.fromEntity(onderhoud));
	        data.add(onderhoud);
	    } catch (Exception e) {
	    	e.printStackTrace();
//	    	onderhoudDaoJpa.rollbackTransaction();
	    	throw new IllegalArgumentException(e.getMessage());
	    }
    }

    public void updateOnderhoud(OnderhoudDTO onderhoud) {
    	
    	int index = data.indexOf(onderhoud);
        
        try {
        	Onderhoud updatedOnderhoud = data.stream().filter(o -> o.getOnderhoudId() == onderhoud.id()).findFirst().orElse(null);
            onderhoudDaoJpa.startTransaction();
        	onderhoudDaoJpa.update(updatedOnderhoud);
        	onderhoudDaoJpa.commitTransaction();
        	
        	data.set(index, updatedOnderhoud);
            onderhoudList.set(onderhoudList.indexOf(updatedOnderhoud), onderhoud);
	    } catch (Exception e) {
	    	onderhoudDaoJpa.rollbackTransaction();
	    	throw new IllegalArgumentException(e.getMessage());
	    }
    }

    public void deleteOnderhoud(OnderhoudDTO onderhoud) {
    	        
        try {
        	Onderhoud onderhoudToDelete = data.stream().filter(o -> o.getOnderhoudId() == onderhoud.id()).findFirst().orElse(null);
            onderhoudDaoJpa.startTransaction();
        	onderhoudDaoJpa.delete(onderhoudToDelete);
        	onderhoudDaoJpa.commitTransaction();
            onderhoudList.remove(OnderhoudDTO.fromEntity(onderhoudToDelete));
        	
        	data.remove(onderhoud);
	    } catch (Exception e) {
	    	onderhoudDaoJpa.rollbackTransaction();
	    	throw new IllegalArgumentException(e.getMessage());
	    }
    }

    public void changeFilter(String filterValue) {
        filteredOnderhoudList.setPredicate(onderhoud -> {
            if (filterValue == null || filterValue.isBlank()) {
                return true;
            }
            String lowerCaseValue = filterValue.toLowerCase();
            return onderhoud.reden().toLowerCase().contains(lowerCaseValue) ||
                   onderhoud.rapport().toLowerCase().contains(lowerCaseValue);
        });
    }
    
    public ObservableList<OnderhoudDTO> filterByRole(GebruikerDTO ingelogdeGebruiker) {
    	boolean isVerantwoordelijke = ingelogdeGebruiker.rol() == Rol.VERANTWOORDELIJKE || ingelogdeGebruiker.rol() == Rol.ADMINISTRATOR;
    	int userId = ingelogdeGebruiker.id();
    	
        filteredOnderhoudList.setPredicate(onderhoud -> {
            if (isVerantwoordelijke) {
                // Verantwoordelijke ziet alle onderhouden
                return true;
            } else {
                // Technieker ziet alleen onderhouden van toegewezen machines
                return onderhoud.technieker().id() == userId;
            }
        });
		return filteredOnderhoudList;
    }
    
    public void validateOnderhoudDetails(Onderhoud onderhoud) {
        if (onderhoud.getDatum() == null || onderhoud.getStartTijd() == null || onderhoud.getEindTijd() == null) {
            throw new IllegalArgumentException("Datum, starttijd en eindtijd mogen niet leeg zijn.");
        }
        if (onderhoud.getReden() == null || onderhoud.getReden().isBlank()) {
            throw new IllegalArgumentException("Reden mag niet leeg zijn.");
        }
        if (onderhoud.getRapport() == null || onderhoud.getRapport().isBlank()) {
            throw new IllegalArgumentException("Rapport mag niet leeg zijn.");
        }
        if (onderhoud.getStatus() == OnderhoudStatus.INGEPLAND) {
            throw new IllegalArgumentException("De status 'ingepland' is niet toegestaan voor techniekers.");
        }
    }
    
    public void showSuccessMessage() {
        System.out.println("Onderhoud succesvol geregistreerd.");
    }


}
