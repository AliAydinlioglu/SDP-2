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

    @Override
    public List<Site> findAll() throws EntityNotFoundException {
        try {
            return em.createQuery("SELECT s FROM Site s LEFT JOIN FETCH s.verantwoordelijke", Site.class)
                    .getResultList();
        } catch (NoResultException e) {
            throw new EntityNotFoundException(("Geen sites gevonden"));
        }
    }

    public long countMachinesForSite(int siteId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(m) FROM Machine m WHERE m.site.siteId = :siteId", Long.class);
        query.setParameter("siteId", siteId);
        return query.getSingleResult();
    }
}