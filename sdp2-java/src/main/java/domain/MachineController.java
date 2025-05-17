package domain;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import dto.GebruikerDTO;
import dto.MachineDTO;
import dto.SiteDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import repository.MachineDao;
import repository.MachineDaoJpa;
import enums.MachineStatus;
import enums.Rol;
import utils.AlertHelper;
import domain.builders.NotificatieBuilder;

public class MachineController {

    private MachineDao machineDaoJpa;
    private SiteController siteController;
    private GebruikerController gebruikerController;
    private NotificatiesController notificatiesController;

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
        siteController = new SiteController();
        gebruikerController = new GebruikerController();
        notificatiesController = new NotificatiesController();
        initData();
    }

    public MachineController(MachineDao machineDaoJpa) {
        this.machineDaoJpa = machineDaoJpa;
        this.siteController = new SiteController();
        this.gebruikerController = new GebruikerController();
        this.notificatiesController = new NotificatiesController();
    }

    /**
     * Returns all sites for use in dropdowns
     * 
     * @return ObservableList of SiteDTO.SiteSummaryDTO objects
     */
    public ObservableList<SiteDTO.SiteSummaryDTO> getAllSites() {
        // Convert SiteDTO to SiteSummaryDTO
        ObservableList<SiteDTO> sites = siteController.getAllSites();
        return FXCollections.observableArrayList(
                sites.stream()
                        .map(site -> new SiteDTO.SiteSummaryDTO(site.id(), site.naam()))
                        .collect(Collectors.toList()));
    }

    /**
     * Returns all technicians (users with TECHNIEKER role) for use in dropdowns
     * 
     * @return ObservableList of GebruikerDTO objects
     */
    public ObservableList<GebruikerDTO> getAllTechnicians() {
        ObservableList<GebruikerDTO> allUsers = gebruikerController.findAll();
        return allUsers.filtered(user -> user.rol() == Rol.TECHNIEKER && user.actief());
    }

    /**
     * Adds a new machine from a DTO
     * 
     * @param machineDTO The DTO containing the machine data
     */
    public void addMachineFromDTO(MachineDTO machineDTO) {
        try {
            // Get the real objects from the controllers
            Gebruiker technieker = gebruikerController.getRealGebruiker(machineDTO.technieker().id());

            // For the site, we'll use a workaround since we don't have direct access to the
            // Site entity
            // We'll create a temporary Site object with the ID from the DTO
            Site site = new Site();
            site.setSiteId(machineDTO.site().id());
            site.setNaam(machineDTO.site().naam());

            // Create a new Machine object
            Machine machine = new Machine(
                    machineDTO.naam(),
                    machineDTO.productInfo(),
                    machineDTO.locatie(),
                    machineDTO.status(),
                    machineDTO.productieStatus(),
                    machineDTO.uptime(),
                    technieker,
                    machineDTO.dagenSindsOnderhoud(),
                    machineDTO.volgendOnderhoud(),
                    site);

            // Add the machine
            addMachine(machine);
        } catch (Exception e) {
            throw new IllegalArgumentException("Machine kon niet worden toegevoegd: " + e.getMessage());
        }
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

        MachineStatus oldStatus = machine.getStatus();

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
            if (existingDTO != null) {
                machineList.set(machineList.indexOf(existingDTO), updatedDTO);
            }

            if (isMachineStopped(machine.getStatus()) && !isMachineStopped(oldStatus)) {
                createMachineStatusNotification(machine, "Machine Gestopt");
            }

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
            MachineStatus oldStatus = machine.getStatus();
            machine.setStatus(MachineStatus.GESTOPT_AUTO);
            machineDaoJpa.startTransaction();
            machineDaoJpa.update(machine);
            machineDaoJpa.commitTransaction();

            MachineDTO dtoInList = machineList.stream()
                    .filter(mDto -> mDto.id() == machine.getMachineID())
                    .findFirst().orElse(null);
            if (dtoInList != null) {
                int dtoIndex = machineList.indexOf(dtoInList);
                machineList.set(dtoIndex, MachineDTO.fromEntity(machine));
            }
            int dataIndex = data.indexOf(machine);
            if (dataIndex != -1) {
                data.set(dataIndex, machine);
            } else {
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).getMachineID() == machine.getMachineID()) {
                        data.set(i, machine);
                        break;
                    }
                }
            }

            if (!isMachineStopped(oldStatus)) {
                createMachineStatusNotification(machine, "Machine Automatisch Gestopt");
            }
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

        updateMachine(MachineDTO.fromEntity(machine));
    }

    public void stopOnderhoud(MachineDTO machineDTO) {
        Machine machine = getRealMachine(machineDTO.id());
        if (machine.getStatus() != MachineStatus.IN_ONDERHOUD) {
            throw new IllegalArgumentException("Machine is niet in onderhoud.");
        }
        machine.setStatus(MachineStatus.STARTBAAR);

        updateMachine(MachineDTO.fromEntity(machine));
    }

    private boolean isMachineStopped(MachineStatus status) {
        return status == MachineStatus.GESTOPT_AUTO || status == MachineStatus.GESTOPT_MANUEEL;
    }

    private void createMachineStatusNotification(Machine machine, String titel) {
        String message = String.format("Machine '%s' (ID: %d) in site '%s' is nu %s.",
                machine.getNaam(),
                machine.getMachineID(),
                machine.getSite() != null ? machine.getSite().getNaam() : "Onbekend",
                machine.getStatus().toString().toLowerCase());

        List<Gebruiker> gebruikersToNotify = new java.util.ArrayList<>();

        Gebruiker assignedTechnieker = machine.getTechnieker();
        if (assignedTechnieker != null &&
                (assignedTechnieker.getRol() == Rol.TECHNIEKER ||
                        assignedTechnieker.getRol() == Rol.VERANTWOORDELIJKE ||
                        assignedTechnieker.getRol() == Rol.MANAGER)) {
            gebruikersToNotify.add(assignedTechnieker);
        }

        List<GebruikerDTO> allUsersDTO = gebruikerController.findAll();
        for (GebruikerDTO userDTO : allUsersDTO) {
            if (userDTO.rol() == Rol.VERANTWOORDELIJKE || userDTO.rol() == Rol.MANAGER) {
                Gebruiker user = gebruikerController.getRealGebruiker(userDTO.id());
                if (user != null
                        && gebruikersToNotify.stream().noneMatch(g -> g.getGebruikerID() == user.getGebruikerID())) {
                    gebruikersToNotify.add(user);
                }
            }
        }

        for (Gebruiker ontvanger : gebruikersToNotify) {
            Notificatie notificatie = new NotificatieBuilder()
                    .titel(titel)
                    .message(message)
                    .ontvanger(ontvanger)
                    .itemType("MACHINE")
                    .itemId(machine.getMachineID())
                    .build();
            notificatiesController.addNotificatie(notificatie);
        }
    }
}
