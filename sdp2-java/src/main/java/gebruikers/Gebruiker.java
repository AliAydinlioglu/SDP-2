package gebruikers;

import java.time.LocalDate;

import enums.Rol;
import lombok.Getter;
import lombok.Setter;

public class Gebruiker {
    @Setter @Getter private String naam;
    @Setter @Getter private String voornaam;
    @Setter @Getter private LocalDate geboortedatum;
    @Setter @Getter private String adres;
    @Getter private String email;
    @Getter private String gsm; // Optioneel, behalve voor Technieker
    @Getter private Rol rol;
    @Setter @Getter private boolean Status;

    public Gebruiker(String naam, String voornaam, LocalDate geboortedatum, String adres, String email, String gsm, Rol rol, boolean Status) {
        if (naam.isBlank() || voornaam.isBlank() || geboortedatum == null || adres.isBlank() || email.isBlank()) {
            throw new IllegalArgumentException("Alle velden (behalve gsm) moeten ingevuld zijn.");
        }
        if (rol.equals(Rol.TECHNIEKER) && (gsm == null || gsm.isBlank())) {
            throw new IllegalArgumentException("Gsm is verplicht voor Techniekers.");
        }
        setNaam(naam);
        setVoornaam(voornaam);
        setGeboortedatum(geboortedatum);
        setAdres(adres);
        setEmail(email);
        setGsm(gsm);
        setRol(rol);
        setStatus(Status);
    }

    public void setEmail(String email) {
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email mag niet leeg zijn.");
        }
        this.email = email;
    }


    public void setGsm(String gsm) {
        if (rol.equals(Rol.TECHNIEKER) && (gsm == null || gsm.isBlank())) {
            throw new IllegalArgumentException("Technieker moet een gsm-nummer hebben.");
        }
        this.gsm = gsm;
    }

    public boolean getStatus() {
        return Status;
    }
    private void setRol(Rol rol) {
    	this.rol = rol;
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s) - %s | Status: %s", voornaam, naam, rol, email, Status ? "Actief" : "Inactief");
    }

}
