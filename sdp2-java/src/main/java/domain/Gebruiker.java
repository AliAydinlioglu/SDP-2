package domain;

import java.io.Serializable;
import java.time.LocalDate;

import enums.Rol;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import javafx.beans.property.SimpleStringProperty;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NamedQueries({
    @NamedQuery(name = "Gebruiker.findByEmail",
                query = """
                        SELECT g
                        FROM Gebruiker g
                        WHERE g.email = :gebruikerEmail

                        """),
    @NamedQuery(name = "Gebruiker.findAll",
                query = """
                        SELECT g
                        FROM Gebruiker g
                        """)
})
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(exclude = "gebruikerID")

public class Gebruiker implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	@Column(name = "id")
	private int gebruikerID;
	
	private String achternaam;
    private String voornaam;
    private LocalDate geboorteDatum;
    @Embedded
    private Adres adres;
    private String email;
    @Column(name = "gsm_nr")
    private String gsm; // Optioneel, behalve voor Technieker
    @Enumerated(EnumType.STRING)
    private Rol rol;
    private boolean actief;
    
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @Column(name = "hashed_password")
    private String wachtwoord;

    public Gebruiker(String naam, String voornaam, LocalDate geboortedatum, Adres adres, String email, String gsm, Rol rol) {
        if (naam.isBlank() || voornaam.isBlank() || geboortedatum == null || email.isBlank()) {
            throw new IllegalArgumentException("Alle velden (behalve gsm) moeten ingevuld zijn.");
        }
        if (rol.equals(Rol.TECHNIEKER) && (gsm == null || gsm.isBlank())) {
            throw new IllegalArgumentException("Gsm is verplicht voor Techniekers.");
        }
        setAchternaam(naam);
        setVoornaam(voornaam);
        setGeboorteDatum(geboortedatum);
        setAdres(adres);
        setEmail(email);
        setRol(rol);
        setGsm(gsm);
        setActief(true);
        

        wachtwoord = "default"; // Placeholder, wachtwoord moet nog goed worden ingesteld

    }

    public void setEmail(String email) {
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email mag niet leeg zijn.");
        }
        this.email = email;
    }
    
    public SimpleStringProperty voornaamProperty() {
        return new SimpleStringProperty(voornaam);
    }
    
    public SimpleStringProperty achternaamProperty() {
		return new SimpleStringProperty(achternaam);
	}
    public SimpleStringProperty emailProperty() {
		return new SimpleStringProperty(email);
	}


    public void setGsm(String gsm) {
        if (rol.equals(Rol.TECHNIEKER) && (gsm == null || gsm.isBlank())) {
            throw new IllegalArgumentException("Technieker moet een gsm-nummer hebben.");
        }
        this.gsm = gsm;
    }

    private void setRol(Rol rol) {
    	this.rol = rol;
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s) - %s | Status: %s", voornaam, achternaam, rol, email, actief ? "Actief" : "Inactief");
    }

}
