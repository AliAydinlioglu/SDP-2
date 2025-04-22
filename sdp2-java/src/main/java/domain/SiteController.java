package domain;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import repository.SiteDao;
import repository.SiteDaoJpa; // Import de implementatie

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import dto.SiteDTO;

public class SiteController {

    private SiteDao siteDao;
    private ObservableList<SiteDTO> siteList;

    public SiteController() {
        this.siteDao = new SiteDaoJpa();

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
        return siteList.stream().filter(s -> s.id() == site.id())
        		.findFirst()
        		.orElseThrow(() -> new IllegalArgumentException("Site not found"));
    }

    public long getAantalMachinesVoorSite(SiteDTO site) {
        if (siteDao instanceof SiteDaoJpa) {
            return ((SiteDaoJpa) siteDao).countMachinesForSite(site.id());
        }
        return site.machines().size();
    }
    
    
    public ObservableList<SiteDTO> getSitesByUserId(int id){
    	return FXCollections.observableArrayList(siteDao.getSitesByVerantwoordelijkeId(id)
    			.stream()
				.map(SiteDTO::fromEntity)
				.collect(Collectors.toList()));
    }
}