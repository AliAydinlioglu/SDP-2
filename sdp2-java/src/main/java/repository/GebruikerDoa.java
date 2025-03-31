package repository;
import java.util.List;

import domain.Gebruiker;
import jakarta.persistence.EntityNotFoundException;

public interface GebruikerDoa extends GenericDao<Gebruiker> {
	
	public Gebruiker get(int id) throws EntityNotFoundException;
	
	public List<Gebruiker> findAll() throws EntityNotFoundException;

}
