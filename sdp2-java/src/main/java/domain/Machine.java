package domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import enums.MachineStatus;
import enums.ProductionStatus;
import jakarta.persistence.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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
    private MachineStatus status;

    @Column(name = "production_status")
    private ProductionStatus productionStatus;

    @Column(name = "uptime")
    private int uptime;  // Uptime in uren of een andere eenheid

    private String technicianName;

    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    @Column(name = "days_since_last_maintenance")
    private int daysSinceLastMaintenance;

    @Column(name = "next_maintenance_date")
    private LocalDate nextMaintenanceDate;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;

    public Machine(String name, String location, String productInfo, MachineStatus status,
                   ProductionStatus productionStatus, int uptime, String technicianName, LocalDate lastMaintenanceDate,
                   int daysSinceLastMaintenance, LocalDate nextMaintenanceDate) {

        // Controleer of verplichte velden leeg zijn
        if (name == null || name.isBlank() || location == null || location.isBlank() || productInfo == null || productInfo.isBlank()) {
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
        if (technicianName == null || technicianName.isBlank()) {
            throw new IllegalArgumentException("Naam van de technieker mag niet leeg zijn.");
        }

        // Controleer of de laatste onderhoudsdatum niet in de toekomst ligt
        if (lastMaintenanceDate == null || lastMaintenanceDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("De laatste onderhoudsdatum mag niet in de toekomst liggen.");
        }
        setName(name);
        setLocation(location);
        setProductInfo(productInfo);
        setStatus(status);
        setProductionStatus(productionStatus);
        setUptime(uptime);
        setTechnicianName(technicianName);
        setLastMaintenanceDate(lastMaintenanceDate);
        setDaysSinceLastMaintenance(daysSinceLastMaintenance);
        setNextMaintenanceDate(nextMaintenanceDate);
    }

    public SimpleStringProperty nameProperty() {
        return new SimpleStringProperty(name);

    }

    public SimpleStringProperty technicianNameProperty() {
        return new SimpleStringProperty(technicianName);

    }

    public ObjectProperty<MachineStatus> statusProperty() {
        return new SimpleObjectProperty<>(status);
    }

    public SimpleIntegerProperty uptimeProperty() {
        return new SimpleIntegerProperty(uptime);
    }

    @Override
    public String toString() {
        return String.format("Machine: %s - Status: %s | Uptime: %d | Technician: %s",
                name, status, uptime, technicianName);
    }
}
