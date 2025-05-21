package domain;

import enums.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GebruikerTest {

    private Gebruiker gebruiker;
    private Adres testAdres;

    @BeforeEach
    void setUp() {
        testAdres = new Adres("Straat", "1", "1000", "Brussel", "Belgie");
        gebruiker = new Gebruiker(
                "Jan",
                "Jansen",
                LocalDate.of(1990, 5, 12),
                testAdres,
                "jan.jansen@example.com",
                "0499123456",
                Rol.ADMINISTRATOR,
                true);
    }

    @Test
    void constructorShouldThrowWhenMissingRequiredFields() {
        // Test with empty voornaam
        assertThrows(IllegalArgumentException.class, () -> new Gebruiker("", "Jansen", LocalDate.now(), testAdres,
                "test@mail.com", "", Rol.ADMINISTRATOR, true));
        // Test with empty achternaam
        assertThrows(IllegalArgumentException.class, () -> new Gebruiker("Jan", "", LocalDate.now(), testAdres,
                "test@mail.com", "", Rol.ADMINISTRATOR, true));
        // Test with null geboortedatum
        assertThrows(IllegalArgumentException.class, () -> new Gebruiker("Jan", "Jansen", null, testAdres,
                "test@mail.com", "", Rol.ADMINISTRATOR, true));
        // Test with null adres (Adres itself is tested separately for its fields)
        assertThrows(IllegalArgumentException.class,
                () -> new Gebruiker("Jan", "Jansen", LocalDate.now(), null, "test@mail.com", "", Rol.ADMINISTRATOR,
                        true));
        // Test with empty email
        assertThrows(IllegalArgumentException.class,
                () -> new Gebruiker("Jan", "Jansen", LocalDate.now(), testAdres, "", "", Rol.ADMINISTRATOR,
                        true));
    }

    @Test
    void techniekerMustHaveGsm() {
        assertThrows(IllegalArgumentException.class, () -> new Gebruiker("Bart", "Bouwman", LocalDate.now(),
                testAdres, "bart@test.com", "", Rol.TECHNIEKER, true));
        assertThrows(IllegalArgumentException.class, () -> new Gebruiker("Bart", "Bouwman", LocalDate.now(),
                testAdres, "bart@test.com", null, Rol.TECHNIEKER, true));
    }

    @Test
    void setEmailShouldRejectBlank() {
        assertThrows(IllegalArgumentException.class, () -> gebruiker.setEmail("  "));
        assertThrows(IllegalArgumentException.class, () -> gebruiker.setEmail(null));
    }

    @Test
    void setGsmShouldRejectBlankOrNullForTechnieker() {
        // Corrected order: voornaam="Bart", achternaam="Bouwman"
        Gebruiker technieker = new Gebruiker(
                "Bart",
                "Bouwman",
                LocalDate.of(1985, 1, 1),
                new Adres("Teststraat", "2", "2000", "Antwerpen", "Belgie"),
                "bart@test.com",
                "0499000000",
                Rol.TECHNIEKER,
                true);
        assertThrows(IllegalArgumentException.class, () -> technieker.setGsm(""));
        assertThrows(IllegalArgumentException.class, () -> technieker.setGsm(null));
    }

    @Test
    void setGsmShouldAcceptBlankOrNullForNonTechnieker() {
        gebruiker.setRol(Rol.ADMINISTRATOR); // Ensure not technieker
        assertDoesNotThrow(() -> gebruiker.setGsm(""));
        assertDoesNotThrow(() -> gebruiker.setGsm(null));
    }

    @Test
    void setRolShouldRejectNull() {
        assertThrows(IllegalArgumentException.class, () -> gebruiker.setRol(null));
    }

    @Test
    void checkWachtwoordShouldReturnTrueWithCorrectCredentials() {
        boolean match = gebruiker.checkWachtwoord("jan.jansen@example.com", "Jan.Jansen");
        assertTrue(match, "Password check should succeed with correct email and password");
    }

    @Test
    void checkWachtwoordShouldReturnFalseWithWrongEmail() {
        assertFalse(gebruiker.checkWachtwoord("wrong@email.com", "Jan.Jansen"),
                "Password check should fail with incorrect email");
    }

    @Test
    void checkWachtwoordShouldReturnFalseWithWrongPassword() {
        assertFalse(gebruiker.checkWachtwoord("jan.jansen@example.com", "wrongpass"),
                "Password check should fail with incorrect password");
    }

    @Test
    void addAndRemoveSiteShouldWorkCorrectly() {
        Site mockSite = mock(Site.class);

        // Test adding a site
        gebruiker.addSite(mockSite);

        // Verify that the site's verantwoordelijke was set to this gebruiker
        verify(mockSite).setVerantwoordelijke(gebruiker);

        assertTrue(gebruiker.getSitesSet().contains(mockSite), "Gebruiker's sites should contain the added site");
        assertEquals(1, gebruiker.getSitesSet().size(), "Gebruiker's sites set size should be 1 after adding one site");

        // Test removing a site
        gebruiker.removeSite(mockSite);
        assertFalse(gebruiker.getSitesSet().contains(mockSite),
                "Gebruiker's sites should not contain the removed site");
        assertEquals(0, gebruiker.getSitesSet().size(),
                "Gebruiker's sites set size should be 0 after removing the site");
    }

    @Test
    void getSitesSetShouldBeUnmodifiable() {
        Site anotherSite = mock(Site.class);
        assertThrows(UnsupportedOperationException.class, () -> gebruiker.getSitesSet().add(anotherSite));
    }

    @Test
    void toStringShouldContainRelevantInfo() {
        // With setUp corrected (voornaam="Jan", achternaam="Jansen")
        String output = gebruiker.toString();
        assertTrue(output.contains("Jan"), "toString should contain voornaam");
        assertTrue(output.contains("Jansen"), "toString should contain achternaam");
        assertTrue(output.contains("ADMINISTRATOR"), "toString should contain rol");
        assertTrue(output.contains("jan.jansen@example.com"), "toString should contain email");
        assertTrue(output.contains("Actief"), "toString should contain status");
    }

    @Test
    void builderShouldCreateGebruikerCorrectly() {
        LocalDate geboorteDatum = LocalDate.of(1995, 6, 15);
        Adres newAdres = new Adres("Builderstraat", "10", "3000", "Leuven", "Belgie");
        Gebruiker builtGebruiker = Gebruiker.builder()
                .voornaam("Max")
                .achternaam("Mustermann")
                .geboorteDatum(geboorteDatum)
                .adres(newAdres)
                .email("max.mustermann@example.com")
                .gsm("0477123456")
                .rol(Rol.ADMINISTRATOR) // Changed from KLANT to ADMINISTRATOR
                .actief(true)
                .build();

        assertEquals("Max", builtGebruiker.getVoornaam());
        assertEquals("Mustermann", builtGebruiker.getAchternaam());
        assertEquals(geboorteDatum, builtGebruiker.getGeboorteDatum());
        assertEquals(newAdres, builtGebruiker.getAdres());
        assertEquals("max.mustermann@example.com", builtGebruiker.getEmail());
        assertEquals("0477123456", builtGebruiker.getGsm());
        assertEquals(Rol.ADMINISTRATOR, builtGebruiker.getRol()); // Changed from KLANT to ADMINISTRATOR
        assertTrue(builtGebruiker.isActief());
    }

    @Test
    void builderShouldThrowForInvalidData() {
        // Test builder validations from Gebruiker.Builder
        assertThrows(IllegalArgumentException.class, () -> Gebruiker.builder().voornaam(""));
        assertThrows(IllegalArgumentException.class, () -> Gebruiker.builder().achternaam(null));
        assertThrows(IllegalArgumentException.class,
                () -> Gebruiker.builder().geboorteDatum(LocalDate.now().plusYears(1))); // Future date
        assertThrows(IllegalArgumentException.class,
                () -> Gebruiker.builder().geboorteDatum(LocalDate.now().minusYears(17))); // Too young
        assertThrows(IllegalArgumentException.class, () -> Gebruiker.builder().email(" "));
        assertThrows(IllegalArgumentException.class, () -> Gebruiker.builder().rol(null));

        // Technieker must have GSM
        assertThrows(IllegalArgumentException.class, () -> Gebruiker.builder()
                .voornaam("Test")
                .achternaam("Technieker")
                .geboorteDatum(LocalDate.of(2000, 1, 1))
                .adres(testAdres)
                .email("tech@test.com")
                .rol(Rol.TECHNIEKER)
                .gsm(null) // Missing GSM for technieker
                .build());
    }
}
