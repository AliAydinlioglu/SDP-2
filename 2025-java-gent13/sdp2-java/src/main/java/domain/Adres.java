package domain;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.*;


@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter

public class Adres implements Serializable {

	private static final long serialVersionUID = 1L;

	private String straat;
	private String huis_nr;
	private String postcode;
	private String stad;
	private String land;


	@Override
	public String toString() {
		return "%s %s, %s %s %s".formatted(straat, huis_nr, postcode, stad, land);
	}

	public static class Builder {
		private String straat;
		private String huis_nr;
		private String postcode;
		private String stad;
		private String land;

		public Builder straat(String straat) {
			if (straat == null || straat.isBlank())
				throw new IllegalArgumentException("Straat mag niet leeg zijn");
			this.straat = straat;
			return this;
		}

		public Builder huis_nr(String huis_nr) {
			if (huis_nr == null || huis_nr.isBlank())
				throw new IllegalArgumentException("Huisnummer mag niet leeg zijn");
			this.huis_nr = huis_nr;
			return this;
		}

		public Builder postcode(String postcode) {
			if (postcode == null || postcode.isBlank())
				throw new IllegalArgumentException("Postcode mag niet leeg zijn");
			this.postcode = postcode;
			return this;
		}

		public Builder stad(String stad) {
			if (stad == null || stad.isBlank())
				throw new IllegalArgumentException("Stad mag niet leeg zijn");
			this.stad = stad;
			return this;
		}

		public Builder land(String land) {
			if (land == null || land.isBlank())
				throw new IllegalArgumentException("Land mag niet leeg zijn");
			this.land = land;
			return this;
		}

		public Adres build() {
			return new Adres(straat, huis_nr, postcode, stad, land);
		}
	}

	public static Builder builder() {
		return new Builder();
	}
}