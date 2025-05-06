package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MeldingTest {

    private Melding melding;
    private Gebruiker mockGebruiker;
    private LocalDate testDatum;

    @BeforeEach
    public void setUp() {
        mockGebruiker = new Gebruiker();
        testDatum = LocalDate.now();
        melding = new Melding("Test beschrijving", mockGebruiker, "Open", "Storing", testDatum);
    }

    @Test
    public void testMeldingConstructorAndGetters() {
        assertEquals("Test beschrijving", melding.getBeschrijving());
        assertEquals(mockGebruiker, melding.getGebruiker());
        assertEquals("Open", melding.getStatus());
        assertEquals("Storing", melding.getType());
        assertEquals(testDatum, melding.getDatum());
        assertEquals(0, melding.getMeldingId());
    }

    @Test
    public void testSetBeschrijving() {
        melding.setBeschrijving("Nieuwe beschrijving");
        assertEquals("Nieuwe beschrijving", melding.getBeschrijving());
    }

    @Test
    public void testSetGebruiker() {
        Gebruiker nieuweGebruiker = new Gebruiker();
        melding.setGebruiker(nieuweGebruiker);
        assertEquals(nieuweGebruiker, melding.getGebruiker());
    }

    @Test
    public void testSetStatus() {
        melding.setStatus("Gesloten");
        assertEquals("Gesloten", melding.getStatus());
    }

    @Test
    public void testSetType() {
        melding.setType("Onderhoud");
        assertEquals("Onderhoud", melding.getType());
    }

    @Test
    public void testSetDatum() {
        LocalDate newDate = LocalDate.now().plusDays(1);
        melding.setDatum(newDate);
        assertEquals(newDate, melding.getDatum());
    }

    @Test
    public void testProtectedNoArgsConstructor() {
        Melding defaultMelding = new Melding();
        assertNotNull(defaultMelding);
        assertNull(defaultMelding.getBeschrijving());
        assertNull(defaultMelding.getGebruiker());
        assertNull(defaultMelding.getStatus());
        assertNull(defaultMelding.getType());
        assertNull(defaultMelding.getDatum());
    }
}
