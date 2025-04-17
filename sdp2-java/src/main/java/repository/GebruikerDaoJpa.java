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
    @Override
    public void insert(Gebruiker gebruiker) {
        try {
            // Check if a user with the same email already exists
            Gebruiker existingUser = getGebruikerByEmail(gebruiker.getEmail());
            if (existingUser != null) {
                throw new IllegalArgumentException("Email is already in use: " + gebruiker.getEmail());
            }
        } catch (EntityNotFoundException ex) {
            // No user with the same email exists, proceed with insertion
            super.insert(gebruiker);
        } catch (IllegalArgumentException e) {
            // Show an alert box for duplicate email
            utils.AlertHelper.showError("Duplicate Email", e.getMessage());
        }
    }
}
