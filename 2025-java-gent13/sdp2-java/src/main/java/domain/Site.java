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
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    private Site(String naam, Gebruiker verantwoordelijke, Set<Machine> machines) {
//        Voor Builder
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

    public static class Builder {
        private String naam;
        private Gebruiker verantwoordelijke;
        private Set<Machine> machines = new HashSet<>();

        public Builder naam(String naam) {
            if (naam == null || naam.isBlank()) {
                throw new IllegalArgumentException("Naam mag niet leeg zijn");
            }
            this.naam = naam;
            return this;
        }

        public Builder verantwoordelijke(Gebruiker verantwoordelijke) {
            this.verantwoordelijke = verantwoordelijke;
            return this;
        }


        public Builder machines(Set<Machine> machines) {
            if (machines == null || machines.isEmpty()) {
                throw new IllegalArgumentException("Machines mag niet leeg zijn");
            }
            this.machines = machines;
            return this;
        }

        public Site build() {
            if (this.naam == null || this.naam.isBlank()) {
                throw new IllegalStateException("Naam is niet ingesteld in de builder en is verplicht.");
            }
            if (this.verantwoordelijke == null) {
                throw new IllegalStateException("Verantwoordelijke is niet ingesteld en is verplicht.");
            }

            Site site = new Site(this.naam);
            if (this.verantwoordelijke != null) {
                site.setVerantwoordelijke(this.verantwoordelijke);
            }
            return site;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
