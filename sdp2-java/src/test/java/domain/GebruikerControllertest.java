package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import domain.Adres;
import domain.Gebruiker;
import domain.GebruikerController;
import enums.Rol;
import repository.GebruikerDoa;

@ExtendWith(MockitoExtension.class)
public class GebruikerControllertest {


    @Mock
    private GebruikerDoa gebruikerRepo;

    @InjectMocks
    private GebruikerController dc;

    @Test
    public void addGebruiker() {
        final String VOORNAAM = "Jan", ACHTERNAAM = "Baard", EMAIL = "jan@bertje.be", GSM = "920387509";
        final Adres ADRES = new Adres("kniestraat", "12", "Gent", "Belgie", "9000");
        final Rol ROL = Rol.MANAGER;
        final LocalDate GEBOORTEDATUM = LocalDate.of(1990, 1, 1);

        Gebruiker gebruiker = new Gebruiker(VOORNAAM, ACHTERNAAM, GEBOORTEDATUM, ADRES, EMAIL, GSM, ROL, true);

        when(gebruikerRepo.findAll()).thenReturn(Arrays.asList(gebruiker));
        when(gebruikerRepo.getGebruikerByEmail(EMAIL)).thenReturn(gebruiker);

        // construct AFTER stubbing


        // assert
        assertEquals(gebruiker, dc.getGebruikerByEmail(EMAIL));
        assertTrue(dc.findAll().contains(gebruiker));

        // verify
        verify(gebruikerRepo).findAll();
        verify(gebruikerRepo).getGebruikerByEmail(EMAIL);

    }

}
