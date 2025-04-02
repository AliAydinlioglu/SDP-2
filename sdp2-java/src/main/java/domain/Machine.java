package domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
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
@EqualsAndHashCode(exclude = "machineID")
public class Machine implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id")
    private int machineID;

    private String name;
    private String location;
    private String productInfo;

    @Column(name = "status")
    private String status;  // E.g., Draait, Gestopt [auto or manual]

    @Column(name = "production_status")
    private String productionStatus;  // E.g., Gezond, Nood aan onderhoud, falend

    @Column(name = "uptime")
    private int uptime;  // Uptime in hours or another metric

    private String technicianName;

    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    @Column(name = "days_since_last_maintenance")
    private int daysSinceLastMaintenance;

    @Column(name = "next_maintenance_date")
    private LocalDate nextMaintenanceDate;

    public Machine(String name, String location, String productInfo, String status,
                   String productionStatus, int uptime, String technicianName, LocalDate lastMaintenanceDate,
                   int daysSinceLastMaintenance, LocalDate nextMaintenanceDate) {

        // Controleer of verplichte velden leeg zijn
        if (name.isBlank() || location.isBlank() || productInfo.isBlank() || status.isBlank() || productionStatus.isBlank()) {
            throw new IllegalArgumentException("Alle velden moeten ingevuld zijn.");
        }

        // Controleer of uptime een geldig positief getal is
        if (uptime < 0) {
            throw new IllegalArgumentException("Uptime mag niet negatief zijn.");
        }

        // Controleer of het aantal dagen sinds het laatste onderhoud een geldig positief getal is
        if (daysSinceLastMaintenance < 0) {
            throw new IllegalArgumentException("Aantal dagen sinds het laatste onderhoud mag niet negatief zijn.");
        }

        // Controleer of de datum van het volgende onderhoud geldig is en niet in het verleden ligt
        if (nextMaintenanceDate == null || nextMaintenanceDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("De datum voor het volgende onderhoud mag niet in het verleden liggen.");
        }

        // Controleer of de naam van de technieker niet leeg is
        if (technicianName.isBlank()) {
            throw new IllegalArgumentException("Naam van de technieker mag niet leeg zijn.");
        }

        // Controleer of de laatste onderhoudsdatum niet in de toekomst ligt
        if (lastMaintenanceDate == null || lastMaintenanceDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("De laatste onderhoudsdatum mag niet in de toekomst liggen.");
        }

        // Als alle controles geslaagd zijn, stel de velden in
        this.name = name;
        this.location = location;
        this.productInfo = productInfo;
        this.status = status;
        this.productionStatus = productionStatus;
        this.uptime = uptime;
        this.technicianName = technicianName;
        this.lastMaintenanceDate = lastMaintenanceDate;
        this.daysSinceLastMaintenance = daysSinceLastMaintenance;
        this.nextMaintenanceDate = nextMaintenanceDate;
    }


    @Override
    public String toString() {
        return String.format("Machine: %s - Status: %s | Uptime: %d | Technician: %s",
                name, status, uptime, technicianName);
    }
}
