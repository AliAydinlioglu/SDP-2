package domain.builders;

import java.time.LocalDate;
import java.time.LocalTime;

import domain.Gebruiker;
import domain.Machine;
import domain.Onderhoud;
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
    private Machine machine;

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
        if (status == null) {
            throw new IllegalArgumentException("Status mag niet leeg zijn");
        }
        this.status = status;
        return this;
    }

    public OnderhoudBuilder machine(Machine machine) { // Changed from machineId to Machine
        if (machine == null) {
            throw new IllegalArgumentException("Machine mag niet null zijn.");
        }
        this.machine = machine;
        return this;
    }

    public Onderhoud build() {
        if (this.datum == null) {
            throw new IllegalStateException("Datum moet ingesteld zijn.");
        }
        if (this.startTijd == null) {
            throw new IllegalStateException("Starttijd moet ingesteld zijn.");
        }
        if (this.eindTijd == null) {
            throw new IllegalStateException("Eindtijd moet ingesteld zijn.");
        }
        if (this.technieker == null) {
            throw new IllegalStateException("Technieker moet ingesteld zijn.");
        }
        if (this.reden == null || this.reden.isBlank()) {
            throw new IllegalStateException("Reden moet ingesteld zijn.");
        }
        if (this.status == null) {
            throw new IllegalStateException("Status moet ingesteld zijn.");
        }
        if (this.machine == null) {
            throw new IllegalStateException("Machine moet ingesteld zijn voordat Onderhoud gebouwd kan worden.");
        }
        return new Onderhoud(datum, startTijd, eindTijd, technieker.getGebruikerID(), reden, rapport, opmerkingen,
                status, machine.getMachineID());
    }
}
