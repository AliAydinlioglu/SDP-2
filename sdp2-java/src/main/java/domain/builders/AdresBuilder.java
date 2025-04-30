package domain.builders;

import domain.Adres;

public class AdresBuilder {
	
	private String straat;
	private String huis_nr;
	private String postcode;
	private String stad;
	private String land;
	
	public AdresBuilder straat(String straat) {
		if (straat == null || straat.isBlank()) {
			throw new IllegalArgumentException("Straat mag niet leeg zijn");
		}
		this.straat = straat;
		return this;
	}
	
	public AdresBuilder huis_nr(String huis_nr) {
		if (huis_nr == null || huis_nr.isBlank()) {
			throw new IllegalArgumentException("Huisnummer mag niet leeg zijn");
		}
		this.huis_nr = huis_nr;
		return this;
	}
	
	public AdresBuilder postcode(String postcode) {
		if (postcode == null || postcode.isBlank()) {
			throw new IllegalArgumentException("Postcode mag niet leeg zijn");
		}
		this.postcode = postcode;
		return this;
	}
	
	public AdresBuilder stad(String stad) {
		if (stad == null || stad.isBlank()) {
			throw new IllegalArgumentException("Stad mag niet leeg zijn");
		}
		this.stad = stad;
		return this;
	}
	
	public AdresBuilder land(String land) {
		if (land == null || land.isBlank()) {
			throw new IllegalArgumentException("Land mag niet leeg zijn");
		}
		this.land = land;
		return this;
	}
	
	public Adres build() {
		return new Adres(this.straat, this.huis_nr, this.postcode, this.stad, this.land);
	}

}
