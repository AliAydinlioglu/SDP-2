package domain;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import repository.SiteDao;
import repository.SiteDaoJpa;

import java.util.List;
import java.util.stream.Collectors;
import domain.builders.SiteBuilder;
import dto.GebruikerDTO;
import dto.SiteDTO;

public class SiteController {

    private SiteDao siteDao;
    private ObservableList<SiteDTO> siteList;
    private GebruikerController gebruikerController;
    private LogController logController;

    public SiteController(GebruikerController gebruikerController, LogController logController) {
        this.siteDao = new SiteDaoJpa();
        this.gebruikerController = gebruikerController;
        this.logController = logController;

        loadSitesFromDatabase();
    }

    public SiteController() {
        this(new GebruikerController(), new LogController());
        System.err.println("Waarschuwing: SiteController aangeroepen zonder GebruikerController/LogController. Nieuwe instances aangemaakt.");
    }
    private void loadSitesFromDatabase() {
        List<Site> sitesFromDb = siteDao.findAll();
        this.siteList = FXCollections.observableArrayList(sitesFromDb.stream()
                .map(SiteDTO::fromEntity)
                .collect(Collectors.toList()));
    }

    /**
     * Geeft de lijst van sites terug, klaar voor gebruik in een TableView.
     *
     * @return ObservableList van Site objecten.
     */
    public ObservableList<SiteDTO> getAllSites() {
        return siteList;
    }

    public SiteDTO getSiteDetails(SiteDTO site) {
        if (site == null) {
            throw new IllegalArgumentException("Kan details niet ophalen van een null site.");
        }
        return siteList.stream().filter(s -> s.id() == site.id())
        		.findFirst()
        		.orElseThrow(() -> new IllegalArgumentException("Site not found"));
    }

    public long getAantalMachinesVoorSite(SiteDTO site) {
        if (site == null) return 0;
        if (site.machines() != null) {
            return site.machines().size();
        }
        return 0;
    }
    
    
    public ObservableList<SiteDTO> getSitesByUserId(int gebruikerId){
        List<Site> sitesFromDbForUser = siteDao.getSitesByVerantwoordelijkeId(gebruikerId);
    	return FXCollections.observableArrayList(siteDao.getSitesByVerantwoordelijkeId(gebruikerId)
    			.stream()
				.map(SiteDTO::fromEntity)
				.collect(Collectors.toList()));
    }
    public void addSite(String naam, GebruikerDTO verantwoordelijkeDto, GebruikerDTO ingelogdeGebruikerDto) {
        if (naam == null || naam.trim().isEmpty()) {
            throw new IllegalArgumentException("Site naam mag niet leeg zijn.");
        }
        if (verantwoordelijkeDto == null) {
            throw new IllegalArgumentException("Verantwoordelijke voor de site is verplicht.");
        }

        Gebruiker verantwoordelijkeEntity = gebruikerController.getRealGebruiker(verantwoordelijkeDto.id());
        if (verantwoordelijkeEntity == null) {
            throw new IllegalArgumentException("Verantwoordelijke gebruiker met id " + verantwoordelijkeDto.id() + " niet gevonden.");
        }

        Site nieuweSiteEntity = new SiteBuilder()
                .naam(naam)
                .verantwoordelijke(verantwoordelijkeEntity)
                .build();

        try {
            siteDao.startTransaction();
            siteDao.insert(nieuweSiteEntity);
            siteDao.commitTransaction();

            SiteDTO nieuweSiteDto = SiteDTO.fromEntity(nieuweSiteEntity);
            this.siteList.add(nieuweSiteDto);

            if (logController != null && ingelogdeGebruikerDto != null) {
                logController.addLog(ingelogdeGebruikerDto, "Nieuwe site aangemaakt",
                        String.format("Site '%s' (ID: %d) met verantwoordelijke '%s'",
                                nieuweSiteDto.naam(), nieuweSiteDto.id(),
                                verantwoordelijkeDto.email()));
            }

        } catch (Exception e) {
            siteDao.rollbackTransaction();
            throw new RuntimeException("Kon nieuwe site niet opslaan: " + e.getMessage(), e);
        }
    }
}