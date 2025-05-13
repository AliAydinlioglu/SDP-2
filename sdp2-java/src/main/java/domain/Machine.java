package domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import enums.MachineStatus;
import enums.ProductionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "info")
    private String naam;

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

    @Column(name = "volgendOnderhoud")
    private LocalDate volgendOnderhoud;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "technieker_id")
    private Gebruiker technieker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site;

    public Machine(String naam, String productInfo, String locatie, MachineStatus status,
                   ProductionStatus productieStatus, int uptime, Gebruiker technieker,
                   int dagenSindsOnderhoud, LocalDate volgendOnderhoud, Site site) {

        if (naam == null || naam.isBlank() || locatie == null || locatie.isBlank()) {
            throw new IllegalArgumentException("Naam en locatie moeten ingevuld zijn.");
        }

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
        setProductInfo(productInfo);
        setLocatie(locatie);
        setStatus(status);
        setProductieStatus(productieStatus);
        setUptime(uptime);
        setTechnieker(technieker);
        setDagenSindsOnderhoud(dagenSindsOnderhoud);
        setVolgendOnderhoud(volgendOnderhoud);
        setSite(site);
    }


    @Override
    public String toString() {
        return String.format("Machine[ID=%d]: Naam: %s (Info: %s) - Status: %s | Uptime: %d | Tech: %s | Site: %s",
                machineID,
                naam,
                productInfo,
                status,
                uptime,
                (technieker != null ? technieker.getEmail() : "null"),
                (site != null ? site.getNaam() : "null"));
    }
}