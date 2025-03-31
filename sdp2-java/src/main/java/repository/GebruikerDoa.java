package repository;
import java.util.List;

import domain.Gebruiker;
import jakarta.persistence.EntityNotFoundException;

public interface GebruikerDoa extends GenericDao<Gebruiker> {
	
	public Gebruiker getGebruikerByEmail(String email) throws EntityNotFoundException;
	
	public List<Gebruiker> findAll() throws EntityNotFoundException;

}
