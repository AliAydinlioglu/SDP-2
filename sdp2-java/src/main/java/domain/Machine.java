package domain;

import jakarta.persistence.*;
import lombok.*;
import enums.MachineStatus;
import enums.ProductieStatus;

@Entity
@Table(name = "machines")
@Getter @Setter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(exclude = {"machineId", "site"})
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int machineId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    private String locatie;

    @Enumerated(EnumType.STRING)
    private MachineStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "prod_status")
    private ProductieStatus productieStatus;

    public Machine(Site site, String locatie, MachineStatus status, ProductieStatus productieStatus) {
        this.site = site;
        this.locatie = locatie;
        this.status = status;
        this.productieStatus = productieStatus;
    }

    @Override
    public String toString() {
        return String.format("Machine[id=%d, locatie='%s', status=%s, prodStatus=%s]",
                machineId, locatie, status, productieStatus);
    }
}