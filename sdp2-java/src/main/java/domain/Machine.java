package domain;

import enums.MachineStatus;
import enums.ProductionStatus;
import jakarta.persistence.*;
import javafx.beans.property.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@NamedQueries({
        @NamedQuery(name = "Machine.findAll", query = "SELECT m FROM Machine m"),
        @NamedQuery(name = "Machine.findById", query = "SELECT m FROM Machine m WHERE m.machineID = :machineID")
})
@Getter
@Setter
@Table(name = "machines")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(exclude = {"machineID", "site", "technieker"})
public class Machine implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id")
    private int machineID;

    // Java 'naam' veld gemapt aan database 'info' kolom
    @Column(name = "info")
    private String naam;

    // ProductInfo niet gemapt aan database (geen aparte kolom in afbeelding)
    @Transient
    private String productInfo;

    @Column(name = "locatie")
    private String locatie;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MachineStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "prod_status")
    private ProductionStatus productieStatus;

    @Column(name = "uptime")
    private int uptime;

    @Column(name = "dagenSindsOnderhoud")
    private int dagenSindsOnderhoud;

    @Column(name = "volgendOnderhoud") // Aanname: DB type is DATE
    private LocalDate volgendOnderhoud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technieker_id")
    private Gebruiker technieker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site;

    // Constructor aangepast: 'naam' toegevoegd (gemapt aan DB 'info'), 'productInfo' toegevoegd (als @Transient), 'lastMaintenanceDate' verwijderd, 'technicianName' vervangen door 'technieker' (Gebruiker)
    public Machine(String naam, String productInfo, String locatie, MachineStatus status,
                   ProductionStatus productieStatus, int uptime, Gebruiker technieker,
                   int dagenSindsOnderhoud, LocalDate volgendOnderhoud, Site site) {

        // Validatie aangepast
        if (naam == null || naam.isBlank() || locatie == null || locatie.isBlank()) {
            throw new IllegalArgumentException("Naam en locatie moeten ingevuld zijn.");
        }
        // productInfo is @Transient, validatie misschien niet nodig, of anders:
        // if (productInfo == null || productInfo.isBlank()){
        //     throw new IllegalArgumentException("Product Info mag niet leeg zijn.");
        // }
        if (technieker == null) {
            throw new IllegalArgumentException("Technieker moet opgegeven zijn.");
        }
        if (site == null) {
            throw new IllegalArgumentException("Site moet opgegeven zijn.");
        }
        if (uptime < 0) {
            throw new IllegalArgumentException("Uptime mag niet negatief zijn.");
        }
        if (dagenSindsOnderhoud < 0) {
            throw new IllegalArgumentException("Aantal dagen sinds het laatste onderhoud mag niet negatief zijn.");
        }
        if (volgendOnderhoud == null) {
            throw new IllegalArgumentException("De datum voor het volgende onderhoud mag niet leeg zijn.");
        }

        setNaam(naam);
        setProductInfo(productInfo); // Setter voor @Transient veld
        setLocatie(locatie);
        setStatus(status);
        setProductieStatus(productieStatus);
        setUptime(uptime);
        setTechnieker(technieker);
        setDagenSindsOnderhoud(dagenSindsOnderhoud);
        setVolgendOnderhoud(volgendOnderhoud);
        setSite(site);
    }

    // --- JavaFX Properties (voorbeelden, pas aan/voeg toe indien nodig) ---

    public StringProperty naamProperty() {
        return new SimpleStringProperty(naam);
    }

    public StringProperty productInfoProperty() {
        return new SimpleStringProperty(productInfo); // Voor @Transient veld
    }

    public StringProperty locatieProperty() {
        return new SimpleStringProperty(locatie);
    }

    public ObjectProperty<MachineStatus> statusProperty() {
        return new SimpleObjectProperty<>(status);
    }

    public ObjectProperty<ProductionStatus> productieStatusProperty() {
        return new SimpleObjectProperty<>(productieStatus);
    }

    public IntegerProperty uptimeProperty() {
        return new SimpleIntegerProperty(uptime);
    }

    public StringProperty techniekerNaamProperty() {
        String naamTech = (technieker != null) ? technieker.getVoornaam() + " " + technieker.getAchternaam() : "N/A";
        return new SimpleStringProperty(naamTech);
    }

    public StringProperty siteNaamProperty() {
        String naamSite = (site != null) ? site.getNaam() : "N/A";
        return new SimpleStringProperty(naamSite);
    }


    // --- toString Aangepast ---
    @Override
    public String toString() {
        // Inclusief @Transient productInfo
        return String.format("Machine[ID=%d]: Naam: %s (Info: %s) - Status: %s | Uptime: %d | Tech: %s | Site: %s",
                machineID,
                naam,
                productInfo, // Weergegeven maar niet persistent
                status,
                uptime,
                (technieker != null ? technieker.getEmail() : "null"),
                (site != null ? site.getNaam() : "null"));
    }
}