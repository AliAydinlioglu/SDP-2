package domain;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import dto.MachineDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import repository.MachineDaoJpa;
import enums.MachineStatus;

public class MachineController {

    private MachineDaoJpa machineDaoJpa;

    private ObservableList<MachineDTO> machineList;
    private FilteredList<MachineDTO> filteredMachineList;

    private SortedList<MachineDTO> sortedMachineList;

    private final Comparator<MachineDTO> byName = (m1, m2) -> m1.naam().compareToIgnoreCase(m2.naam());
    private final Comparator<MachineDTO> byID = Comparator.comparingInt(MachineDTO::id);
    private final Comparator<MachineDTO> byStatus = Comparator.comparing(MachineDTO::status);

    private final Comparator<MachineDTO> sortOrder = byName.thenComparing(byID).thenComparing(byStatus);

    private static List<Machine> data;

    public MachineController() {
        //new PopulateDB().run();
        machineDaoJpa = new MachineDaoJpa();
        data = machineDaoJpa.findAll();

        machineList = FXCollections.observableArrayList(data.stream().map(MachineDTO::fromEntity)
        		.collect(Collectors.toList()));
        filteredMachineList = new FilteredList<>(machineList, p -> true);
        sortedMachineList = new SortedList<>(filteredMachineList, sortOrder);
    }

    public MachineDTO getMachine(int id) {
        Machine m = machineDaoJpa.get(id);
        return MachineDTO.fromEntity(m);
    }

    public ObservableList<MachineDTO> getAll() {
        return sortedMachineList;
    }


    public void changeFilter(String filterValue) {
        filteredMachineList.setPredicate(machine -> {
            if (filterValue == null || filterValue.isBlank()) {
                return true;
            }
            String lowerCaseValue = filterValue.toLowerCase();
            return machine.naam().toLowerCase().contains(lowerCaseValue);
        });
    }

    public void updateMachine(Machine machine) {
        machineDaoJpa.update(machine);
    }

    public void addMachine(Machine machine) {
        machineDaoJpa.insert(machine);
    }

    public void deactivateMachine(Machine machine) {
        machine.setStatus(MachineStatus.GESTOPT_AUTO);
        machineDaoJpa.update(machine);
    }

    public ObservableList<MachineDTO> getMachinesByStatus(MachineStatus status) {
        FilteredList<MachineDTO> filteredByStatus = new FilteredList<>(machineList, m -> m.status().equals(status));
        return new SortedList<>(filteredByStatus, sortOrder);
    }
    
    public ObservableList<MachineDTO> getMachinesForTechnieker(int techniekerId) {
        FilteredList<MachineDTO> filteredByTechnieker = new FilteredList<>(machineList, 
            machine -> machine.technieker() != null && machine.technieker().id() == techniekerId);
        return new SortedList<>(filteredByTechnieker, sortOrder);
    }
    
    public void validateMachineStatus(Machine machine) {
        if (machine.getStatus() != MachineStatus.GESTOPT_AUTO 
                && machine.getStatus() != MachineStatus.GESTOPT_MANUEEL 
                && machine.getStatus() != MachineStatus.IN_ONDERHOUD) {
            throw new IllegalArgumentException("De machine moet gestopt (automatisch of manueel) of in onderhoud zijn.");
        }
    }

    public void startOnderhoud(Machine machine) {
        validateMachineStatus(machine);
        machine.setStatus(MachineStatus.IN_ONDERHOUD);
        updateMachine(machine);
    }

}
