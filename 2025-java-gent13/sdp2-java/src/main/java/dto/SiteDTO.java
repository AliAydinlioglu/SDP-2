package dto;

import java.util.Set;
import java.util.stream.Collectors;

import domain.Site;

public record SiteDTO(int id, String naam, GebruikerDTO verantwoordelijke, Set<MachineDTO> machines) {
    public static SiteDTO fromEntity(Site site) {
        if (site == null) {
            return null;
            }
        GebruikerDTO verantwDto = (site.getVerantwoordelijke() != null) ?
                GebruikerDTO.fromEntity(site.getVerantwoordelijke()) : null;

        Set<MachineDTO> machineDtos = (site.getMachines() != null) ?
                site.getMachines().stream()
                        .map(MachineDTO::fromEntity)
                        .collect(Collectors.toSet())
                : Set.of();

        return new SiteDTO(
                site.getSiteId(),
                site.getNaam(),
                verantwDto,
                machineDtos
        );
    }
    public static record SiteSummaryDTO(int id, String naam) {
        public static SiteSummaryDTO fromEntity(Site site) {
            if (site == null) return null;
            return new SiteSummaryDTO(site.getSiteId(), site.getNaam());
        }

        @Override
        public String toString() {
            return naam();
        }
    }
}