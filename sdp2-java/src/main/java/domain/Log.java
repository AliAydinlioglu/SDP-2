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
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@Table(name= "logs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
	
	public Log(Gebruiker gebruiker, String actie, String opmerking) {
		setGebruiker(gebruiker);
		setDate(LocalDateTime.now());
		setActie(actie);
		setOpmerking(opmerking);
	}
	
	

}
