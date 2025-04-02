package domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Entity
@Table(name = "sites")
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


    public StringProperty naamProperty() {
        return new SimpleStringProperty(naam);
    }

    public StringProperty verantwoordelijkeNaamProperty() {
        String naamVerantwoordelijke = (verantwoordelijke != null) ? verantwoordelijke.getVoornaam() + " " + verantwoordelijke.getAchternaam() : "Niet toegewezen";
        return new SimpleStringProperty(naamVerantwoordelijke);
    }

    public IntegerProperty aantalMachinesProperty() {
        return new SimpleIntegerProperty(machines.size());
    }

    @Override
    public String toString() {
        return String.format("Site[id=%d, naam='%s', Verantwoordelijke=%s, #Machines=%d]",
                siteId, naam, (verantwoordelijke != null ? verantwoordelijke.getEmail() : "null"), machines.size());
    }
}