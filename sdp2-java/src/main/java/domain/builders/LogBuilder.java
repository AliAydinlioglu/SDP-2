package domain.builders;

import domain.Gebruiker;
import domain.Log;

public class LogBuilder {
	
	private Gebruiker gebruiker;
	private String actie;
	private String opmerking;
	//private LocalDateTime date;
	
	public LogBuilder gebruiker(Gebruiker gebruiker) {
		if(gebruiker == null) {
			throw new IllegalArgumentException("Gebruiker mag niet leeg zijn");
		}
		this.gebruiker = gebruiker;
		return this;
	}
	
	public LogBuilder actie(String actie) {
		if(actie == null || actie.isBlank()) {
			throw new IllegalArgumentException("Actie mag niet leeg zijn");
		}
		this.actie = actie;
		return this;
	}
	
	public LogBuilder opmerking(String opmerking) {
		this.opmerking = opmerking;
		return this;
	}
	
//	public LogBuilder date(LocalDateTime date) {
//		if(date == null) {
//			throw new IllegalArgumentException("Datum mag niet leeg zijn");
//		}
//		this.date = date;
//		return this;
//	}
	
	public Log build() {
		return new Log(this.gebruiker, this.actie, this.opmerking);
	}

}
