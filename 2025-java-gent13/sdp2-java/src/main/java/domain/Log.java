package domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@Table(name = "logs")
public class Log implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "gebruiker_id")
    private Gebruiker gebruiker;

    private LocalDateTime date;

    private String actie;

    private String opmerking;

    protected Log() {
    }

    private Log(Gebruiker gebruiker, String actie, String opmerking, LocalDateTime date) {
        this.gebruiker = gebruiker;
        this.actie = actie;
        this.opmerking = opmerking;
        this.date = date;
    }

    public static class Builder {
        private Gebruiker gebruiker;
        private String actie;
        private String opmerking;

        public Builder gebruiker(Gebruiker gebruiker) {
            if (gebruiker == null) {
                throw new IllegalArgumentException("Gebruiker mag niet leeg zijn");
            }
            this.gebruiker = gebruiker;
            return this;
        }

        public Builder actie(String actie) {
            if (actie == null || actie.isBlank()) {
                throw new IllegalArgumentException("Actie mag niet leeg zijn");
            }
            this.actie = actie;
            return this;
        }

        public Builder opmerking(String opmerking) {
            this.opmerking = opmerking;
            return this;
        }

        public Log build() {
            if (gebruiker == null) {
                throw new IllegalStateException("Gebruiker is verplicht");
            }
            if (actie == null || actie.isBlank()) {
                throw new IllegalStateException("Actie is verplicht");
            }
            return new Log(gebruiker, actie, opmerking, LocalDateTime.now());
        }
    }

    public static Builder builder() {
        return new Builder();
    }

}
