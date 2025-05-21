package domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import dto.GebruikerDTO;
import enums.Rol;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import repository.GebruikerDaoJpa;
import repository.GebruikerDao;
import utils.AlertHelper;

public class GebruikerController {

    private GebruikerDao gebruikerRepo;

    private List<Gebruiker> data;
    private ObservableList<GebruikerDTO> gebruikerList;
    private FilteredList<GebruikerDTO> filteredGebruikerList;

    private SortedList<GebruikerDTO> sortedGebruikerList;

    private final Comparator<GebruikerDTO> byFirstName = (p1, p2) -> p1.voornaam().compareToIgnoreCase(p2.voornaam());

    private final Comparator<GebruikerDTO> byLastName = (p1, p2) -> p1.achternaam()
            .compareToIgnoreCase(p2.achternaam());

    private final Comparator<GebruikerDTO> byEmail = (p1, p2) -> p1.email().compareToIgnoreCase(p2.email());

    private final Comparator<GebruikerDTO> sortOrder = byFirstName.thenComparing(byLastName).thenComparing(byEmail);

    public GebruikerController() {
        try { // Keep try-catch from origin/main
            this.gebruikerRepo = new GebruikerDaoJpa();
            initData();
        } catch (Exception e) {
            AlertHelper.showError("Fout bij initialisatie GebruikerController", e.getMessage());
            // Optionally rethrow or handle more gracefully
            throw new RuntimeException("Initialisatie GebruikerController mislukt", e);
        }
    }

    public GebruikerController(GebruikerDao gebruikerRepo) { // voor mockito
         
        this.gebruikerRepo = gebruikerRepo;
    }

    private void initData() {
        try {
            data = gebruikerRepo.findAll();
        } catch (Exception e) {
            AlertHelper.showError("Connectie met databank mislukt", e.getMessage());
            data = new ArrayList<>(); // Initialize to empty list on error
        }

        gebruikerList = FXCollections.observableArrayList(data.stream()
                .map(GebruikerDTO::fromEntity)
                .collect(Collectors.toList()));
        filteredGebruikerList = new FilteredList<>(gebruikerList, p -> true);
        sortedGebruikerList = new SortedList<>(filteredGebruikerList, sortOrder);
    }

    public GebruikerDTO getGebruiker(int id) {
        Gebruiker g = gebruikerRepo.get(id);
        return GebruikerDTO.fromEntity(g);
    }

    // Made public for MainFrameController to access
    public Gebruiker getRealGebruiker(int id) {
		if (gebruikerRepo.get(id) == null) {
			throw new IllegalArgumentException("Geen gebruiker gevonden met id: " + id);
		}
		// System.out.println(gebruikerRepo.get(id));
    	return gebruikerRepo.get(id);
    }

    public ObservableList<GebruikerDTO> findAll() {
        if (data == null)
            initData();
        return sortedGebruikerList;
    }

    public GebruikerDTO getGebruikerByEmailDTO(String email) {
        Gebruiker g = gebruikerRepo.getGebruikerByEmail(email);
        return GebruikerDTO.fromEntity(g);

    }

    private Gebruiker getGebruikerByEmail(String email) {
        return gebruikerRepo.getGebruikerByEmail(email);

    }

    public void addGebruiker(String naam, String voornaam, LocalDate geboortedatum, String straat, String huisNr,
            String postcode, String stad, String land, String email, String gsm, Rol rol, boolean actief) {

        try {
            Adres adres = new Adres.Builder()
                    .straat(straat)
                    .huis_nr(huisNr)
                    .postcode(postcode)
                    .stad(stad)
                    .land(land)
                    .build();

            Gebruiker nieuweGebruiker = new Gebruiker.Builder()
                    .achternaam(naam)
                    .voornaam(voornaam)
                    .geboorteDatum(geboortedatum)
                    .adres(adres)
                    .email(email)
                    .gsm(gsm)
                    .rol(rol)
                    .actief(actief)
                    .build();
            // Assuming wachtwoord needs to be set, e.g. a default or generated one
            // nieuweGebruiker.setWachtwoord("DefaultPassword123!"); // Example

            try {
            	gebruikerRepo.startTransaction();
                gebruikerRepo.insert(nieuweGebruiker);
                gebruikerRepo.commitTransaction();

                data.add(nieuweGebruiker);
                gebruikerList.add(GebruikerDTO.fromEntity(nieuweGebruiker));
			} catch (Exception e) {
				gebruikerRepo.rollbackTransaction();
				throw new IllegalArgumentException("Gebruiker kon niet worden toegevoegd: " + e.getMessage());
			}

        } catch (Exception e) {
            throw new IllegalArgumentException("Gebruiker kon niet worden toegevoegd: " + e.getMessage());
            
        }
    }

