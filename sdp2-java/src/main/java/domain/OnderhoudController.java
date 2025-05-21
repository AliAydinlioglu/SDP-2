package domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import dto.GebruikerDTO;
import dto.MachineDTO;
import dto.OnderhoudDTO;
import dto.SiteDTO;
import enums.OnderhoudStatus;
import enums.Rol;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import repository.OnderhoudDao;
import repository.OnderhoudDaoJpa;

public class OnderhoudController {

    private List<Onderhoud> data;
    private OnderhoudDao onderhoudDao;
    private NotificatiesController notificatiesController;
    private GebruikerController gebruikerController;
    private MachineController machineController;

    private ObservableList<OnderhoudDTO> onderhoudList;
    private FilteredList<OnderhoudDTO> filteredOnderhoudList;
    private SortedList<OnderhoudDTO> sortedOnderhoudList;

    private final Comparator<OnderhoudDTO> byDate = Comparator.comparing(OnderhoudDTO::datum);
    private final Comparator<OnderhoudDTO> byStatus = Comparator.comparing(OnderhoudDTO::status);
    private final Comparator<OnderhoudDTO> sortOrder = byDate.thenComparing(byStatus);

    public OnderhoudController() {
        this(new OnderhoudDaoJpa(), new NotificatiesController(), new GebruikerController(), new MachineController());
    }

    public OnderhoudController(OnderhoudDao onderhoudDao, NotificatiesController notificatiesController,
                               GebruikerController gebruikerController, MachineController machineController) {
        this.onderhoudDao = onderhoudDao;
        this.notificatiesController = notificatiesController;
        this.gebruikerController = gebruikerController;
        this.machineController = machineController;
        initData();
    }

    private void initData() {
        try {
            data = onderhoudDao.findAll();
            onderhoudList = FXCollections.observableArrayList(
                    data.stream().map(OnderhoudDTO::fromEntity).collect(Collectors.toList()));
            filteredOnderhoudList = new FilteredList<>(onderhoudList, p -> true);
            sortedOnderhoudList = new SortedList<>(filteredOnderhoudList, sortOrder);
        } catch (Exception e) {
            System.err.println("Failed to initialize Onderhoud data: " + e.getMessage());
            data = new java.util.ArrayList<>();
            onderhoudList = FXCollections.observableArrayList();
            filteredOnderhoudList = new FilteredList<>(onderhoudList, p -> true);
            sortedOnderhoudList = new SortedList<>(filteredOnderhoudList, sortOrder);
        }
    }

    public ObservableList<OnderhoudDTO> getAllOnderhoud() {
        return sortedOnderhoudList;
    }

    public OnderhoudDTO getOnderhoudById(int id) {
        Onderhoud onderhoud = onderhoudDao.get(id);
        return onderhoud != null ? OnderhoudDTO.fromEntity(onderhoud) : null;
    }

    public Onderhoud getRealOnderhoudById(int id) {
        return onderhoudDao.get(id);
    }

    public void addOnderhoud(LocalDate datum, LocalTime startTijd, LocalTime eindTijd,
                             int techniekerId, String reden, String rapport, String opmerkingen,
                             OnderhoudStatus status, int machineId) {
        try {
            Gebruiker technieker = gebruikerController.getRealGebruiker(techniekerId);
            Machine machine = machineController.getRealMachine(machineId);

            if (technieker == null) {
                throw new IllegalArgumentException("Technieker niet gevonden met ID: " + techniekerId);
            }
            if (machine == null) {
                throw new IllegalArgumentException("Machine niet gevonden met ID: " + machineId);
            }

            Onderhoud nieuwOnderhoud = new Onderhoud.Builder()
                    .datum(datum)
                    .startTijd(startTijd)
                    .eindTijd(eindTijd)
                    .technieker(technieker)
                    .reden(reden)
                    .rapport(rapport)
                    .opmerkingen(opmerkingen)
                    .status(status)
                    .machineId(machine.getMachineID()) // Changed from .machine(machine)
                    .build();

            try {
            	onderhoudDao.startTransaction();
                onderhoudDao.insert(nieuwOnderhoud);
                onderhoudDao.commitTransaction();

                onderhoudList.add(OnderhoudDTO.fromEntity(nieuwOnderhoud));
			} catch (Exception e) {
				onderhoudDao.rollbackTransaction();
				throw new IllegalArgumentException("Kon onderhoud niet toevoegen: " + e.getMessage(), e);
			}

            Gebruiker siteVerantwoordelijke = machine.getSite().getVerantwoordelijke();
            if (siteVerantwoordelijke != null) {
                String titel = String.format("Nieuw onderhoud gepland voor machine %s", machine.getNaam());
                createOnderhoudNotification(nieuwOnderhoud, titel, siteVerantwoordelijke);
            }

            if (technieker != null && technieker
                    .getGebruikerID() != (siteVerantwoordelijke != null ? siteVerantwoordelijke.getGebruikerID()
                    : -1)) {
                String titelTechnieker = String.format("U bent toegewezen aan een nieuw onderhoud voor machine %s",
                        machine.getNaam());
                createOnderhoudNotification(nieuwOnderhoud, titelTechnieker, technieker);
            }

            onderhoudDao.startTransaction();
            onderhoudDao.insert(nieuwOnderhoud);
            onderhoudDao.commitTransaction();

            onderhoudList.add(OnderhoudDTO.fromEntity(nieuwOnderhoud));

            

        } catch (Exception e) {
            throw new RuntimeException("Fout bij het toevoegen van onderhoud: " + e.getMessage(), e);
        }
    }

