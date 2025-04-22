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

}
