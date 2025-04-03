package domain;

import java.util.Comparator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import repository.MachineDaoJpa;
import enums.MachineStatus;

public class MachineController {

    private MachineDaoJpa machineDaoJpa;

    private ObservableList<Machine> machineList;
    private FilteredList<Machine> filteredMachineList;

    private SortedList<Machine> sortedMachineList;

    private final Comparator<Machine> byName = (m1, m2) -> m1.getName().compareToIgnoreCase(m2.getName());
    private final Comparator<Machine> byID = Comparator.comparingInt(Machine::getMachineID);
    private final Comparator<Machine> byStatus = Comparator.comparing(Machine::getStatus);

    private final Comparator<Machine> sortOrder = byName.thenComparing(byID).thenComparing(byStatus);

    private static List<Machine> data;

    public MachineController() {
        //new PopulateDB().run();
        machineDaoJpa = new MachineDaoJpa();
        data = machineDaoJpa.findAll();

        machineList = FXCollections.observableArrayList(data);
        filteredMachineList = new FilteredList<>(machineList, p -> true);
        sortedMachineList = new SortedList<>(filteredMachineList, sortOrder);
    }

    public Machine getMachine(int id) {
        return machineDaoJpa.get(id);
    }

    public ObservableList<Machine> getAll() {
        return sortedMachineList;
    }


    public void changeFilter(String filterValue) {
        filteredMachineList.setPredicate(machine -> {
            if (filterValue == null || filterValue.isBlank()) {
                return true;
            }
            String lowerCaseValue = filterValue.toLowerCase();
            return machine.getName().toLowerCase().contains(lowerCaseValue);
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

    public ObservableList<Machine> getMachinesByStatus(MachineStatus status) {
        FilteredList<Machine> filteredByStatus = new FilteredList<>(machineList, m -> m.getStatus().equals(status));
        return new SortedList<>(filteredByStatus, sortOrder);
    }
}
