package domain;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import repository.OnderhoudDaoJpa;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import dto.GebruikerDTO;
import dto.MachineDTO;
import dto.OnderhoudDTO;
import enums.MachineStatus;
import enums.OnderhoudStatus;

public class OnderhoudController {

    private OnderhoudDaoJpa onderhoudDaoJpa;
    private ObservableList<Onderhoud> onderhoudList;
    private FilteredList<OnderhoudDTO> filteredOnderhoudList;
    private SortedList<OnderhoudDTO> sortedOnderhoudList;

    private final Comparator<OnderhoudDTO> byDate = Comparator.comparing(OnderhoudDTO::datum);
    private final Comparator<OnderhoudDTO> byStatus = Comparator.comparing(OnderhoudDTO::status);
    private final Comparator<OnderhoudDTO> sortOrder = byDate.thenComparing(byStatus);

    public OnderhoudController() {
        onderhoudDaoJpa = new OnderhoudDaoJpa();
        try {
            List<Onderhoud> data = onderhoudDaoJpa.findAll();
            onderhoudList = FXCollections.observableArrayList(data);
        } catch (Exception e) {
            e.printStackTrace();
            onderhoudList = FXCollections.observableArrayList(); // Fallback
        }

        ObservableList<OnderhoudDTO> onderhoudDTOList = FXCollections.observableArrayList(onderhoudList.stream()
				.map(OnderhoudDTO::fromEntity)
				.collect(Collectors.toList()));
        filteredOnderhoudList = new FilteredList<>(onderhoudDTOList, p -> true);
        sortedOnderhoudList = new SortedList<>(filteredOnderhoudList, sortOrder);
    }

    public ObservableList<OnderhoudDTO> getAllOnderhoud() {
        return sortedOnderhoudList;
    }

    public OnderhoudDTO getOnderhoudById(int id) {
        return OnderhoudDTO.fromEntity(onderhoudDaoJpa.get(id));
    }

    public void addOnderhoud(Onderhoud onderhoud) {
        onderhoudDaoJpa.insert(onderhoud);
        onderhoudList.add(onderhoud);
    }

    public void updateOnderhoud(OnderhoudDTO onderhoud) {
    	Onderhoud updatedOnderhoud = onderhoudList.stream().filter(o -> o.getOnderhoudId() == onderhoud.id()).findFirst().orElse(null);
        onderhoudDaoJpa.startTransaction();
    	onderhoudDaoJpa.update(updatedOnderhoud);
    	onderhoudDaoJpa.commitTransaction();
        onderhoudList.set(onderhoudList.indexOf(updatedOnderhoud), updatedOnderhoud);
    }

    public void deleteOnderhoud(OnderhoudDTO onderhoud) {
    	Onderhoud onderhoudToDelete = onderhoudList.stream().filter(o -> o.getOnderhoudId() == onderhoud.id()).findFirst().orElse(null);
        onderhoudDaoJpa.startTransaction();
    	onderhoudDaoJpa.delete(onderhoudToDelete);
    	onderhoudDaoJpa.commitTransaction();
        onderhoudList.remove(onderhoudToDelete);
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
    
    public void filterByRole(boolean isVerantwoordelijke, int userId) {
        filteredOnderhoudList.setPredicate(onderhoud -> {
            if (isVerantwoordelijke) {
                // Verantwoordelijke ziet alle onderhouden
                return true;
            } else {
                // Technieker ziet alleen onderhouden van toegewezen machines
                return onderhoud.technieker().id() == userId;
            }
        });
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

    
    public void registerOnderhoud(Onderhoud onderhoud) {
        validateOnderhoudDetails(onderhoud);
        addOnderhoud(onderhoud);

        // Als het onderhoud voltooid is, wijzig de status van de machine naar STARTBAAR
        if (onderhoud.getStatus() == OnderhoudStatus.VOLTOOID) {
        	//weet niet zeker of je controller van machine meot gebruiken of de JPA
        	MachineController machineController = new MachineController();
//            MachineDTO machine = machineController.getMachine(onderhoud.getMachineId());
//            machine.setStatus(MachineStatus.DRAAIT);
//            machineController.updateMachine(machine);
        }
    }
    
    public void showSuccessMessage() {
        System.out.println("Onderhoud succesvol geregistreerd.");
    }


}
