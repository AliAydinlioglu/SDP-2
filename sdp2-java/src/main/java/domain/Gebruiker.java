package domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import enums.Rol;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

                        """)
})
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(exclude = {"gebruikerID", "sites", "onderhouden", "machines"})
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
    
    @OneToMany(mappedBy = "verantwoordelijke", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @Setter(AccessLevel.NONE)
    private Set<Site> sites = new HashSet<Site>();
    
    @OneToMany(mappedBy = "technieker", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @Setter(AccessLevel.NONE)
    private Set<Onderhoud> onderhouden = new HashSet<Onderhoud>();
    
    @OneToMany(mappedBy = "technieker", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @Setter(AccessLevel.NONE)
    private Set<Machine> machines = new HashSet<Machine>();
    
    
    private static final Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(16, 32, 1, 131072, 6);

    public Gebruiker(String voornaam, String achternaam, LocalDate geboortedatum, Adres adres, String email, String gsm, Rol rol, boolean actief) {
        if (achternaam.isBlank() || voornaam.isBlank() || geboortedatum == null || adres == null) {
            throw new IllegalArgumentException("Alle velden (behalve gsm) moeten ingevuld zijn.");
        }
        setAchternaam(achternaam);
        setVoornaam(voornaam);
        setGeboorteDatum(geboortedatum);
        setAdres(adres);
        setEmail(email);
        setRol(rol);
        setGsm(gsm);
        setActief(actief);
        
        

       // wachtwoord = "012345678"; // Placeholder, wachtwoord moet nog goed worden ingesteld
        setWachtwoord("012345678");

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

    public void setRol(Rol rol) {
    	if(rol == null) {
			throw new IllegalArgumentException("Rol moet ingevuld zijn.");
		}

    	this.rol = rol;
    }
    
    private void setWachtwoord(String wachtwoord) {
        if (wachtwoord == null || wachtwoord.isBlank() || wachtwoord.length() < 8) {
            throw new IllegalArgumentException("Wachtwoord mag niet leeg zijn en moet minstens 8 karakters lang zijn.");
        }
        this.wachtwoord = encoder.encode(wachtwoord);
    }
    
    public boolean checkWachtwoord(String email, String wachtwoord) {
        return encoder.matches(wachtwoord, this.wachtwoord) && this.email.equals(email);
    }
    
    public void addSite(Site site) {
    	sites.add(site);
    	site.setVerantwoordelijke(this);
    }
    
    public void removeSite(Site site) {
    	sites.remove(site);
    }
    
    public Set<Site> getSitesSet(){
    	return Collections.unmodifiableSet(sites);
    }
    
    public boolean getActief() {
    	return actief;
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s) - %s | Status: %s", voornaam, achternaam, rol, email, actief ? "Actief" : "Inactief");
    }

}
