package domain;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "meldingen")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(exclude = {"meldingId"})
@Getter
@Setter
public class Melding implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	@Column(name = "id")
	private int meldingId;
	
	private String beschrijving;
	
	@Column(name = "user_id")
	@ManyToOne
	private Gebruiker gebruiker;
	
	private String status;
	private String type;
	private LocalDate datum;
	
	public Melding(String beschrijving, Gebruiker gebruiker, String status, String type, LocalDate datum) {
		setBeschrijving(beschrijving);
		setGebruiker(gebruiker);
		setStatus(status);
		setType(type);
		setDatum(datum);
		
	}
	public static class Builder {
		private String beschrijving;
		private Gebruiker gebruiker;
		private String status;
		private String type;
		private LocalDate datum;

		public Builder beschrijving(String beschrijving) {
			if (beschrijving == null || beschrijving.isBlank()) {
				throw new IllegalArgumentException("Beschrijving mag niet leeg zijn");
			}
			this.beschrijving = beschrijving;
			return this;
		}

		public Builder gebruiker(Gebruiker gebruiker) {
			if (gebruiker == null) {
				throw new IllegalArgumentException("Gebruiker mag niet leeg zijn");
			}
			this.gebruiker = gebruiker;
			return this;
		}

		public Builder status(String status) {
			if (status == null || status.isBlank()) {
				throw new IllegalArgumentException("Status mag niet leeg zijn");
			}
			this.status = status;
			return this;
		}

		public Builder type(String type) {
			if (type == null || type.isBlank()) {
				throw new IllegalArgumentException("Type mag niet leeg zijn");
			}
			this.type = type;
			return this;
		}

		public Builder datum(LocalDate datum) {
			if (datum == null) {
				throw new IllegalArgumentException("Datum mag niet leeg zijn");
			}
			this.datum = datum;
			return this;
		}

		public Melding build() {
			return new Melding(beschrijving, gebruiker, status, type, datum);
		}
	}

	public static Builder builder() {
		return new Builder();
	}
}



