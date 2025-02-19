package domein;

import java.time.LocalDate;

public class Technieker extends Gebruiker {
    public Technieker(String naam, String voornaam, LocalDate geboortedatum, String adres, String email, String gsm) {
        super(naam, voornaam, geboortedatum, adres, email, gsm, "Technieker", true);
        if (gsm == null || gsm.isBlank()) {
            throw new IllegalArgumentException("Gsm is verplicht voor Techniekers.");
        }
    }

    @Override
    public String getRol() {
        return "Technieker";
    }
}
