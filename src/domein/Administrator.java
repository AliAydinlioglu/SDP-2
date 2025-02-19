package domein;

import java.time.LocalDate;

public class Administrator extends Gebruiker {
    public Administrator(String naam, String voornaam, LocalDate geboortedatum, String adres, String email, String gsm) {
        super(naam, voornaam, geboortedatum, adres, email, gsm, "Administrator", true);
    }

    @Override
    public String getRol() {
        return "Administrator";
    }
}

