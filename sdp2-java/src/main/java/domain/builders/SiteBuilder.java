package domain.builders;

import java.util.HashSet;
import java.util.Set;

import domain.Gebruiker;
import domain.Machine;
import domain.Site;


public class SiteBuilder {

	private String naam;
    private Gebruiker verantwoordelijke;
	private Set<Machine> machines = new HashSet<>();

    public SiteBuilder naam(String naam) {
		if (naam == null || naam.isBlank()) {
			throw new IllegalArgumentException("Naam mag niet leeg zijn");
		}
		this.naam = naam;
		return this;
    }
    
    public SiteBuilder verantwoordelijke(Gebruiker verantwoordelijke) {
		this.verantwoordelijke = verantwoordelijke;
		return this;
    }

    
    public SiteBuilder machines(Set<Machine> machines) {
		if (machines == null || machines.isEmpty()) {
			throw new IllegalArgumentException("Machines mag niet leeg zijn");
		}
		this.machines = machines;
		return this;
    }

    public Site build() {
		if (this.naam == null || this.naam.isBlank()){
			throw new IllegalStateException("Naam is niet ingesteld in de builder en is verplicht.");
		}
		if (this.verantwoordelijke == null) {
			throw new IllegalStateException("Verantwoordelijke is niet ingesteld en is verplicht.");
		 }

		Site site = new Site(this.naam);
		if (this.verantwoordelijke != null) {
			site.setVerantwoordelijke(this.verantwoordelijke);
		}
		return site;
	}
}