    public void updateOnderhoud(OnderhoudDTO onderhouddto) {
        Onderhoud onderhoud = data.stream()
                .filter(o -> o.getOnderhoudId() == onderhouddto.id())
                .findFirst()
                .orElseGet(() -> onderhoudDao.get(onderhouddto.id()));

        if (onderhoud == null) {
            throw new IllegalArgumentException("Onderhoud met ID " + onderhouddto.id() + " niet gevonden voor update.");
        }

        OnderhoudStatus oldStatus = onderhoud.getStatus();
        Machine machine = onderhoud.getMachine();

        onderhoud.setDatum(onderhouddto.datum());
        onderhoud.setStartTijd(onderhouddto.startTijd());
        onderhoud.setEindTijd(onderhouddto.eindTijd());
        onderhoud.setReden(onderhouddto.reden());
        onderhoud.setRapport(onderhouddto.rapport());
        onderhoud.setOpmerkingen(onderhouddto.opmerkingen());
        onderhoud.setStatus(onderhouddto.status());

        try {
            onderhoudDao.startTransaction();
            onderhoudDao.update(onderhoud);
            onderhoudDao.commitTransaction();

            int dataIndex = -1;
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getOnderhoudId() == onderhoud.getOnderhoudId()) {
                    dataIndex = i;
                    break;
                }
            }
            if (dataIndex != -1) {
                data.set(dataIndex, onderhoud);
            }

            OnderhoudDTO updatedDTO = OnderhoudDTO.fromEntity(onderhoud);

            int listIndex = -1;
            for (int i = 0; i < onderhoudList.size(); i++) {
                if (onderhoudList.get(i).id() == updatedDTO.id()) {
                    listIndex = i;
                    break;
                }
            }
            if (listIndex != -1) {
                onderhoudList.set(listIndex, updatedDTO);
            } else {
                onderhoudList.add(updatedDTO);
            }

