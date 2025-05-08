package domain;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import dto.MachineDTO;
import dto.SiteDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import repository.MachineDao;
import repository.MachineDaoJpa;
import enums.MachineStatus;
import utils.AlertHelper;

public class MachineController {

    private MachineDao machineDaoJpa;

    private List<Machine> data;
    private ObservableList<MachineDTO> machineList;
    private FilteredList<MachineDTO> filteredMachineList;

    private SortedList<MachineDTO> sortedMachineList;

    private final Comparator<MachineDTO> byName = (m1, m2) -> m1.naam().compareToIgnoreCase(m2.naam());
    private final Comparator<MachineDTO> byID = Comparator.comparingInt(MachineDTO::id);
    private final Comparator<MachineDTO> byStatus = Comparator.comparing(MachineDTO::status);

    private final Comparator<MachineDTO> sortOrder = byName.thenComparing(byID).thenComparing(byStatus);

    public MachineController() {
        machineDaoJpa = new MachineDaoJpa();
        initData();
    }

    public MachineController(MachineDao machineDaoJpa) {
        this.machineDaoJpa = machineDaoJpa;
    }

    private void initData() {
        try {
            data = machineDaoJpa.findAll();
        } catch (Exception e) {
            AlertHelper.showError("Connectie met databank mislukt", e.getMessage());
        }

        machineList = FXCollections.observableArrayList(data.stream()
                .map(MachineDTO::fromEntity)
                .collect(Collectors.toList()));
        filteredMachineList = new FilteredList<>(machineList, p -> true);
        sortedMachineList = new SortedList<>(filteredMachineList, sortOrder);
    }

    public MachineDTO getMachine(int id) {
        Machine m = machineDaoJpa.get(id);
        return MachineDTO.fromEntity(m);
    }

    protected Machine getRealMachine(int id) {
        return machineDaoJpa.get(id);
    }

    public ObservableList<MachineDTO> getAll() {
        if (data == null)
            initData();
        return sortedMachineList;
    }

    public void changeFilter(String filterValue) {
        filteredMachineList.setPredicate(machine -> {
            if (filterValue == null || filterValue.isBlank()) {
                return true;
            }
            String lowerCaseValue = filterValue.toLowerCase();
            return machine.naam().toLowerCase().contains(lowerCaseValue)
                    || (machine.locatie() != null && machine.locatie().toLowerCase().contains(lowerCaseValue))
                    || machine.status().toString().toLowerCase().contains(lowerCaseValue)
                    || Integer.toString(machine.uptime()).contains(lowerCaseValue);
        });
    }

    public void updateMachine(MachineDTO machineDTO) {
        Machine machine = data.stream()
                .filter(m -> m.getMachineID() == machineDTO.id())
                .findFirst()
                .orElse(null);
        if (machine == null)
            return;

        int index = data.indexOf(machine);

        machine.setNaam(machineDTO.naam());
        machine.setStatus(machineDTO.status());
        machine.setLocatie(machineDTO.locatie());
        machine.setUptime(machineDTO.uptime());

        try {
            machineDaoJpa.startTransaction();
            machineDaoJpa.update(machine);
            machineDaoJpa.commitTransaction();

            data.set(index, machine);
            MachineDTO updatedDTO = MachineDTO.fromEntity(machine);
            MachineDTO existingDTO = machineList.stream()
                    .filter(m -> m.id() == machineDTO.id())
                    .findFirst()
                    .orElse(null);
            machineList.set(machineList.indexOf(existingDTO), updatedDTO);

        } catch (Exception e) {
            machineDaoJpa.rollbackTransaction();
            throw new IllegalArgumentException("Machine kon niet worden aangepast: " + e.getMessage());
        }
    }

