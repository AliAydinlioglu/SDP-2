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
import lombok.*;

@Entity
@NamedQueries({
        @NamedQuery(name = "Machine.findAll", query = "SELECT m FROM Machine m"),
        @NamedQuery(name = "Machine.findById", query = "SELECT m FROM Machine m WHERE m.machineID = :machineID"),
        @NamedQuery(name = "machines", query = "SELECT m FROM Machine m WHERE m.technieker.gebruikerID = :techniekerId") // Added
                                                                                                                         // for
                                                                                                                         // the
                                                                                                                         // SQL
                                                                                                                         // error
})
@Getter
@Setter
@Table(name = "machines")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(exclude = { "machineID", "site", "technieker" })
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

    @Transient // Tell JPA to ignore this field for database operations
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

        if (naam == null || naam.isBlank()) {
            throw new IllegalArgumentException("Naam mag niet leeg zijn.");
        }
        if (locatie == null || locatie.isBlank()) {
            throw new IllegalArgumentException("Locatie mag niet leeg zijn.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status mag niet leeg zijn.");
        }
        if (productieStatus == null) {
            throw new IllegalArgumentException("Productiestatus mag niet leeg zijn.");
        }
        if (uptime < 0) {
            throw new IllegalArgumentException("Uptime mag niet negatief zijn.");
        }
        if (technieker == null) {
            throw new IllegalArgumentException("Technieker moet opgegeven zijn.");
        }
        if (dagenSindsOnderhoud < 0) {
            throw new IllegalArgumentException("Aantal dagen sinds het laatste onderhoud mag niet negatief zijn.");
        }
        if (volgendOnderhoud == null) {
            throw new IllegalArgumentException("De datum voor het volgende onderhoud mag niet leeg zijn.");
        }
        if (site == null) {
            throw new IllegalArgumentException("Site moet opgegeven zijn.");
        }
        this.naam = naam;
        this.productInfo = productInfo;
        this.locatie = locatie;
        this.status = status;
        this.productieStatus = productieStatus;
        this.uptime = uptime;
        this.technieker = technieker;
        this.dagenSindsOnderhoud = dagenSindsOnderhoud;
        this.volgendOnderhoud = volgendOnderhoud;
        this.site = site;
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

    public static class Builder {
        private String naam;
        private String productInfo;
        private String locatie;
        private MachineStatus status;
        private ProductionStatus productieStatus;
        private int uptime;
        private int dagenSindsOnderhoud;
        private LocalDate volgendOnderhoud;
        private Gebruiker technieker;
        private Site site;

        public Builder naam(String naam) {
            if (naam == null || naam.isBlank()) {
                throw new IllegalArgumentException("Naam mag niet leeg zijn");
            }
            this.naam = naam;
            return this;
        }

        public Builder productInfo(String productInfo) {
            if (productInfo == null || productInfo.isBlank()) {
                throw new IllegalArgumentException("Productinformatie mag niet leeg zijn");
            }
            this.productInfo = productInfo;
            return this;
        }

        public Builder locatie(String locatie) {
            if (locatie == null || locatie.isBlank()) {
                throw new IllegalArgumentException("Locatie mag niet leeg zijn");
            }
            this.locatie = locatie;
            return this;
        }

        public Builder status(MachineStatus status) {
            if (status == null) {
                throw new IllegalArgumentException("Status mag niet leeg zijn");
            }
            this.status = status;
            return this;
        }

        public Builder productieStatus(ProductionStatus productieStatus) {
            if (productieStatus == null) {
                throw new IllegalArgumentException("Productiestatus mag niet leeg zijn");
            }
            this.productieStatus = productieStatus;
            return this;
        }

        public Builder uptime(int uptime) {
            if (uptime < 0) {
                throw new IllegalArgumentException("Uptime mag niet negatief zijn");
            }
            this.uptime = uptime;
            return this;
        }

        public Builder dagenSindsOnderhoud(int dagenSindsOnderhoud) {
            if (dagenSindsOnderhoud < 0) {
                throw new IllegalArgumentException("Dagen sinds onderhoud mag niet negatief zijn");
            }
            this.dagenSindsOnderhoud = dagenSindsOnderhoud;
            return this;
        }

        public Builder volgendOnderhoud(LocalDate volgendOnderhoud) {
            if (volgendOnderhoud == null) {
                throw new IllegalArgumentException("Volgend onderhoud mag niet leeg zijn");
            }
            this.volgendOnderhoud = volgendOnderhoud;
            return this;
        }

        public Builder technieker(Gebruiker technieker) {
            if (technieker == null) {
                throw new IllegalArgumentException("Technieker mag niet leeg zijn");
            }
            this.technieker = technieker;
            return this;
        }

        public Builder site(Site site) {
            if (site == null) {
                throw new IllegalArgumentException("Site mag niet leeg zijn");
            }
            this.site = site;
            return this;
        }

        public Machine build() {
            return new Machine(naam, productInfo, locatie, status, productieStatus, uptime,
                    technieker, dagenSindsOnderhoud, volgendOnderhoud, site);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}