package domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;

import enums.OnderhoudStatus;
import enums.Rol;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "onderhouden")
@NamedQueries({
	@NamedQuery(name = "Onderhoud.findByTechniekerId",
            	query = "SELECT o FROM Onderhoud o WHERE o.technieker.gebruikerID = :techniekerId"),
    @NamedQuery(name = "Onderhoud.findByMachineId",
                query = "SELECT o FROM Onderhoud o WHERE o.machine.machineID = :machineId"),
    @NamedQuery(name = "Onderhoud.findVoltooideLaatste3Maanden",
    			query = "SELECT o FROM Onderhoud o WHERE o.status = :status AND o.datum >= :date"),
    @NamedQuery(name = "Onderhoud.findLaatsteVoltooidePerMachine",
    			query = "SELECT o FROM Onderhoud o WHERE o.status = enums.OnderhoudStatus.VOLTOOID " +
            			"AND o.datum = (SELECT MAX(o2.datum) FROM Onderhoud o2 WHERE o2.machine.machineID = o.machine.machineID)")
    
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(exclude = {"onderhoudId", "machineId"})
public class Onderhoud implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id")
    private int onderhoudId;

    @Column(name = "datum")
    private LocalDate datum;

    @Column(name = "starttijd")
    private LocalTime startTijd;

    @Column(name = "eindtijd")
    private LocalTime eindTijd;

    @ManyToOne
    @JoinColumn(name = "technieker_id", nullable = false)
    private Gebruiker technieker;

    @Column(name = "reden")
    private String reden;

    @Column(name = "rapport")
    private String rapport;

    @Column(name = "opmerkingen")
    private String opmerkingen;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OnderhoudStatus status;
    
    @ManyToOne
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine machine;
    
   

    public Onderhoud(LocalDate datum, LocalTime startTijd, LocalTime eindTijd,
            int techniekerId, String reden, String rapport, String opmerkingen,
            OnderhoudStatus status, int machineId) {
    	
		if (datum == null) {
		   throw new IllegalArgumentException("Datum mag niet null zijn.");
		}
		if (startTijd == null) {
		   throw new IllegalArgumentException("Starttijd mag niet null zijn.");
		}
		if (eindTijd == null) {
		   throw new IllegalArgumentException("Eindtijd mag niet null zijn.");
		}
		if (reden == null || reden.isBlank()) {
		   throw new IllegalArgumentException("Reden mag niet null, leeg of alleen spaties bevatten.");
		}
		if (rapport == null || rapport.isBlank()) {
		   throw new IllegalArgumentException("Rapport mag niet null, leeg of alleen spaties bevatten.");
		}
		if (status == null) {
		   throw new IllegalArgumentException("Status mag niet null zijn.");
		}
		if (status == OnderhoudStatus.INGEPLAND) {
		   throw new IllegalArgumentException("Techniekers mogen geen 'ingepland' als status instellen.");
		}
		
		setDatum(datum);
	    setStartTijd(startTijd);
	    setEindTijd(eindTijd);
	    setTechnieker(techniekerId);
	    setReden(reden);
	    setRapport(rapport);
	    setOpmerkingen(opmerkingen);
	    setStatus(status);
	    setMachine(machineId);
	   
	}
    
    public void setTechnieker(int techniekerId) {
        GebruikerController gebruikerController = new GebruikerController();
        Gebruiker technieker = gebruikerController.getRealGebruiker(techniekerId);

        if (technieker == null) {
            throw new IllegalArgumentException("Technieker met ID " + techniekerId + " bestaat niet.");
        }

        if (technieker.getRol() != Rol.TECHNIEKER) {
            throw new IllegalArgumentException("Gebruiker met ID " + techniekerId + " is geen technieker.");
        }

        this.technieker = technieker;
    }
    
    public void setMachine(int machineId) {
		MachineController machineController = new MachineController();
		Machine machine = machineController.getRealMachine(machineId);

		if (machine == null) {
			throw new IllegalArgumentException("Machine met ID " + machineId + " bestaat niet.");
		}

		this.machine = machine;
	}

    @Override
    public String toString() {
        return String.format("Onderhoud op %s (%s - %s) door technieker %s | Status: %s",
                datum, startTijd, eindTijd,
                technieker != null ? technieker.getVoornaam() + " " + technieker.getAchternaam() : "Onbekend", status);
    }
}
