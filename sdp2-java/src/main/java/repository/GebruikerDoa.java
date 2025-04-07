package repository;

import domain.Gebruiker;
import jakarta.persistence.EntityNotFoundException;

public interface GebruikerDoa extends GenericDao<Gebruiker> {
	
	public Gebruiker getGebruikerByEmail(String email) throws EntityNotFoundException;
	


}
