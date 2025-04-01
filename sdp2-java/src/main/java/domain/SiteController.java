package domain;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import repository.SiteDao;
import repository.SiteDaoJpa; // Import de implementatie

import java.util.Comparator;
import java.util.List;

public class SiteController {

    private SiteDao siteDao;
    private ObservableList<Site> siteList;

    public SiteController() {
        this.siteDao = new SiteDaoJpa();

        List<Site> sitesFromDb = siteDao.findAll();

        this.siteList = FXCollections.observableArrayList(sitesFromDb);
    }

    /**
     * Geeft de lijst van sites terug, klaar voor gebruik in een TableView.
     * @return ObservableList van Site objecten.
     */
    public ObservableList<Site> getAllSites() {
        return siteList;
    }

    public Site getSiteDetails(Site site) {
        return site;
    }

    public long getAantalMachinesVoorSite(Site site) {
        if (siteDao instanceof SiteDaoJpa) {
            return ((SiteDaoJpa) siteDao).countMachinesForSite(site.getSiteId());
        }
        return site.getMachines().size();
    }
}