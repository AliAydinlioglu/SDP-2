package dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import domain.Onderhoud;
import enums.OnderhoudStatus;

public record OnderhoudDTO(
    int id,
    LocalDate datum,
    LocalTime startTijd,
    LocalTime eindTijd,
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
            GebruikerDTO.fromEntity(onderhoud.getTechnieker())
        );
    }
}
