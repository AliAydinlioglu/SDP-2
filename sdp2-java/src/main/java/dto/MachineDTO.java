package dto;

import domain.Machine;
import dto.SiteDTO.SiteSummaryDTO;
import enums.MachineStatus;
import enums.ProductionStatus;

import java.time.LocalDate;

public record MachineDTO(
        int id,
        String naam,
        String productInfo,
        String locatie,
        MachineStatus status,
        ProductionStatus productieStatus,
        int uptime,
        int dagenSindsOnderhoud,
        LocalDate volgendOnderhoud,
        GebruikerDTO technieker,
        SiteSummaryDTO site
) {
    public static MachineDTO fromEntity(Machine machine) {
        if (machine == null) return null;

        return new MachineDTO(
                machine.getMachineID(),
                machine.getNaam(),
                machine.getProductInfo(),
                machine.getLocatie(),
                machine.getStatus(),
                machine.getProductieStatus(),
                machine.getUptime(),
                machine.getDagenSindsOnderhoud(),
                machine.getVolgendOnderhoud(),
                GebruikerDTO.fromEntity(machine.getTechnieker()),
                SiteDTO.SiteSummaryDTO.fromEntity(machine.getSite())
        );
    }
    
    
}