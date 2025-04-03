package repository;

import domain.Site;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class SiteDaoJpa extends GenericDaoJpa<Site> implements SiteDao {

    public SiteDaoJpa() {
        super(Site.class);
    }


    public long countMachinesForSite(int siteId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(m) FROM Machine m WHERE m.site.siteId = :siteId", Long.class);
        query.setParameter("siteId", siteId);
        return query.getSingleResult();
    }
}