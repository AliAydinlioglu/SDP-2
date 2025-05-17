package domain;

import java.time.LocalDateTime;

import enums.NotificatieStatus;
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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notificaties")
@Getter
@Setter
@NoArgsConstructor
public class Notificatie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String titel;
    private String message;
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ontvanger_id", nullable = false)
    private Gebruiker ontvanger;

    @Enumerated(EnumType.STRING)
    private NotificatieStatus status;

    @Column(name = "item_type")
    private String itemType;

    @Column(name = "item_id")
    private int itemId;

    public Notificatie(String titel, String message, Gebruiker ontvanger, String itemType, int itemId) {
        this.titel = titel;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.ontvanger = ontvanger;
        this.status = NotificatieStatus.NIEUW;
        this.itemType = itemType;
        this.itemId = itemId;
    }
}
