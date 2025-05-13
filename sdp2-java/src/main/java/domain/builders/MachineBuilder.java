package domain.builders;

import java.time.LocalDate;

import domain.Gebruiker;
import domain.Machine;
import domain.Site;
import enums.MachineStatus;
import enums.ProductionStatus;

public class MachineBuilder {
	
	private String naam;
    private String productInfo;
    private String locatie;
    private MachineStatus status;
    private ProductionStatus productieStatus;
    private int uptime;
    private int dagenSindsOnderhoud;
    private LocalDate volgendOnderhoud;
    private Gebruiker technieker;
    private Site site;
	
	public MachineBuilder naam(String naam) {
		if (naam == null || naam.isBlank()) {
			throw new IllegalArgumentException("Naam mag niet leeg zijn");
		}
		this.naam = naam;
		return this;
	}
	
	public MachineBuilder productInfo(String productInfo) {
		if (productInfo == null || productInfo.isBlank()) {
			throw new IllegalArgumentException("Productinformatie mag niet leeg zijn");
		}
		this.productInfo = productInfo;
		return this;
	}
	
	public MachineBuilder locatie(String locatie) {
		if (locatie == null || locatie.isBlank()) {
			throw new IllegalArgumentException("Locatie mag niet leeg zijn");
		}
		this.locatie = locatie;
		return this;
	}
	
	public MachineBuilder status(MachineStatus status) {
		if (status == null) {
			throw new IllegalArgumentException("Status mag niet leeg zijn");
		}
		this.status = status;
		return this;
	}
	
	public MachineBuilder productieStatus(ProductionStatus productieStatus) {
		if (productieStatus == null) {
			throw new IllegalArgumentException("Productiestatus mag niet leeg zijn");
		}
		this.productieStatus = productieStatus;
		return this;
	}
	
	public MachineBuilder uptime(int uptime) {
		if (uptime < 0) {
			throw new IllegalArgumentException("Uptime mag niet negatief zijn");
		}
		this.uptime = uptime;
		return this;
	}
	
	public MachineBuilder dagenSindsOnderhoud(int dagenSindsOnderhoud) {
		if (dagenSindsOnderhoud < 0) {
			throw new IllegalArgumentException("Dagen sinds onderhoud mag niet negatief zijn");
		}
		this.dagenSindsOnderhoud = dagenSindsOnderhoud;
		return this;
	}
	
	public MachineBuilder volgendOnderhoud(LocalDate volgendOnderhoud) {
		if (volgendOnderhoud == null) {
			throw new IllegalArgumentException("Volgend onderhoud mag niet leeg zijn");
		}
		this.volgendOnderhoud = volgendOnderhoud;
		return this;
	}
	
	public MachineBuilder technieker(Gebruiker technieker) {
		if (technieker == null) {
			throw new IllegalArgumentException("Technieker mag niet leeg zijn");
		}
		this.technieker = technieker;
		return this;
	}
	
	public MachineBuilder site(Site site) {
		if (site == null) {
			throw new IllegalArgumentException("Site mag niet leeg zijn");
		}
		this.site = site;
		return this;
	}
	
	public Machine build() {
		return new Machine(naam, productInfo, locatie, status, productieStatus, uptime, technieker, dagenSindsOnderhoud, volgendOnderhoud, site);
	}

}
