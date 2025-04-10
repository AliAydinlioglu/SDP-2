package domain;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import repository.OnderhoudDaoJpa;

import java.util.Comparator;
import java.util.List;

import enums.MachineStatus;
import enums.OnderhoudStatus;

public class OnderhoudController {

    private OnderhoudDaoJpa onderhoudDaoJpa;
    private ObservableList<Onderhoud> onderhoudList;
    private FilteredList<Onderhoud> filteredOnderhoudList;
    private SortedList<Onderhoud> sortedOnderhoudList;

    private final Comparator<Onderhoud> byDate = Comparator.comparing(Onderhoud::getDatum);
    private final Comparator<Onderhoud> byStatus = Comparator.comparing(Onderhoud::getStatus);
    private final Comparator<Onderhoud> sortOrder = byDate.thenComparing(byStatus);

    public OnderhoudController() {
        onderhoudDaoJpa = new OnderhoudDaoJpa();
        List<Onderhoud> data = onderhoudDaoJpa.findAll();

        onderhoudList = FXCollections.observableArrayList(data);
        filteredOnderhoudList = new FilteredList<>(onderhoudList, p -> true);
        sortedOnderhoudList = new SortedList<>(filteredOnderhoudList, sortOrder);
    }

    public ObservableList<Onderhoud> getAllOnderhoud() {
        return sortedOnderhoudList;
    }

    public Onderhoud getOnderhoudById(int id) {
        return onderhoudDaoJpa.get(id);
    }

    public void addOnderhoud(Onderhoud onderhoud) {
        onderhoudDaoJpa.insert(onderhoud);
        onderhoudList.add(onderhoud);
    }

    public void updateOnderhoud(Onderhoud onderhoud) {
        onderhoudDaoJpa.update(onderhoud);
        onderhoudList.set(onderhoudList.indexOf(onderhoud), onderhoud);
    }

    public void deleteOnderhoud(Onderhoud onderhoud) {
        onderhoudDaoJpa.delete(onderhoud);
        onderhoudList.remove(onderhoud);
    }

    public void changeFilter(String filterValue) {
        filteredOnderhoudList.setPredicate(onderhoud -> {
            if (filterValue == null || filterValue.isBlank()) {
                return true;
            }
            String lowerCaseValue = filterValue.toLowerCase();
            return onderhoud.getReden().toLowerCase().contains(lowerCaseValue) ||
                   onderhoud.getRapport().toLowerCase().contains(lowerCaseValue);
        });
    }
    
    public void filterByRole(boolean isVerantwoordelijke, int userId) {
        filteredOnderhoudList.setPredicate(onderhoud -> {
            if (isVerantwoordelijke) {
                // Verantwoordelijke ziet alle onderhouden
                return true;
            } else {
                // Technieker ziet alleen onderhouden van toegewezen machines
                return onderhoud.getTechnieker().getGebruikerID() == userId;
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
            Machine machine = new MachineController().getMachine(onderhoud.getMachineId());
            machine.setStatus(MachineStatus.DRAAIT);
            new MachineController().updateMachine(machine);
        }
    }
    
    public void showSuccessMessage() {
        System.out.println("Onderhoud succesvol geregistreerd.");
    }


}
