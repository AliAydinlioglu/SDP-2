package repository;

import domain.Onderhoud;

public class OnderhoudDaoJpa extends GenericDaoJpa<Onderhoud> implements OnderhoudDao {

    public OnderhoudDaoJpa() {
        super(Onderhoud.class);
    }

}
