package repository;

import java.util.List;

import domain.Machine;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;

public class MachineDaoJpa extends GenericDaoJpa<Machine> implements MachineDao {

    public MachineDaoJpa() {
        super(Machine.class);
    }
}
