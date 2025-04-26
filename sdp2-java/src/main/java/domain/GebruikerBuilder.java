package domain;

import java.time.LocalDate;

import enums.Rol;

public class GebruikerBuilder {
    private String voornaam;
    private String achternaam;
    private LocalDate geboorteDatum;
    private Adres adres;
    private String email;
    private String gsm;
    private Rol rol;
    private boolean actief = true;

    public GebruikerBuilder voornaam(String voornaam) {
    	if (voornaam == null || voornaam.isBlank()) {
			throw new IllegalArgumentException("Voornaam mag niet leeg zijn");
		}
        this.voornaam = voornaam;
        return this;
    }

    public GebruikerBuilder achternaam(String achternaam) {
		if (achternaam == null || achternaam.isBlank()) {
			throw new IllegalArgumentException("Achternaam mag niet leeg zijn");
		}
        this.achternaam = achternaam;
        return this;
    }

    public GebruikerBuilder geboorteDatum(LocalDate geboorteDatum) {
    	if(geboorteDatum == null ) {
    		throw new IllegalArgumentException("Geboortedatum mag niet leeg zijn");
    	} else if(LocalDate.now().getYear() - geboorteDatum.getYear() < 18) {
    		throw new IllegalArgumentException("Gebruiker moet minstens 18 jaar geleden zijn");
		} else if(geboorteDatum.isAfter(LocalDate.now())) {
			throw new IllegalArgumentException("Geboortedatum mag niet in de toekomst liggen");
    	}
        this.geboorteDatum = geboorteDatum;
        return this;
    }

    public GebruikerBuilder adres(Adres adres) {
    	if(adres == null) {
    		throw new IllegalArgumentException("Adres mag niet leeg zijn");
    	}
        this.adres = adres;
        return this;
    }

    public GebruikerBuilder email(String email) {
    	if(email == null || email.isBlank()) {
    		throw new IllegalArgumentException("Email mag niet leeg zijn");
    	}
        this.email = email;
        return this;
    }

    public GebruikerBuilder gsm(String gsm) {
    	if (this.rol.equals(Rol.TECHNIEKER) && (gsm == null || gsm.isBlank())) {
    		throw new IllegalArgumentException("Technieker moet een gsm-nummer hebben.");
    	}
        this.gsm = gsm;
        return this;
    }

    public GebruikerBuilder rol(Rol rol) {
    	if(rol == null) {
    		throw new IllegalArgumentException("Rol mag niet leeg zijn");
    	}
        this.rol = rol;
        return this;
    }

    public GebruikerBuilder actief(boolean actief) {
        this.actief = actief;
        return this;
    }

    public Gebruiker build() {
        return new Gebruiker(this.voornaam, this.achternaam, this.geboorteDatum, this.adres, this.email, this.gsm, this.rol, this.actief);
    }
}
