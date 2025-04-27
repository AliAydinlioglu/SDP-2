package domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import enums.OnderhoudStatus;

public class OnderhoudBuilder {

	private LocalDate datum;
    private LocalTime startTijd;
    private LocalTime eindTijd;
    private Gebruiker technieker;
    private String reden;
    private String rapport;
    private String opmerkingen;
    private OnderhoudStatus status;
    private int machineId;
    
    
    public OnderhoudBuilder datum(LocalDate datum) {
		if (datum == null) {
		   throw new IllegalArgumentException("Datum mag niet null zijn.");
		}
		this.datum = datum;
		return this;
	}
    
    public OnderhoudBuilder startTijd(LocalTime startTijd) {
		if (startTijd == null) {
		   throw new IllegalArgumentException("Starttijd mag niet null zijn.");
		}
		this.startTijd = startTijd;
		return this;
	}
	
	public OnderhoudBuilder eindTijd(LocalTime eindTijd) {
		if (eindTijd == null) {
		   throw new IllegalArgumentException("Eindtijd mag niet null zijn.");
		}
		this.eindTijd = eindTijd;
		return this;
	}
	
	public OnderhoudBuilder technieker(Gebruiker technieker) {
		if (technieker == null) {
		   throw new IllegalArgumentException("Technieker mag niet null zijn.");
		}
		this.technieker = technieker;
		return this;
	}
	
	public OnderhoudBuilder reden(String reden) {
		if (reden == null || reden.isBlank()) {
		   throw new IllegalArgumentException("Reden mag niet null, leeg of alleen spaties bevatten.");
		}
		this.reden = reden;
		return this;
	}
	
	public OnderhoudBuilder rapport(String rapport) {
		if (rapport == null || rapport.isBlank()) {
		   throw new IllegalArgumentException("Rapport mag niet null, leeg of alleen spaties bevatten.");
		}
		this.rapport = rapport;
		return this;
	}
	
	public OnderhoudBuilder opmerkingen(String opmerkingen) {
		this.opmerkingen = opmerkingen;
		return this;
	}
	
	public OnderhoudBuilder status(OnderhoudStatus status) {
		if(status == null) {
			throw new IllegalArgumentException("Status mag niet leeg zijn");
		}
		this.status = status;
		return this;
	}
	
	public OnderhoudBuilder machineId(int machineId) {
		if(new MachineController().getMachine(machineId) == null) {
			throw new IllegalArgumentException("Machine ID mag niet negatief zijn");
		}
		this.machineId = machineId;
		return this;
	}
	
	public Onderhoud build() {
		return new Onderhoud(datum, startTijd, eindTijd, technieker.getGebruikerID(), reden, rapport, opmerkingen, status, machineId);
	}
}
