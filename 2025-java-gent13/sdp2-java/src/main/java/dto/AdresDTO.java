package dto;

public record AdresDTO(String straat, String huis_nr, String postcode, String stad, String land) {
	public static AdresDTO fromEntity(domain.Adres adres) {
		return new AdresDTO(adres.getStraat(), adres.getHuis_nr(), adres.getPostcode(), adres.getStad(), adres.getLand());
	}
	
	@Override
	public String toString() {
		return String.format("%s %s, %s %s, %s", straat, huis_nr, postcode, stad, land);
	}

}
