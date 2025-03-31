package repository;

import java.util.List;

import domain.Gebruiker;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;

public class GebruikerDaoJpa extends GenericDaoJpa<Gebruiker> implements GebruikerDoa {

	public GebruikerDaoJpa() {
		super(Gebruiker.class);
	}
	
	@Override
	public Gebruiker getGebruikerByEmail(String email) {
		try {
            return em.createNamedQuery("Gebruiker.findByEmail", Gebruiker.class)
                 .setParameter("gebruikerEmail", email)
                 .getSingleResult();
        } catch (NoResultException ex) {
        	throw new EntityNotFoundException("Geen gebruiker gevonden met email: %s".formatted(email));
        }
	}

	@Override
	public List<Gebruiker> findAll() throws EntityNotFoundException {
		try {
			return em.createNamedQuery("Gebruiker.findAll", Gebruiker.class).getResultList();
		} catch (NoResultException e) {
        	throw new EntityNotFoundException("Geen gebruikers gevonden");
		}
	}
}
