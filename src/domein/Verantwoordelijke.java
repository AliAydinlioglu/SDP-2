package domein;

import java.time.LocalDate;

class Verantwoordelijke extends Gebruiker {
    public Verantwoordelijke(String naam, String voornaam, LocalDate geboortedatum, String adres, String email, String gsm) {
        super(naam, voornaam, geboortedatum, adres, email, gsm, "Verantwoordelijke", true);
    }

    @Override
    public String getRol() {
        return "Verantwoordelijke";
    }
}
