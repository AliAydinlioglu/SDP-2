package dto;

import java.time.LocalDate;

import domain.Adres;
import domain.Gebruiker;
import enums.Rol;

public record GebruikerDTO(int id, String voornaam, String achternaam, LocalDate geboortedatum, AdresDTO adres, String email, String gsm, Rol rol, boolean actief) {
	public static GebruikerDTO fromEntity(Gebruiker g) {
        return new GebruikerDTO(
            g.getGebruikerID(), g.getVoornaam(), g.getAchternaam(),
            g.getGeboorteDatum(), AdresDTO.fromEntity(g.getAdres()), g.getEmail(),
            g.getGsm(), g.getRol(), g.getActief()
        );
    }
}
