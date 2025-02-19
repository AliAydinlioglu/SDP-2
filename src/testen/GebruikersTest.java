package testen;

import domein.Administrator;
import domein.Gebruiker;
import domein.Manager;
import domein.Technieker;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

class GebruikersTest {

    @Test
    void testGeldigeAdministratorAanmaken() {
        Gebruiker admin = new Administrator("Janssens", "Pieter", LocalDate.of(1985, 5, 20), "Stationsstraat 12", "pieter@example.com", "0471234567");
        assertEquals("Administrator", admin.getRol());
        assertTrue(admin.getStatus());
    }

    @Test
    void testGeldigeTechniekerAanmaken() {
        Gebruiker technieker = new Technieker("Peeters", "Jan", LocalDate.of(1990, 3, 10), "Markt 5", "jan@example.com", "0479876543");
        assertEquals("Technieker", technieker.getRol());
    }

    @Test
    void testTechniekerZonderGsmMoetFalen() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Technieker("Peeters", "Jan", LocalDate.of(1990, 3, 10), "Markt 5", "jan@example.com", "");
        });
        assertEquals("Gsm is verplicht voor Techniekers.", exception.getMessage());
    }

    @Test
    void testLegeVeldenMoetenFalen() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Manager("", "Jan", LocalDate.of(1990, 3, 10), "Markt 5", "jan@example.com", "0479876543");
        });
        assertEquals("Alle velden (behalve gsm) moeten ingevuld zijn.", exception.getMessage());
    }

    @Test
    void testStatusWijzigen() {
        Gebruiker manager = new Manager("Claes", "Sarah", LocalDate.of(1992, 8, 25), "Dorpstraat 3", "sarah@example.com", "0485123456");
        manager.setStatus(false);
        assertFalse(manager.getStatus(), "Gebruiker zou inactief moeten zijn.");
    }
}
