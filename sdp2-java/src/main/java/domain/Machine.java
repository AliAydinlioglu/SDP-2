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
    private int machineId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    private String code;
    private String locatie;

    @Enumerated(EnumType.STRING)
    private MachineStatus status;

    @Enumerated(EnumType.STRING)
    private ProductieStatus productieStatus;


    public Machine(Site site, String code, String locatie, MachineStatus status, ProductieStatus productieStatus) {
        this.site = site;
        this.code = code;
        this.locatie = locatie;
        this.status = status;
        this.productieStatus = productieStatus;
    }

    @Override
    public String toString() {
        return String.format("Machine[id=%d, code='%s', locatie='%s', status=%s, prodStatus=%s]",
                machineId, code, locatie, status, productieStatus);
    }
}