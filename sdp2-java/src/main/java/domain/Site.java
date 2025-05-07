package domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "sites")
@NamedQueries({
	@NamedQuery(name = "Site.findByVerantwoordelijkeId",
			query = "SELECT s FROM Site s WHERE s.verantwoordelijke.gebruikerID = :verantwoordelijkeId"),
})
@Getter @Setter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(exclude = {"siteId", "machines"})
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int siteId;

    private String naam;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "verantw_id")
    private Gebruiker verantwoordelijke;

    @OneToMany(mappedBy = "site", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<Machine> machines = new HashSet<>();

    public Site(String naam, Gebruiker verantwoordelijke) {
        setNaam(naam);
        setVerantwoordelijke(verantwoordelijke);
    }

    public Site(String naam) {
        setNaam(naam);
    }

    public void setNaam(String naam) {
        if (naam == null || naam.trim().isEmpty()) {
            throw new IllegalArgumentException("Site naam mag niet leeg zijn.");
        }
        this.naam = naam;
    }

    public void setVerantwoordelijke(Gebruiker verantwoordelijke) {
         if (verantwoordelijke == null) {
             throw new IllegalArgumentException("Site verantwoordelijke mag niet leeg zijn.");
         }
        this.verantwoordelijke = verantwoordelijke;
    }

    @Override
    public String toString() {
        return String.format("Site[id=%d, naam='%s', Verantwoordelijke=%s, #Machines=%d]",
                siteId, naam, (verantwoordelijke != null ? verantwoordelijke.getEmail() : "null"),
                (machines != null ? machines.size() : 0));
    }
}