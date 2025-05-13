package repository;

import java.time.LocalDate;
import java.util.List;

import domain.Onderhoud;
import enums.OnderhoudStatus;

public class OnderhoudDaoJpa extends GenericDaoJpa<Onderhoud> implements OnderhoudDao {

    public OnderhoudDaoJpa() {
        super(Onderhoud.class);
    }
    
    @Override
    public List<Onderhoud> findVoltooideLaatste3Maanden() {
        LocalDate ninetyDaysAgo = LocalDate.now().minusDays(90); // Use LocalDate instead of LocalDateTime
        return em.createNamedQuery("Onderhoud.findVoltooideLaatste3Maanden", Onderhoud.class)
                 .setParameter("status", OnderhoudStatus.VOLTOOID)
                 .setParameter("date", ninetyDaysAgo) // Pass LocalDate
                 .getResultList();
    }

    
    public List<Onderhoud> findLaatsteVoltooidePerMachine() {
        return em.createNamedQuery("Onderhoud.findLaatsteVoltooidePerMachine", Onderhoud.class)
                            .getResultList();
    }

}
