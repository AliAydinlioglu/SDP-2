package repository;

import domain.Machine;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

public interface MachineDao extends GenericDao<Machine> {
    public Machine getMachineByID(String id) throws EntityNotFoundException;

    public List<Machine> findAll() throws EntityNotFoundException;
}
