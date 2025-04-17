package repository;

import domain.Site;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

public interface SiteDao extends GenericDao<Site>{
    public long countMachinesForSite(int siteId);
    
    public List<Site> getSitesByVerantwoordelijkeId(int verantwoordelijkeId);
}
