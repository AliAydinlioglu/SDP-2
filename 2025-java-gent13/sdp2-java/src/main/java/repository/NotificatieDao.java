package repository;

import java.util.List;
import domain.Gebruiker;
import domain.Notificatie;

public interface NotificatieDao extends GenericDao<Notificatie> {
    List<Notificatie> findByOntvanger(Gebruiker ontvanger);

    long countUnreadByOntvanger(Gebruiker ontvanger);
}
