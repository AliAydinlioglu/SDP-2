package domain;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Adres implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String straat;
	private String huis_nr;
	private String stad;
	private String land;
	private String postcode;
	
	@Override
	public String toString() {
		return "%s %s, %s %s %s".formatted(straat, huis_nr, postcode, stad, land);
	}

}
