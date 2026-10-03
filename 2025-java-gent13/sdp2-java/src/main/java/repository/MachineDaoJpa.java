package repository;

import domain.Machine;

public class MachineDaoJpa extends GenericDaoJpa<Machine> implements MachineDao {

    public MachineDaoJpa() {
        super(Machine.class);
    }
}
