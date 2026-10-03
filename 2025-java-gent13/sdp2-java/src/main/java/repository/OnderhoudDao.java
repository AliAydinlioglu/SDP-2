package repository;

import java.util.List;

import domain.Onderhoud;

public interface OnderhoudDao extends GenericDao<Onderhoud> {
    
	public List<Onderhoud> findVoltooideLaatste3Maanden();
	
	public List<Onderhoud> findLaatsteVoltooidePerMachine();
}
