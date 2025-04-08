package domain;


import enums.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class GebruikerTest {

    private Gebruiker gebruiker;

    @BeforeEach
    void setUp() {
        gebruiker = new Gebruiker(
                "Jansen",
                "Jan",
                LocalDate.of(1990, 5, 12),
                new Adres("Straat", "1", "1000", "Brussel", "Belgie"),
                "jan.jansen@example.com",
                "0499123456",
                Rol.ADMINISTRATOR
        );
    }

    @Test
    void constructorShouldThrowWhenMissingRequiredFields() {
        assertThrows(IllegalArgumentException.class, () ->
                new Gebruiker("", "Jan", LocalDate.now(), new Adres(), "test@mail.com", "", Rol.ADMINISTRATOR)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Gebruiker("Achternaam", "", LocalDate.now(), new Adres(), "test@mail.com", "", Rol.ADMINISTRATOR)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Gebruiker("Achternaam", "Jan", null, new Adres(), "test@mail.com", "", Rol.ADMINISTRATOR)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Gebruiker("Achternaam", "Jan", LocalDate.now(), new Adres(), "", "", Rol.ADMINISTRATOR)
        );
    }

    @Test
    void techniekerMustHaveGsm() {
        assertThrows(IllegalArgumentException.class, () ->
                new Gebruiker("Achternaam", "Jan", LocalDate.now(), new Adres(), "jan@test.com", "", Rol.TECHNIEKER)
        );
    }

    @Test
    void setEmailShouldRejectBlank() {
        assertThrows(IllegalArgumentException.class, () -> gebruiker.setEmail("  "));
    }

    @Test
    void setGsmShouldRejectNullForTechnieker() {
        Gebruiker technieker = new Gebruiker(
                "Bouwman",
                "Bart",
                LocalDate.of(1985, 1, 1),
                new Adres("Teststraat", "2", "2000", "Antwerpen", "Belgie"),
                "bart@test.com",
                "0499000000",
                Rol.TECHNIEKER
        );
        assertThrows(IllegalArgumentException.class, () -> technieker.setGsm(""));
    }

    @Test
    void checkWachtwoordShouldReturnTrueWithCorrectCredentials() {
        boolean match = gebruiker.checkWachtwoord("jan.jansen@example.com", "012345678");
        assertTrue(match);
    }

    @Test
    void checkWachtwoordShouldReturnFalseWithWrongEmail() {
        assertFalse(gebruiker.checkWachtwoord("wrong@email.com", "012345678"));
    }

    @Test
    void checkWachtwoordShouldReturnFalseWithWrongPassword() {
        assertFalse(gebruiker.checkWachtwoord("jan.jansen@example.com", "wrongpass"));
    }

    @Test
    void addAndRemoveSiteShouldWorkCorrectly() {
        Site site = new Site("Test"); 
        gebruiker.addSite(site);
        assertTrue(gebruiker.getSitesSet().size() == 1);

        gebruiker.removeSite(site);
        assertFalse(gebruiker.getSitesSet().contains(site));
    }

    @Test
    void getSitesSetShouldBeUnmodifiable() {
        assertThrows(UnsupportedOperationException.class, () ->
                gebruiker.getSitesSet().add(new Site("Test"))
        );
    }

    @Test
    void toStringShouldContainRelevantInfo() {
        String output = gebruiker.toString();
        assertTrue(output.contains("Jan"));
        assertTrue(output.contains("Jansen"));
        assertTrue(output.contains("ADMINISTRATOR"));
        assertTrue(output.contains("jan.jansen@example.com"));
    }
}
