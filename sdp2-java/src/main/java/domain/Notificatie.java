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

    private Notificatie(Builder builder) {
        this.titel = builder.titel;
        this.message = builder.message;
        this.ontvanger = builder.ontvanger;
        this.itemType = builder.itemType;
        this.itemId = builder.itemId;
        this.timestamp = (builder.timestamp != null) ? builder.timestamp : LocalDateTime.now();
        this.status = (builder.status != null) ? builder.status : NotificatieStatus.NIEUW;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String titel;
        private String message;
        private Gebruiker ontvanger;
        private String itemType;
        private int itemId;
        private LocalDateTime timestamp;
        private NotificatieStatus status;

        public Builder titel(String titel) {
            if (titel == null || titel.isBlank()) {
                throw new IllegalArgumentException("Titel mag niet leeg zijn");
            }
            this.titel = titel;
            return this;
        }

        public Builder message(String message) {
            if (message == null || message.isBlank()) {
                throw new IllegalArgumentException("Message mag niet leeg zijn");
            }
            this.message = message;
            return this;
        }

        public Builder ontvanger(Gebruiker ontvanger) {
            if (ontvanger == null) {
                throw new IllegalArgumentException("Ontvanger mag niet null zijn");
            }
            this.ontvanger = ontvanger;
            return this;
        }

        public Builder itemType(String itemType) {
            if (itemType == null || itemType.isBlank()) {
                throw new IllegalArgumentException("ItemType mag niet leeg zijn");
            }
            this.itemType = itemType;
            return this;
        }

        public Builder itemId(int itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder status(NotificatieStatus status) {
            this.status = status;
            return this;
        }

        public Notificatie build() {
            if (this.titel == null || this.message == null || this.ontvanger == null || this.itemType == null) {
                throw new IllegalStateException("Titel, message, ontvanger, and itemType zijn verplicht");
            }
            return new Notificatie(this);
        }
    }
}
