package domain;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;

public class LogTest {

	@Test
    public void LogTestValid() {
        Gebruiker gebruiker = new Gebruiker();
        String actie = "Ingelogd";
        String opmerking = "Gebruiker is succesvol ingelogd";

        Log log = Log.builder()
				.gebruiker(gebruiker)
				.actie(actie)
				.opmerking(opmerking)
				.build();

        assertEquals(gebruiker, log.getGebruiker());
        assertEquals(actie, log.getActie());
        assertEquals(opmerking, log.getOpmerking());
        assertNotNull(log.getDate());

    }

    @Test
    public void createLogGebruikerNull_Exception() {
        assertThrows(IllegalArgumentException.class, () -> {
        	Log.builder()
			.gebruiker(null)
			.actie("Test")
			.opmerking("Test")
			.build();;
        });
    }

    @Test
    public void createLogActieNull_Exception() {
        assertThrows(IllegalArgumentException.class, () -> {
        	Log.builder()
			.gebruiker(new Gebruiker())
			.actie(null)
			.opmerking("test")
			.build();
        });
    }

    @Test
    public void createLogActieBlank_Exception() {
    	assertThrows(IllegalArgumentException.class, () -> {
    		Log.builder()
			.gebruiker(new Gebruiker())
			.actie("    ")
			.opmerking("test")
			.build();
        });
    }
}