            OnderhoudStatus newStatus = onderhouddto.status();
            if (newStatus == OnderhoudStatus.VOLTOOID && oldStatus != OnderhoudStatus.VOLTOOID) {
                createOnderhoudNotification(onderhoud, "Onderhoud Voltooid",
                        machine.getSite() != null ? machine.getSite().getVerantwoordelijke() : null);
            } else if (newStatus == OnderhoudStatus.GEANNULEERD && oldStatus != OnderhoudStatus.GEANNULEERD) {
                createOnderhoudNotification(onderhoud, "Onderhoud Geannuleerd",
                        machine.getSite() != null ? machine.getSite().getVerantwoordelijke() : null);
            } else if ((newStatus == OnderhoudStatus.INGEPLAND || newStatus == OnderhoudStatus.IN_UITVOERING) &&
                    (oldStatus != OnderhoudStatus.INGEPLAND && oldStatus != OnderhoudStatus.IN_UITVOERING
                            && oldStatus != OnderhoudStatus.VOLTOOID && oldStatus != OnderhoudStatus.GEANNULEERD)) {
                createOnderhoudNotification(onderhoud, "Onderhoud Status Gewijzigd: " + newStatus.toString(),
                        machine.getSite() != null ? machine.getSite().getVerantwoordelijke() : null);
            }

        } catch (Exception e) {
            onderhoudDao.rollbackTransaction();
            System.err.println("Fout bij bijwerken onderhoud: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalArgumentException("Onderhoud kon niet worden aangepast: " + e.getMessage(), e);
        }
    }

    public void deleteOnderhoud(OnderhoudDTO onderhoudDto) {
        Onderhoud onderhoudToDelete = onderhoudDao.get(onderhoudDto.id());

        if (onderhoudToDelete == null) {
            boolean removedFromList = onderhoudList.removeIf(dto -> dto.id() == onderhoudDto.id());
            boolean removedFromData = data.removeIf(entity -> entity.getOnderhoudId() == onderhoudDto.id());
            if (removedFromList || removedFromData) {
                System.out.println(
                        "Onderhoud (ID: " + onderhoudDto.id() + ") was niet in DB, verwijderd uit lokale lijst.");
                return;
            }
            throw new IllegalArgumentException(
                    "Te verwijderen onderhoud met ID " + onderhoudDto.id() + " niet gevonden.");
        }

        Machine machine = onderhoudToDelete.getMachine();
        OnderhoudStatus oldStatus = onderhoudToDelete.getStatus();

        try {
            onderhoudDao.startTransaction();
            onderhoudDao.delete(onderhoudToDelete);
            onderhoudDao.commitTransaction();

            onderhoudList.removeIf(dto -> dto.id() == onderhoudDto.id());
            data.removeIf(entity -> entity.getOnderhoudId() == onderhoudDto.id());

            if (oldStatus != OnderhoudStatus.VOLTOOID && oldStatus != OnderhoudStatus.GEANNULEERD) {
                createOnderhoudNotification(onderhoudToDelete, "Onderhoud Verwijderd/Geannuleerd",
                        machine.getSite() != null ? machine.getSite().getVerantwoordelijke() : null);
            }

        } catch (Exception e) {
            onderhoudDao.rollbackTransaction();
            System.err.println("Fout bij verwijderen onderhoud: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalArgumentException("Onderhoud kon niet worden verwijderd: " + e.getMessage(), e);
        }
    }

    public ObservableList<OnderhoudDTO> filterOnderhoud(boolean laatste, boolean minderDanDrieMaanden,
                                                        OnderhoudStatus statusFilter, GebruikerDTO ingelogdeGebruiker, int siteId) {
        filteredOnderhoudList.setPredicate(onderhoud -> {
            boolean matchesStatus = true;
            boolean matchesDrieMaanden = true;
            boolean matchesUserAndSite = true;

            if (statusFilter != null) {
                matchesStatus = onderhoud.status() == statusFilter;
            }

            if (minderDanDrieMaanden) {
                matchesDrieMaanden = onderhoud.datum().isAfter(LocalDate.now().minusMonths(3));
            }

            if (siteId != -1) {
                if (ingelogdeGebruiker.rol() == Rol.VERANTWOORDELIJKE) {
                    matchesUserAndSite = onderhoud.machine().site().id() == siteId;
                } else if (ingelogdeGebruiker.rol() == Rol.TECHNIEKER) {
                    matchesUserAndSite = onderhoud.technieker().id() == ingelogdeGebruiker.id() &&
                            onderhoud.machine().site().id() == siteId;
                } else if (ingelogdeGebruiker.rol() == Rol.MANAGER || ingelogdeGebruiker.rol() == Rol.ADMINISTRATOR) {
                    matchesUserAndSite = onderhoud.machine().site().id() == siteId;
                }
            } else {
                if (ingelogdeGebruiker.rol() == Rol.TECHNIEKER) {
                    matchesUserAndSite = onderhoud.technieker().id() == ingelogdeGebruiker.id();
                }
            }

            return matchesStatus && matchesDrieMaanden && matchesUserAndSite;
        });

        if (laatste) {
            return FXCollections.observableArrayList(
                    filteredOnderhoudList.stream()
                            .collect(Collectors.groupingBy(o -> o.machine().id()))
                            .values().stream()
                            .map(list -> list.stream().max(Comparator.comparing(OnderhoudDTO::datum)).orElse(null))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList()));
        }

        return filteredOnderhoudList;
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
                        .collect(Collectors.toList()));
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
            filteredOnderhoudList.setPredicate(p -> true);
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
                        .collect(Collectors.toList()));
    }

    public ObservableList<OnderhoudDTO> getVoltooideOnderhoudLaatste3Maanden() {
        return FXCollections.observableArrayList(
                onderhoudDao.findVoltooideLaatste3Maanden().stream()
                        .map(OnderhoudDTO::fromEntity)
                        .collect(Collectors.toList()));
    }

    public ObservableList<OnderhoudDTO> getLaatsteVoltooideOnderhoudPerMachine() {
        return FXCollections.observableArrayList(
                onderhoudDao.findLaatsteVoltooidePerMachine().stream()
                        .map(OnderhoudDTO::fromEntity)
                        .collect(Collectors.toList()));
    }

    public OnderhoudDTO getLaatsteVoltooideOnderhoudVanMachine(int machineId) {
        return getLaatsteVoltooideOnderhoudPerMachine().stream()
                .filter(onderhoud -> onderhoud.machine().id() == machineId)
                .findFirst()
                .orElse(null);
    }

    public ObservableList<OnderhoudDTO> getFilteredOnderhoudByUserAndSite(GebruikerDTO ingelogdeGebruiker, int siteId) {
        filterByUser(ingelogdeGebruiker);
        var userPredicate = filteredOnderhoudList.getPredicate();

        filteredOnderhoudList.setPredicate(onderhoud -> userPredicate.test(onderhoud) &&
                onderhoud.machine().site().id() == siteId);

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

    private void createOnderhoudNotification(Onderhoud onderhoud, String titel, Gebruiker ontvanger) {
        if (ontvanger == null) {
            return;
        }
        String message = String.format(
                "Details: Datum: %s, Start: %s, Machine: %s",
                onderhoud.getDatum(),
                onderhoud.getStartTijd(),
                onderhoud.getMachine().getNaam());

        Notificatie notificatie = Notificatie.builder()
                .titel(titel)
                .message(message)
                .ontvanger(ontvanger)
                .itemType("Onderhoud")
                .itemId(onderhoud.getOnderhoudId())
                .build();
        notificatiesController.addNotificatie(notificatie);
    }

    public ObservableList<OnderhoudDTO> getOnderhoudByMachineId(int id) {
        return FXCollections.observableArrayList(
                onderhoudList.stream()
                        .filter(onderhoud -> onderhoud.machine().id() == id)
                        .collect(Collectors.toList()));

    }
}
