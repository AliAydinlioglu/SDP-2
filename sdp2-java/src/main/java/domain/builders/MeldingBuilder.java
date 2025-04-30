package domain.builders;

import java.time.LocalDate;

import domain.Gebruiker;
import domain.Melding;

public class MeldingBuilder {

	private String beschrijving;
	private Gebruiker gebruiker;
	private String status;
	private String type;
	private LocalDate datum;
	
	public MeldingBuilder beschrijving(String beschrijving) {
		if (beschrijving == null || beschrijving.isBlank()) {
			throw new IllegalArgumentException("Beschrijving mag niet leeg zijn");
		}
		this.beschrijving = beschrijving;
		return this;
	}
	
	public MeldingBuilder gebruiker(Gebruiker gebruiker) {
		if (gebruiker == null) {
			throw new IllegalArgumentException("Gebruiker mag niet leeg zijn");
		}
		this.gebruiker = gebruiker;
		return this;
	}
	
	public MeldingBuilder status(String status) {
		if (status == null || status.isBlank()) {
			throw new IllegalArgumentException("Status mag niet leeg zijn");
		}
		this.status = status;
		return this;
	}
	
	public MeldingBuilder type(String type) {
		if (type == null || type.isBlank()) {
			throw new IllegalArgumentException("Type mag niet leeg zijn");
		}
		this.type = type;
		return this;
	}
	
	public MeldingBuilder datum(LocalDate datum) {
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
