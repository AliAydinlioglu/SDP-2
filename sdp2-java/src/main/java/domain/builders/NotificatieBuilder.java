package domain.builders;

import domain.Gebruiker;
import domain.Notificatie;
import enums.NotificatieStatus;
import java.time.LocalDateTime;

public class NotificatieBuilder {

    private String titel;
    private String message;
    private Gebruiker ontvanger;
    private String itemType;
    private int itemId;
    private LocalDateTime timestamp;
    private NotificatieStatus status;

    public NotificatieBuilder titel(String titel) {
        if (titel == null || titel.isBlank()) {
            throw new IllegalArgumentException("Titel mag niet leeg zijn");
        }
        this.titel = titel;
        return this;
    }

    public NotificatieBuilder message(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message mag niet leeg zijn");
        }
        this.message = message;
        return this;
    }

    public NotificatieBuilder ontvanger(Gebruiker ontvanger) {
        if (ontvanger == null) {
            throw new IllegalArgumentException("Ontvanger mag niet null zijn");
        }
        this.ontvanger = ontvanger;
        return this;
    }

    public NotificatieBuilder itemType(String itemType) {
        if (itemType == null || itemType.isBlank()) {
            throw new IllegalArgumentException("ItemType mag niet leeg zijn");
        }
        this.itemType = itemType;
        return this;
    }

    public NotificatieBuilder itemId(int itemId) {
        this.itemId = itemId;
        return this;
    }

    public NotificatieBuilder timestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public NotificatieBuilder status(NotificatieStatus status) {
        this.status = status;
        return this;
    }

    public Notificatie build() {
        if (this.titel == null || this.message == null || this.ontvanger == null || this.itemType == null) {
            throw new IllegalStateException("Titel, message, ontvanger, and itemType zijn verplicht");
        }

        Notificatie notificatie = new Notificatie(this.titel, this.message, this.ontvanger, this.itemType, this.itemId);

        if (this.timestamp != null) {
            notificatie.setTimestamp(this.timestamp);
        }
        if (this.status != null) {
            notificatie.setStatus(this.status);
        } else {
            notificatie.setStatus(NotificatieStatus.NIEUW); // Default status
        }
        return notificatie;
    }
}
