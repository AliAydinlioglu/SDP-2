package repository;

import java.util.List;

import domain.Machine;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;

public class MachineDaoJpa extends GenericDaoJpa<Machine> implements MachineDao {

    public MachineDaoJpa() {
        super(Machine.class);
    }

    @Override
    public Machine getMachineByID(String machineId) throws EntityNotFoundException {
        try {
            return em.createNamedQuery("Machine.findByID", Machine.class)
                    .setParameter("machineID", machineId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            throw new EntityNotFoundException("Geen machine gevonden met ID: %s".formatted(machineId));

        }
    }

    @Override
    public List<Machine> findAll() throws EntityNotFoundException {
        try {
            return em.createNamedQuery("Machine.findAll", Machine.class).getResultList();
        } catch (NoResultException e) {
            throw new EntityNotFoundException("Geen Machines gevonden");
        }
    }
}
