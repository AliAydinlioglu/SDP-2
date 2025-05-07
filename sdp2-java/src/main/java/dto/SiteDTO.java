package dto;

import java.util.Set;
import java.util.stream.Collectors;

import domain.Site;
import domain.Gebruiker;

public record SiteDTO(int id, String naam, GebruikerDTO verantwoordelijke, Set<MachineDTO> machines) {
    public static SiteDTO fromEntity(Site site) {
        Gebruiker verantwEntity = site.getVerantwoordelijke();
        return new SiteDTO(
            site.getSiteId(),
            site.getNaam(),
                (verantwEntity != null) ? GebruikerDTO.fromEntity(site.getVerantwoordelijke()) : null,
            site.getMachines().stream()
                .map(MachineDTO::fromEntity)
                .collect(Collectors.toSet())
        );
    }
    
    public static record SiteSummaryDTO(int id, String naam) {
        public static SiteSummaryDTO fromEntity(Site site) {
            return new SiteSummaryDTO(site.getSiteId(), site.getNaam());
        }
    }
}