package domain;

import java.util.List;
import java.util.stream.Collectors;

import enums.NotificatieStatus;
import repository.NotificatieDao;
import repository.NotificatieDaoJpa;

public class NotificatiesController {

    private NotificatieDao notificatieDao;

    public NotificatiesController() {
        this.notificatieDao = new NotificatieDaoJpa();
    }

    public NotificatiesController(NotificatieDao notificatieDao) {
        this.notificatieDao = notificatieDao;
    }

    public void addNotificatie(Notificatie notificatie) {
        try {
            notificatieDao.startTransaction();
            notificatieDao.insert(notificatie);
            notificatieDao.commitTransaction();
        } catch (Exception e) {
            notificatieDao.rollbackTransaction();
            throw new RuntimeException("Kon notificatie niet toevoegen: " + e.getMessage(), e);
        }
    }

    public List<Notificatie> getNotificatiesVoorGebruiker(Gebruiker gebruiker) {
        return notificatieDao.findByOntvanger(gebruiker);
    }

    public long getUnreadNotificatieCountForGebruiker(Gebruiker gebruiker) {
        return notificatieDao.countUnreadByOntvanger(gebruiker);
    }

    public void markeerAlsGelezen(Notificatie notificatie) {
        if (notificatie.getStatus() != NotificatieStatus.GELEZEN) {
            notificatie.setStatus(NotificatieStatus.GELEZEN);
            try {
                notificatieDao.startTransaction();
                notificatieDao.update(notificatie);
                notificatieDao.commitTransaction();
            } catch (Exception e) {
                notificatieDao.rollbackTransaction();
                throw new RuntimeException("Kon notificatie niet als gelezen markeren: " + e.getMessage(), e);
            }
        }
    }

    public void markeerAlsGelezen(List<Notificatie> notificaties) {
        try {
            notificatieDao.startTransaction();
            for (Notificatie notificatie : notificaties) {
                if (notificatie.getStatus() != NotificatieStatus.GELEZEN) {
                    notificatie.setStatus(NotificatieStatus.GELEZEN);
                    notificatieDao.update(notificatie);
                }
            }
            notificatieDao.commitTransaction();
        } catch (Exception e) {
            notificatieDao.rollbackTransaction();
            throw new RuntimeException("Kon notificaties niet als gelezen markeren: " + e.getMessage(), e);
        }
    }

    public void verwijderNotificatie(Notificatie notificatie) {
        try {
            notificatieDao.startTransaction();
            notificatieDao.delete(notificatie);
            notificatieDao.commitTransaction();
        } catch (Exception e) {
            notificatieDao.rollbackTransaction();
            throw new RuntimeException("Kon notificatie niet verwijderen: " + e.getMessage(), e);
        }
    }

    public void handleNieuwStatusVoorSessie(Gebruiker gebruiker) {
        List<Notificatie> nieuweNotificaties = getNotificatiesVoorGebruiker(gebruiker).stream()
                .filter(n -> n.getStatus() == NotificatieStatus.NIEUW)
                .collect(Collectors.toList());

        if (!nieuweNotificaties.isEmpty()) {
            try {
                notificatieDao.startTransaction();
                for (Notificatie notificatie : nieuweNotificaties) {
                    notificatie.setStatus(NotificatieStatus.ONGELEZEN);
                    notificatieDao.update(notificatie);
                }
                notificatieDao.commitTransaction();
            } catch (Exception e) {
                notificatieDao.rollbackTransaction();
                throw new RuntimeException("Kon status van nieuwe notificaties niet bijwerken: " + e.getMessage(), e);
            }
        }
    }
}
