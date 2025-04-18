package dto;

import java.time.LocalDate;

import domain.Adres;
import enums.Rol;

public record GebruikerDTO(int id, String voornaam, String achternaam, LocalDate geboortedatum, Adres adres, String email, String gsm, Rol rol, boolean actief) {

}
