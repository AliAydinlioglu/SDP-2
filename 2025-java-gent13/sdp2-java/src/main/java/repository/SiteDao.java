package repository;

import java.util.List;

import domain.Site;

public interface SiteDao extends GenericDao<Site>{
    public long countMachinesForSite(int siteId);
    
    public List<Site> getSitesByVerantwoordelijkeId(int verantwoordelijkeId);
}
