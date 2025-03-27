package gebruikers;

import java.time.LocalDate;

import enums.Rol;

public class Gebruiker {
    private String naam;
    private String voornaam;
    private LocalDate geboortedatum;
    private String adres;
    private String email;
    private String gsm; // Optioneel, behalve voor Technieker
    private Rol rol;
    private boolean Status;

    public Gebruiker(String naam, String voornaam, LocalDate geboortedatum, String adres, String email, String gsm, Rol rol, boolean Status) {
        if (naam.isBlank() || voornaam.isBlank() || geboortedatum == null || adres.isBlank() || email.isBlank()) {
            throw new IllegalArgumentException("Alle velden (behalve gsm) moeten ingevuld zijn.");
        }
        if (rol.equals(Rol.TECHNIEKER) && (gsm == null || gsm.isBlank())) {
            throw new IllegalArgumentException("Gsm is verplicht voor Techniekers.");
        }
        this.naam = naam;
        this.voornaam = voornaam;
        this.geboortedatum = geboortedatum;
        this.adres = adres;
        this.email = email;
        this.gsm = gsm;
        this.rol = rol;
        this.Status = Status;
    }

    public void setEmail(String email) {
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email mag niet leeg zijn.");
        }
        this.email = email;
    }

    public void setStatus(boolean actief) {
        this.Status = actief;
    }

    public void setGsm(String gsm) {
        if (rol.equalsIgnoreCase("Technieker") && (gsm == null || gsm.isBlank())) {
            throw new IllegalArgumentException("Technieker moet een gsm-nummer hebben.");
        }
        this.gsm = gsm;
    }

    public boolean getStatus() {
        return Status;
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s) - %s | Status: %s", voornaam, naam, rol, email, Status ? "Actief" : "Inactief");
    }

}
