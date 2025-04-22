package dto;

import java.time.LocalDateTime;

import domain.Onderhoud;
import enums.OnderhoudStatus;

public record OnderhoudDTO(
    int id,
    LocalDateTime datum,
    LocalDateTime startTijd,
    LocalDateTime eindTijd,
    String reden,
    String rapport,
    String opmerkingen,
    OnderhoudStatus status,
    int machineId,
    GebruikerDTO technieker
) {
    public static OnderhoudDTO fromEntity(Onderhoud onderhoud) {
        return new OnderhoudDTO(
            onderhoud.getOnderhoudId(),
            onderhoud.getDatum(),
            onderhoud.getStartTijd(),
            onderhoud.getEindTijd(),
            onderhoud.getReden(),
            onderhoud.getRapport(),
            onderhoud.getOpmerkingen(),
            onderhoud.getStatus(),
            onderhoud.getMachineId(),
            onderhoud.getTechnieker() != null ? GebruikerDTO.fromEntity(onderhoud.getTechnieker()) : null
        );
    }
}
