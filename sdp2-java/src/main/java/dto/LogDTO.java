package dto;

import java.time.LocalDateTime;

public record LogDTO(int id, GebruikerDTO gebruiker, LocalDateTime date, String actie, String opmerking) {
	public static LogDTO fromEntity(domain.Log l) {
		return new LogDTO(l.getId(), GebruikerDTO.fromEntity(l.getGebruiker()), l.getDate(), l.getActie(), l.getOpmerking());
	}

}
