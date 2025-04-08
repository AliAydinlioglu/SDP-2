package repository;

import java.util.List;

import domain.Onderhoud;

public class OnderhoudDaoJpa extends GenericDaoJpa<Onderhoud> implements OnderhoudDao {

    public OnderhoudDaoJpa() {
        super(Onderhoud.class);
    }
    
    public List<Onderhoud> findLaatsteVoltooidePerMachine() {
        return em.createNamedQuery("Onderhoud.findLaatsteVoltooidePerMachine", Onderhoud.class)
                            .getResultList();
    }

}
