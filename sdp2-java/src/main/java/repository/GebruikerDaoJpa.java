package repository;

import domain.Gebruiker;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;

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
}
