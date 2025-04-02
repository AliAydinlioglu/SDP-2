package repository;

import domain.Site;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

public interface SiteDao extends GenericDao<Site>{
    public List<Site> findAll() throws EntityNotFoundException;
    public long countMachinesForSite(int siteId);
}
