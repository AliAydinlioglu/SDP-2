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
	public Gebruiker get(int id) {
		try {
            return em.createNamedQuery("Gebruiker.findById", Gebruiker.class)
                 .setParameter("gebruikerId", id)
                 .getSingleResult();
        } catch (NoResultException ex) {
        	throw new EntityNotFoundException("Geen gebruiker gevonden met id: %d".formatted(id));
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
