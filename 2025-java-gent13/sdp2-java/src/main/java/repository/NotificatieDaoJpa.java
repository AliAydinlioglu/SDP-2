package repository;

import java.util.List;
import domain.Gebruiker;
import domain.Notificatie;
import enums.NotificatieStatus;
import jakarta.persistence.TypedQuery;

public class NotificatieDaoJpa extends GenericDaoJpa<Notificatie> implements NotificatieDao {

    public NotificatieDaoJpa() {
        super(Notificatie.class);
    }

    @Override
    public List<Notificatie> findByOntvanger(Gebruiker ontvanger) {
        return em
                .createQuery("SELECT n FROM Notificatie n WHERE n.ontvanger = :ontvanger ORDER BY n.timestamp DESC",
                        Notificatie.class)
                .setParameter("ontvanger", ontvanger)
                .getResultList();
    }

    @Override
    public long countUnreadByOntvanger(Gebruiker ontvanger) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(n) FROM Notificatie n WHERE n.ontvanger = :ontvanger AND (n.status = :nieuw OR n.status = :ongelezen)",
                Long.class);
        query.setParameter("ontvanger", ontvanger);
        query.setParameter("nieuw", NotificatieStatus.NIEUW);
        query.setParameter("ongelezen", NotificatieStatus.ONGELEZEN);
        return query.getSingleResult();
    }
}
