package domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import dto.AdresDTO;
import dto.GebruikerDTO;
import enums.Rol;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import repository.GebruikerDao;

@ExtendWith(MockitoExtension.class)
public class GebruikerControllerTest {

    @Mock
    private GebruikerDao gebruikerRepo;

    @InjectMocks
    private GebruikerController dc;
    
    private static Stream<Arguments> invalidAddGebruikerParameters() {
        return Stream.of(
                Arguments.of(null, "voornaam", LocalDate.now(), "straat", "1", "1000", "stad", "land", "email@test.com", "gsm", Rol.GEBRUIKER, true),
                Arguments.of("achternaam", null, LocalDate.now(), "straat", "1", "1000", "stad", "land", "email@test.com", "gsm", Rol.GEBRUIKER, true),
                Arguments.of("achternaam", "voornaam", null, "straat", "1", "1000", "stad", "land", "email@test.com", "gsm", Rol.GEBRUIKER, true),
                Arguments.of("achternaam", "voornaam", LocalDate.now(), null, "1", "1000", "stad", "land", "email@test.com", "gsm", Rol.GEBRUIKER, true),
                Arguments.of("achternaam", "voornaam", LocalDate.now(), "straat", null, "1000", "stad", "land", "email@test.com", "gsm", Rol.GEBRUIKER, true),
                Arguments.of("achternaam", "voornaam", LocalDate.now(), "straat", "1", null, "stad", "land", "email@test.com", "gsm", Rol.GEBRUIKER, true),
                Arguments.of("achternaam", "voornaam", LocalDate.now(), "straat", "1", "1000", null, "land", "email@test.com", "gsm", Rol.GEBRUIKER, true),
                Arguments.of("achternaam", "voornaam", LocalDate.now(), "straat", "1", "1000", "stad", null, "email@test.com", "gsm", Rol.GEBRUIKER, true),
                Arguments.of("achternaam", "voornaam", LocalDate.now(), "straat", "1", "1000", "stad", "land", null, "gsm", Rol.GEBRUIKER, true),
                Arguments.of("achternaam", "voornaam", LocalDate.now(), "straat", "1", "1000", "stad", "land", "email@test.com", null, Rol.GEBRUIKER, true),
                Arguments.of("achternaam", "voornaam", LocalDate.now(), "straat", "1", "1000", "stad", "land", "email@test.com", "gsm", null, true)
        );
    }
    
    private static Stream<Arguments> invalidUpdateGebruikerParameters() {
        Adres validAdres = VALID_GEBRUIKER.getAdres();

        return Stream.of(
                Arguments.of(new GebruikerDTO(VALID_GEBRUIKER.getGebruikerID(), null, "Achternaam", LocalDate.now(), AdresDTO.fromEntity(validAdres), "email@test.com", "gsm", Rol.GEBRUIKER, true)),
                Arguments.of(new GebruikerDTO(VALID_GEBRUIKER.getGebruikerID(), "Voornaam", null, LocalDate.now(), AdresDTO.fromEntity(validAdres), "email@test.com", "gsm", Rol.GEBRUIKER, true)),
                Arguments.of(new GebruikerDTO(VALID_GEBRUIKER.getGebruikerID(), "Voornaam", "Achternaam", null, AdresDTO.fromEntity(validAdres), "email@test.com", "gsm", Rol.GEBRUIKER, true)),
                Arguments.of(new GebruikerDTO(VALID_GEBRUIKER.getGebruikerID(), "Voornaam", "Achternaam", LocalDate.now(), AdresDTO.fromEntity(validAdres), null, "gsm", Rol.GEBRUIKER, true)),
                Arguments.of(new GebruikerDTO(VALID_GEBRUIKER.getGebruikerID(), "Voornaam", "Achternaam", LocalDate.now(), AdresDTO.fromEntity(validAdres), "email@test.com", null, Rol.GEBRUIKER, true)),
                Arguments.of(new GebruikerDTO(VALID_GEBRUIKER.getGebruikerID(), "Voornaam", "Achternaam", LocalDate.now(), AdresDTO.fromEntity(validAdres), "email@test.com", "gsm", null, true))
        );
    }
    
    private static Stream<Arguments> invalidLoginParameters() {
        Gebruiker valid = Gebruiker.builder()
                .voornaam("Test")
                .achternaam("User")
                .geboorteDatum(LocalDate.of(199, 1, 1))
                .adres(new Adres("Straat", "1", "1000", "Stad", "Land"))
                .email("user@test.com")
                .rol(Rol.MANAGER)
                .gsm("123456789")
                .actief(true)
                .build();

        return Stream.of(
                Arguments.of(null, "somepass", null),
                Arguments.of("email@test.com", null, null),
                Arguments.of("notfound@test.com", "wrong", null),
                Arguments.of(valid.getEmail(), "wrongpassword", valid)
        );
    }