    public void addMachine(Machine machine) {
        try {
            machineDaoJpa.startTransaction();
            machineDaoJpa.insert(machine);
            machineDaoJpa.commitTransaction();
            machineList.add(MachineDTO.fromEntity(machine));
            if (data != null) {
                data.add(machine);
            }
        } catch (Exception e) {
            machineDaoJpa.rollbackTransaction();
            throw new IllegalArgumentException("Machine kon niet worden toegevoegd: " + e.getMessage());
        }
    }

    public void deactivateMachine(Machine machine) {
        try {
            machine.setStatus(MachineStatus.GESTOPT_AUTO);
            machineDaoJpa.startTransaction();
            machineDaoJpa.update(machine);
            machineDaoJpa.commitTransaction();
        } catch (Exception e) {
            machineDaoJpa.rollbackTransaction();
            throw new IllegalArgumentException("Machine kon niet worden gedeactiveerd: " + e.getMessage());
        }
    }

    public void deleteMachine(int machineId) {
        try {
            Machine machine = machineDaoJpa.get(machineId);
            if (machine == null) {
                throw new IllegalArgumentException("Machine met ID " + machineId + " niet gevonden");
            }

            machineDaoJpa.startTransaction();
            machineDaoJpa.delete(machine);
            machineDaoJpa.commitTransaction();

            if (data != null) {
                data.removeIf(m -> m.getMachineID() == machineId);
            }
            machineList.removeIf(m -> m.id() == machineId);
        } catch (Exception e) {
            machineDaoJpa.rollbackTransaction();
            throw new IllegalArgumentException("Machine kon niet worden verwijderd: " + e.getMessage());
        }
    }

    public ObservableList<MachineDTO> getMachinesByStatus(MachineStatus status) {
        if (data == null)
            initData();
        FilteredList<MachineDTO> filteredByStatus = new FilteredList<>(machineList, m -> m.status().equals(status));
        return new SortedList<>(filteredByStatus, sortOrder);
    }

    public ObservableList<MachineDTO> getMachinesForTechnieker(int techniekerId) {
        if (data == null)
            initData();
        FilteredList<MachineDTO> filteredByTechnieker = new FilteredList<>(machineList,
                machine -> machine.technieker() != null && machine.technieker().id() == techniekerId);
        return new SortedList<>(filteredByTechnieker, sortOrder);
    }

    public ObservableList<MachineDTO> getMachinesBySite(int siteId) {
        if (data == null)
            initData();
        FilteredList<MachineDTO> filteredBySite = new FilteredList<>(machineList,
                machine -> machine.site() != null && machine.site().id() == siteId);
        return new SortedList<>(filteredBySite, sortOrder);
    }

    public List<MachineDTO> getMachinesBySiteList(List<SiteDTO> siteList) {
        List<MachineDTO> allMachines = getAll();

        return allMachines.stream()
                .filter(machine -> siteList.stream()
                        .anyMatch(site -> site.id() == machine.site().id()))
                .collect(Collectors.toList());
    }

    public void validateMachineStatus(MachineDTO machine) {
        if (machine.status() != MachineStatus.GESTOPT_AUTO
                && machine.status() != MachineStatus.GESTOPT_MANUEEL
                && machine.status() != MachineStatus.IN_ONDERHOUD) {
            throw new IllegalArgumentException(
                    "De machine moet gestopt (automatisch of manueel) of in onderhoud zijn.");
        }
    }

    public void startOnderhoud(MachineDTO machineDTO) {
        validateMachineStatus(machineDTO);
        Machine machine = getRealMachine(machineDTO.id());
        machine.setStatus(MachineStatus.IN_ONDERHOUD);

        MachineDTO updatedMachine = MachineDTO.fromEntity(machine);
        updateMachine(updatedMachine);
    }

    public void stopOnderhoud(MachineDTO machineDTO) {
        validateMachineStatus(machineDTO);
        Machine machine = getRealMachine(machineDTO.id());
        machine.setStatus(MachineStatus.STARTBAAR);

        MachineDTO updatedMachine = MachineDTO.fromEntity(machine);
        updateMachine(updatedMachine);
    }
}
