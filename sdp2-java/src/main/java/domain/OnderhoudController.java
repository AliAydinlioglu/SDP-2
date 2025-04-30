package domain;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import repository.OnderhoudDao;
import repository.OnderhoudDaoJpa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import domain.builders.OnderhoudBuilder;
import dto.GebruikerDTO;
import dto.MachineDTO;
import dto.OnderhoudDTO;
import dto.SiteDTO;
import enums.MachineStatus;
import enums.OnderhoudStatus;
import enums.Rol;

public class OnderhoudController {
	
	private List<Onderhoud> data;
    private OnderhoudDao onderhoudDao;
    private ObservableList<OnderhoudDTO> onderhoudList;
    private FilteredList<OnderhoudDTO> filteredOnderhoudList;
    private SortedList<OnderhoudDTO> sortedOnderhoudList;

    private final Comparator<OnderhoudDTO> byDate = Comparator.comparing(OnderhoudDTO::datum);
    private final Comparator<OnderhoudDTO> byStatus = Comparator.comparing(OnderhoudDTO::status);
    private final Comparator<OnderhoudDTO> sortOrder = byDate.thenComparing(byStatus);

    public OnderhoudController() {
    	onderhoudDao = new OnderhoudDaoJpa();
        try {
            data = onderhoudDao.findAll();
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
        return OnderhoudDTO.fromEntity(onderhoudDao.get(id));
    }
    
    public Onderhoud getRealOnderhoudById(int id) {
		return onderhoudDao.get(id);
	}

    public void addOnderhoud(LocalDate datum, LocalTime startTijd, LocalTime eindTijd,
            int techniekerId, String reden, String rapport, String opmerkingen,
            OnderhoudStatus status, int machineId) {
        try {
        	
            Gebruiker technieker = new GebruikerController().getRealGebruiker(techniekerId); // Assuming this method exists
            Onderhoud onderhoud = new OnderhoudBuilder()
                    .datum(datum)
                    .startTijd(startTijd)
                    .eindTijd(eindTijd)
                    .technieker(technieker)
                    .reden(reden)
                    .rapport(rapport)
                    .opmerkingen(opmerkingen)
                    .status(status)
                    .machineId(machineId)
                    .build();

            onderhoudDao.startTransaction();
            onderhoudDao.insert(onderhoud);
            onderhoudDao.commitTransaction();
            
            onderhoudList.add(OnderhoudDTO.fromEntity(onderhoud));
            data.add(onderhoud);
        } catch (Exception e) {
//            onderhoudDao.rollbackTransaction();
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    public void updateOnderhoud(OnderhoudDTO onderhouddto) {
        
        try {
        	Onderhoud onderhoud = getRealOnderhoudById(onderhouddto.id());
        	
        	int indexData = data.indexOf(onderhoud);
        	int indexList = onderhoudList.indexOf(
        		    onderhoudList.stream()
        		        .filter(o -> o.id() == onderhouddto.id())
        		        .findFirst()
        		        .orElse(null)
        		);
        	
        	onderhoud.setDatum(onderhouddto.datum());
        	onderhoud.setStartTijd(onderhouddto.startTijd());
        	onderhoud.setEindTijd(onderhouddto.eindTijd());
        	onderhoud.setReden(onderhouddto.reden());
        	onderhoud.setRapport(onderhouddto.rapport());
        	onderhoud.setOpmerkingen(onderhouddto.opmerkingen());
        	onderhoud.setStatus(onderhouddto.status());
        	
        	onderhoudDao.startTransaction();
        	onderhoudDao.update(onderhoud);
        	onderhoudDao.commitTransaction();
        	
        	data.set(indexData, onderhoud);
            onderhoudList.set(indexList, onderhouddto);
	    } catch (Exception e) {
//	    	onderhoudDao.rollbackTransaction();
	    	e.printStackTrace();
	    }
    }

    public void deleteOnderhoud(OnderhoudDTO onderhoud) {
    	        
        try {
        	Onderhoud onderhoudToDelete = data.stream().filter(o -> o.getOnderhoudId() == onderhoud.id()).findFirst().orElse(null);
        	onderhoudDao.startTransaction();
        	onderhoudDao.delete(onderhoudToDelete);
        	onderhoudDao.commitTransaction();
        	
            onderhoudList.remove(OnderhoudDTO.fromEntity(onderhoudToDelete));
        	data.remove(onderhoud);
	    } catch (Exception e) {
//	    	onderhoudDao.rollbackTransaction();
	    	throw new IllegalArgumentException(e.getMessage());
	    }
    }

    public ObservableList<OnderhoudDTO> changeFilter(String filterValue) {
        return FXCollections.observableArrayList(
            onderhoudList.stream()
                .filter(onderhoud -> {
                    if (filterValue == null || filterValue.isBlank()) {
                        return true;
                    }
                    String lowerCaseValue = filterValue.toLowerCase();
                    return onderhoud.reden().toLowerCase().contains(lowerCaseValue) ||
                           onderhoud.rapport().toLowerCase().contains(lowerCaseValue);
                })
                .collect(Collectors.toList())
        );
    }

    
    public ObservableList<OnderhoudDTO> filterByUser(GebruikerDTO ingelogdeGebruiker) {
        int userId = ingelogdeGebruiker.id();

        if (ingelogdeGebruiker.rol() == Rol.VERANTWOORDELIJKE) {
            SiteController siteController = new SiteController();
            MachineController machineController = new MachineController();

            List<Integer> machineIds = siteController.getSitesByUserId(userId).stream()
                    .flatMap(site -> machineController.getMachinesBySite(site.id()).stream())
                    .map(MachineDTO::id)
                    .collect(Collectors.toList());

            filteredOnderhoudList.setPredicate(onderhoud -> machineIds.contains(onderhoud.machine().id()));
        } else if (ingelogdeGebruiker.rol() == Rol.ADMINISTRATOR) {
            filteredOnderhoudList.setPredicate(p -> true); // No filtering for administrators
        } else {
            filteredOnderhoudList.setPredicate(onderhoud -> onderhoud.technieker().id() == userId);
        }

        return filteredOnderhoudList;
    }


    
    public ObservableList<OnderhoudDTO> filterBySite(SiteDTO site) {
        int siteId = site.id();

        MachineController machineController = new MachineController();
        List<Integer> machineIds = machineController.getMachinesBySite(siteId).stream()
                .map(MachineDTO::id)
                .collect(Collectors.toList());

        return FXCollections.observableArrayList(
                onderhoudList.stream()
                        .filter(onderhoud -> machineIds.contains(onderhoud.machine().id()))
                        .collect(Collectors.toList())
        );
    }

    public ObservableList<OnderhoudDTO> getVoltooideOnderhoudLaatste3Maanden() {
        return FXCollections.observableArrayList(
                onderhoudDao.findVoltooideLaatste3Maanden().stream()
                        .map(OnderhoudDTO::fromEntity)
                        .collect(Collectors.toList())
        );
    }

    public ObservableList<OnderhoudDTO> getLaatsteVoltooideOnderhoudPerMachine() {
        return FXCollections.observableArrayList(
                onderhoudDao.findLaatsteVoltooidePerMachine().stream()
                        .map(OnderhoudDTO::fromEntity)
                        .collect(Collectors.toList())
        );
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


}