    public void changeFilter(String filterValue, Rol rol, Boolean nonActiefChecked) {
        filteredGebruikerList.setPredicate(person -> {
            boolean keywordMatch = filterValue == null || filterValue.isEmpty() ||
                    person.voornaam().toLowerCase().contains(filterValue.toLowerCase()) ||
                    person.achternaam().toLowerCase().contains(filterValue.toLowerCase()) ||
                    person.email().toLowerCase().contains(filterValue.toLowerCase());

            boolean rolMatch = rol == null || person.rol() == rol;

            boolean actiefMatch = nonActiefChecked ? !person.actief() : person.actief();

            return keywordMatch && rolMatch && actiefMatch;
        });
    }


    public void removeGebruiker(GebruikerDTO gebruiker) {
        for (int i = 0; i < gebruikerList.size(); i++) {
            if (data.get(i).getGebruikerID() == gebruiker.id()) {
                try {
                    data.get(i).setActief(false);
                    gebruikerRepo.startTransaction();
                    gebruikerRepo.update(data.get(i));
                    gebruikerRepo.commitTransaction();
                    gebruikerList.remove(gebruikerList.get(i));
                    data.remove(i);
                    return;
                } catch (Exception e) {
                    gebruikerRepo.rollbackTransaction();
                    throw new IllegalArgumentException("Gebruiker kon niet worden verwijdert: " + e.getMessage());
                }
            }
        }
    }

    public GebruikerDTO login(String email, String wachtwoord) {
        Gebruiker g = getGebruikerByEmail(email);

        if (g == null || !g.checkWachtwoord(email, wachtwoord)) {
            throw new IllegalArgumentException("Ongeldige email of wachtwoord");
        }
        return GebruikerDTO.fromEntity(g);

    }

    public void updateGebruiker(GebruikerDTO bewerkteDTO) {
        Gebruiker g = data.stream()
                .filter(e -> e.getGebruikerID() == bewerkteDTO.id())
                .findFirst()
                .orElse(null);
        if (g == null)
            return;

        int index = data.indexOf(g);

        g.setVoornaam(bewerkteDTO.voornaam());
        g.setAchternaam(bewerkteDTO.achternaam());
        g.setGeboorteDatum(bewerkteDTO.geboortedatum());
        g.setAdres(new Adres.Builder()
                .straat(bewerkteDTO.adres().straat())
                .huis_nr(bewerkteDTO.adres().huis_nr())
                .postcode(bewerkteDTO.adres().postcode())
                .stad(bewerkteDTO.adres().stad())
                .land(bewerkteDTO.adres().land())
                .build());
        g.setEmail(bewerkteDTO.email());
        g.setGsm(bewerkteDTO.gsm());
        g.setRol(bewerkteDTO.rol());
        g.setActief(bewerkteDTO.actief());

        try {
            gebruikerRepo.startTransaction();
            gebruikerRepo.update(g);
            gebruikerRepo.commitTransaction();

            data.set(index, g);
            GebruikerDTO gg = gebruikerList.stream().filter(e -> e.id() == bewerkteDTO.id()).findFirst().orElse(null);
            gebruikerList.set(gebruikerList.indexOf(gg), bewerkteDTO);

        } catch (Exception e2) {
            gebruikerRepo.rollbackTransaction();
            throw new IllegalArgumentException("Gebruiker kon niet aangepas worden: " + e2.getMessage());
        }

    }

    public List<Gebruiker> getGebruikersByRol(Rol rol) {
        if (data == null) {
            initData();
        }
        return data.stream()
                .filter(g -> g.getRol() == rol && g.isActief())
                .collect(Collectors.toList());
    }

    public List<Gebruiker> getGebruikersByRoleAndSite(Rol rol, int siteId) {
        if (data == null) {
            initData();
        }
        return data.stream()
                .filter(g -> g.getRol() == rol && g.isActief())
                .filter(g -> {
                    if (rol == Rol.VERANTWOORDELIJKE) {
                        return g.getSitesSet().stream().anyMatch(s -> s.getSiteId() == siteId);
                    }
                    return true; // For other roles like MANAGER, no site-specific check here unless defined
                })
                .collect(Collectors.toList());
    }

}
