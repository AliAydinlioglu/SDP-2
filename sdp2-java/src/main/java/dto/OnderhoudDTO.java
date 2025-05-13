package dto;

import java.time.LocalDate;
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
    MachineDTO machine,
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
            MachineDTO.fromEntity(onderhoud.getMachine()),
            GebruikerDTO.fromEntity(onderhoud.getTechnieker())
        );
    }
    
    @Override
    public String toString() {
        return "OnderhoudDTO{" +
               "id=" + id +
               ", datum=" + datum +
               ", startTijd=" + startTijd +
               ", eindTijd=" + eindTijd +
               ", reden='" + reden + '\'' +
               ", rapport='" + rapport + '\'' +
               ", opmerkingen='" + opmerkingen + '\'' +
               ", status=" + status +
               ", machine=" + machine +
               ", technieker=" + technieker +
               '}';
    }
}