    private static final Gebruiker VALID_GEBRUIKER = new Gebruiker(
            "testVoornaam", "TestAchternaam", LocalDate.of(1970, 12, 12),
            new Adres("testStraat", "TestHuisnr", "TestPostcode", "TesteStad", "TestLand"),
            "test@test.com", "password", Rol.ADMINISTRATOR, true);

    @Test
    public void findAllTest() {
        when(gebruikerRepo.findAll()).thenReturn(Arrays.asList(VALID_GEBRUIKER));

        GebruikerDTO gebruikerDTO = GebruikerDTO.fromEntity(VALID_GEBRUIKER);

        assertEquals(Arrays.asList(gebruikerDTO), dc.findAll());
        verify(gebruikerRepo).findAll();
    }

    @Test
    public void findByIdTest() {
        when(gebruikerRepo.get(1)).thenReturn(VALID_GEBRUIKER);

        GebruikerDTO gebruikerDTO = GebruikerDTO.fromEntity(VALID_GEBRUIKER);

        assertEquals(gebruikerDTO, dc.getGebruiker(1));
        verify(gebruikerRepo).get(1);
    }

    @Test
    public void addGebruikerTest_Success() {
        when(gebruikerRepo.findAll()).thenReturn(new ArrayList<>(List.of(VALID_GEBRUIKER)));

        dc.findAll();

        dc.addGebruiker("TestAchternaam", "testVoornaam", LocalDate.of(1970, 12, 12),
                "testStraat", "TestHuisnr", "TestPostcode", "TesteStad", "TestLand",
                "newuser@test.com", "123456", Rol.GEBRUIKER, true);

        verify(gebruikerRepo).findAll();
        verify(gebruikerRepo).startTransaction();
        verify(gebruikerRepo).insert(any(Gebruiker.class));
        verify(gebruikerRepo).commitTransaction();
    }

    @ParameterizedTest
    @MethodSource("invalidAddGebruikerParameters")
    public void addGebruikerTest_InvalidInputs_ThrowsException(
            String achternaam, String voornaam, LocalDate geboortedatum,
            String straat, String huisNr, String postcode, String stad, String land,
            String email, String gsm, Rol rol, boolean actief
    ) {
        when(gebruikerRepo.findAll()).thenReturn(new ArrayList<>(List.of(VALID_GEBRUIKER)));
        dc.findAll(); 

        assertThrows(IllegalArgumentException.class, () -> {
            dc.addGebruiker(achternaam, voornaam, geboortedatum, straat, huisNr,
                    postcode, stad, land, email, gsm, rol, actief);
        });

        verify(gebruikerRepo).findAll();
        verify(gebruikerRepo).rollbackTransaction();
    }

    @Test
    public void updateGebruikerTest_Success() {
        when(gebruikerRepo.findAll()).thenReturn(Arrays.asList(VALID_GEBRUIKER));
        dc.findAll();

        GebruikerDTO updatedDTO = GebruikerDTO.fromEntity(VALID_GEBRUIKER);
        updatedDTO = new GebruikerDTO(
                updatedDTO.id(), "NewVoornaam", updatedDTO.achternaam(), updatedDTO.geboortedatum(),
                updatedDTO.adres(), updatedDTO.email(), updatedDTO.gsm(), updatedDTO.rol(), updatedDTO.actief()
        );

        dc.updateGebruiker(updatedDTO);

        verify(gebruikerRepo).findAll();
        verify(gebruikerRepo).update(any(Gebruiker.class));
    }

    @ParameterizedTest
    @MethodSource("invalidUpdateGebruikerParameters")
    public void updateGebruikerTest_InvalidInputs_ThrowsException(GebruikerDTO invalidDTO) {
        when(gebruikerRepo.findAll()).thenReturn(List.of(VALID_GEBRUIKER));
        dc.findAll(); 

        assertThrows(IllegalArgumentException.class, () -> {
            dc.updateGebruiker(invalidDTO);
        });
        
        verify(gebruikerRepo).findAll();
    }


    @Test
    public void loginTest_Success() {
        when(gebruikerRepo.getGebruikerByEmail("test@test.com")).thenReturn(VALID_GEBRUIKER);
        Gebruiker spyGebruiker = spy(VALID_GEBRUIKER);
        when(spyGebruiker.checkWachtwoord("test@test.com", "password")).thenReturn(true);
        when(gebruikerRepo.getGebruikerByEmail("test@test.com")).thenReturn(spyGebruiker);

        GebruikerDTO result = dc.login("test@test.com", "password");

        assertEquals("Voornaam", result.voornaam());
        verify(gebruikerRepo).getGebruikerByEmail("test@test.com");
    }

    @ParameterizedTest
    @MethodSource("invalidLoginParameters")
    public void loginTest_InvalidInputs_ThrowsException(String email, String password, Gebruiker mockReturn) {
        when(gebruikerRepo.getGebruikerByEmail(email)).thenReturn(mockReturn);

        assertThrows(IllegalArgumentException.class, () -> dc.login(email, password));
    }



}
