package domain;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;

public class LogTest {

	@Test
    public void LogTestValid() {
        Gebruiker gebruiker = new Gebruiker();
        String actie = "Ingelogd";
        String opmerking = "Gebruiker is succesvol ingelogd";

        Log log = new Log(gebruiker, actie, opmerking);

        assertEquals(gebruiker, log.getGebruiker());
        assertEquals(actie, log.getActie());
        assertEquals(opmerking, log.getOpmerking());
        assertNotNull(log.getDate());

    }

    @Test
    public void createLogGebruikerNull_Exception() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Log(null, "Actie", "Opmerking");
        });
    }

    @Test
    public void createLogActieNull_Exception() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Log(new Gebruiker(), null, "Opmerking");
        });
    }

    @Test
    public void createLogActieBlank_Exception() {
    	assertThrows(IllegalArgumentException.class, () -> {
            new Log(new Gebruiker(), "   ", "Opmerking");
        });
    }
}
