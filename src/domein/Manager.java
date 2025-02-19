package domein;

import java.time.LocalDate;

public class Manager extends Gebruiker {
    public Manager(String naam, String voornaam, LocalDate geboortedatum, String adres, String email, String gsm) {
        super(naam, voornaam, geboortedatum, adres, email, gsm, "Manager", true);
    }

    @Override
    public String getRol() {
        return "Manager";
    }
}
