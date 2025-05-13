package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import enums.OnderhoudStatus;
import enums.Rol;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OnderhoudTest {

    private LocalDate validDatum;
    private LocalTime validStartTijd;
    private LocalTime validEindTijd;
    private int validTechniekerId;
    private String validReden;
    private String validRapport;
    private String validOpmerkingen;
    private OnderhoudStatus validStatus;
    private int validMachineId;

    private Gebruiker mockTechnieker;
    private Machine mockMachine;

    @BeforeEach
    void setUp() {
        validDatum = LocalDate.of(2025, 5, 6);
        validStartTijd = LocalTime.of(9, 0);
        validEindTijd = LocalTime.of(17, 0);
        validTechniekerId = 1;
        validReden = "Routine controle";
        validRapport = "Alles OK";
        validOpmerkingen = "Geen bijzonderheden";
        validStatus = OnderhoudStatus.VOLTOOID;
        validMachineId = 1;

        mockTechnieker = mock(Gebruiker.class);
        when(mockTechnieker.getGebruikerID()).thenReturn(validTechniekerId);
        when(mockTechnieker.getRol()).thenReturn(Rol.TECHNIEKER);
        when(mockTechnieker.getVoornaam()).thenReturn("Jan");
        when(mockTechnieker.getAchternaam()).thenReturn("Technieker");

        mockMachine = mock(Machine.class);
        when(mockMachine.getMachineID()).thenReturn(validMachineId);
    }

    private Onderhoud createOnderhoudWithProtectedConstructor() {
        Onderhoud onderhoud = new Onderhoud();
        onderhoud.setDatum(validDatum);
        onderhoud.setStartTijd(validStartTijd);
        onderhoud.setEindTijd(validEindTijd);
        onderhoud.setReden("Basis Reden");
        onderhoud.setRapport("Basis Rapport");
        onderhoud.setStatus(OnderhoudStatus.IN_UITVOERING);
        return onderhoud;
    }

    @Test
    void constructor_ValidInput_CreatesInstance() {
        try (MockedConstruction<GebruikerController> mockedGc = Mockito.mockConstruction(GebruikerController.class,
                (mock, context) -> when(mock.getRealGebruiker(validTechniekerId)).thenReturn(mockTechnieker));
                MockedConstruction<MachineController> mockedMc = Mockito.mockConstruction(MachineController.class,
                        (mock, context) -> when(mock.getRealMachine(validMachineId)).thenReturn(mockMachine))) {

            Onderhoud onderhoud = new Onderhoud(validDatum, validStartTijd, validEindTijd,
                    validTechniekerId, validReden, validRapport, validOpmerkingen,
                    validStatus, validMachineId);

            assertNotNull(onderhoud);
            assertEquals(validDatum, onderhoud.getDatum());
            assertEquals(validStartTijd, onderhoud.getStartTijd());
            assertEquals(validEindTijd, onderhoud.getEindTijd());
            assertEquals(mockTechnieker, onderhoud.getTechnieker());
            assertEquals(validReden, onderhoud.getReden());
            assertEquals(validRapport, onderhoud.getRapport());
            assertEquals(validOpmerkingen, onderhoud.getOpmerkingen());
            assertEquals(validStatus, onderhoud.getStatus());
            assertEquals(mockMachine, onderhoud.getMachine());
            assertEquals(1, mockedGc.constructed().size());
            assertEquals(1, mockedMc.constructed().size());
        }
    }

    @Test
    void constructor_NullDatum_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Onderhoud(null, validStartTijd, validEindTijd, validTechniekerId, validReden, validRapport,
                        validOpmerkingen, validStatus, validMachineId));
        assertEquals("Datum mag niet null zijn.", exception.getMessage());
    }

    @Test
    void constructor_NullStartTijd_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Onderhoud(validDatum, null, validEindTijd, validTechniekerId, validReden, validRapport,
                        validOpmerkingen, validStatus, validMachineId));
        assertEquals("Starttijd mag niet null zijn.", exception.getMessage());
    }

    @Test
    void constructor_NullEindTijd_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Onderhoud(validDatum, validStartTijd, null, validTechniekerId, validReden, validRapport,
                        validOpmerkingen, validStatus, validMachineId));
        assertEquals("Eindtijd mag niet null zijn.", exception.getMessage());
    }

    @Test
    void constructor_NullReden_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Onderhoud(validDatum, validStartTijd, validEindTijd, validTechniekerId, null, validRapport,
                        validOpmerkingen, validStatus, validMachineId));
        assertEquals("Reden mag niet null, leeg of alleen spaties bevatten.", exception.getMessage());
    }

    @Test
    void constructor_EmptyReden_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Onderhoud(validDatum, validStartTijd, validEindTijd, validTechniekerId, "", validRapport,
                        validOpmerkingen, validStatus, validMachineId));
        assertEquals("Reden mag niet null, leeg of alleen spaties bevatten.", exception.getMessage());
    }

    @Test
    void constructor_BlankReden_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Onderhoud(validDatum, validStartTijd, validEindTijd, validTechniekerId, "   ", validRapport,
                        validOpmerkingen, validStatus, validMachineId));
        assertEquals("Reden mag niet null, leeg of alleen spaties bevatten.", exception.getMessage());
    }

    @Test
    void constructor_NullRapport_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Onderhoud(validDatum, validStartTijd, validEindTijd, validTechniekerId, validReden, null,
                        validOpmerkingen, validStatus, validMachineId));
        assertEquals("Rapport mag niet null, leeg of alleen spaties bevatten.", exception.getMessage());
    }

    @Test
    void constructor_EmptyRapport_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Onderhoud(validDatum, validStartTijd, validEindTijd, validTechniekerId, validReden, "",
                        validOpmerkingen, validStatus, validMachineId));
        assertEquals("Rapport mag niet null, leeg of alleen spaties bevatten.", exception.getMessage());
    }

    @Test
    void constructor_BlankRapport_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Onderhoud(validDatum, validStartTijd, validEindTijd, validTechniekerId, validReden, "  ",
                        validOpmerkingen, validStatus, validMachineId));
        assertEquals("Rapport mag niet null, leeg of alleen spaties bevatten.", exception.getMessage());
    }

    @Test
    void constructor_NullStatus_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Onderhoud(validDatum, validStartTijd, validEindTijd, validTechniekerId, validReden,
                        validRapport, validOpmerkingen, null, validMachineId));
        assertEquals("Status mag niet null zijn.", exception.getMessage());
    }

    @Test
    void constructor_StatusIngepland_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Onderhoud(validDatum, validStartTijd, validEindTijd, validTechniekerId, validReden,
                        validRapport, validOpmerkingen, OnderhoudStatus.INGEPLAND, validMachineId));
        assertEquals("Techniekers mogen geen 'ingepland' als status instellen.", exception.getMessage());
    }

    @Test
    void constructor_NonExistentTechnieker_ThrowsIllegalArgumentException() {
        int nonExistentTechniekerId = 999;
        try (MockedConstruction<GebruikerController> mockedGc = Mockito.mockConstruction(GebruikerController.class,
                (mock, context) -> when(mock.getRealGebruiker(nonExistentTechniekerId)).thenReturn(null));
                MockedConstruction<MachineController> mockedMc = Mockito.mockConstruction(MachineController.class, /*
                                                                                                                    * No
                                                                                                                    * interaction
                                                                                                                    * expected
                                                                                                                    */
                        (mock, context) -> when(mock.getRealMachine(validMachineId)).thenReturn(mockMachine))) {

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> new Onderhoud(validDatum, validStartTijd, validEindTijd, nonExistentTechniekerId, validReden,
                            validRapport, validOpmerkingen, validStatus, validMachineId));
            assertEquals("Technieker met ID " + nonExistentTechniekerId + " bestaat niet.", exception.getMessage());
        }
    }

    @Test
    void constructor_GebruikerIsNotTechnieker_ThrowsIllegalArgumentException() {
        int notTechniekerId = 2;
        Gebruiker notATechnieker = mock(Gebruiker.class);
        when(notATechnieker.getRol()).thenReturn(Rol.ADMINISTRATOR);

        try (MockedConstruction<GebruikerController> mockedGc = Mockito.mockConstruction(GebruikerController.class,
                (mock, context) -> when(mock.getRealGebruiker(notTechniekerId)).thenReturn(notATechnieker));
                MockedConstruction<MachineController> mockedMc = Mockito.mockConstruction(MachineController.class,
                        (mock, context) -> when(mock.getRealMachine(validMachineId)).thenReturn(mockMachine))) {

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> new Onderhoud(validDatum, validStartTijd, validEindTijd, notTechniekerId, validReden,
                            validRapport, validOpmerkingen, validStatus, validMachineId));
            assertEquals("Gebruiker met ID " + notTechniekerId + " is geen technieker.", exception.getMessage());
        }
    }

    @Test
    void constructor_NonExistentMachine_ThrowsIllegalArgumentException() {
        int nonExistentMachineId = 999;
        try (MockedConstruction<GebruikerController> mockedGc = Mockito.mockConstruction(GebruikerController.class,
                (mock, context) -> when(mock.getRealGebruiker(validTechniekerId)).thenReturn(mockTechnieker));
                MockedConstruction<MachineController> mockedMc = Mockito.mockConstruction(MachineController.class,
                        (mock, context) -> when(mock.getRealMachine(nonExistentMachineId)).thenReturn(null))) {

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> new Onderhoud(validDatum, validStartTijd, validEindTijd, validTechniekerId, validReden,
                            validRapport, validOpmerkingen, validStatus, nonExistentMachineId));
            assertEquals("Machine met ID " + nonExistentMachineId + " bestaat niet.", exception.getMessage());
        }
    }

    @Test
    void setTechnieker_ValidTechnieker_SetsTechnieker() {
        Onderhoud onderhoud = createOnderhoudWithProtectedConstructor();
        try (MockedConstruction<GebruikerController> mockedController = Mockito.mockConstruction(
                GebruikerController.class,
                (mock, context) -> when(mock.getRealGebruiker(validTechniekerId)).thenReturn(mockTechnieker))) {

            onderhoud.setTechnieker(validTechniekerId);
            assertEquals(mockTechnieker, onderhoud.getTechnieker());
            assertEquals(1, mockedController.constructed().size());
            verify(mockedController.constructed().get(0)).getRealGebruiker(validTechniekerId);
        }
    }

    @Test
    void setTechnieker_NonExistentTechnieker_ThrowsIllegalArgumentException() {
        Onderhoud onderhoud = createOnderhoudWithProtectedConstructor();
        int nonExistentTechniekerId = 999;
        try (MockedConstruction<GebruikerController> mockedController = Mockito.mockConstruction(
                GebruikerController.class,
                (mock, context) -> when(mock.getRealGebruiker(nonExistentTechniekerId)).thenReturn(null))) {

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                onderhoud.setTechnieker(nonExistentTechniekerId);
            });
            assertEquals("Technieker met ID " + nonExistentTechniekerId + " bestaat niet.", exception.getMessage());
        }
    }

    @Test
    void setTechnieker_GebruikerNotTechnieker_ThrowsIllegalArgumentException() {
        Onderhoud onderhoud = createOnderhoudWithProtectedConstructor();
        int notTechniekerId = 2;
        Gebruiker notATechnieker = mock(Gebruiker.class);
        when(notATechnieker.getRol()).thenReturn(Rol.ADMINISTRATOR);

        try (MockedConstruction<GebruikerController> mockedController = Mockito.mockConstruction(
                GebruikerController.class,
                (mock, context) -> when(mock.getRealGebruiker(notTechniekerId)).thenReturn(notATechnieker))) {

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                onderhoud.setTechnieker(notTechniekerId);
            });
            assertEquals("Gebruiker met ID " + notTechniekerId + " is geen technieker.", exception.getMessage());
        }
    }

    @Test
    void setMachine_ValidMachine_SetsMachine() {
        Onderhoud onderhoud = createOnderhoudWithProtectedConstructor();
        try (MockedConstruction<MachineController> mockedController = Mockito.mockConstruction(MachineController.class,
                (mock, context) -> when(mock.getRealMachine(validMachineId)).thenReturn(mockMachine))) {

            onderhoud.setMachine(validMachineId);
            assertEquals(mockMachine, onderhoud.getMachine());
            assertEquals(1, mockedController.constructed().size());
            verify(mockedController.constructed().get(0)).getRealMachine(validMachineId);
        }
    }

    @Test
    void setMachine_NonExistentMachine_ThrowsIllegalArgumentException() {
        Onderhoud onderhoud = createOnderhoudWithProtectedConstructor();
        int nonExistentMachineId = 999;
        try (MockedConstruction<MachineController> mockedController = Mockito.mockConstruction(MachineController.class,
                (mock, context) -> when(mock.getRealMachine(nonExistentMachineId)).thenReturn(null))) {

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                onderhoud.setMachine(nonExistentMachineId);
            });
            assertEquals("Machine met ID " + nonExistentMachineId + " bestaat niet.", exception.getMessage());
        }
    }

    @Test
    void toString_WithTechnieker_ReturnsCorrectFormat() {
        try (MockedConstruction<GebruikerController> mockedGc = Mockito.mockConstruction(GebruikerController.class,
                (mock, context) -> when(mock.getRealGebruiker(validTechniekerId)).thenReturn(mockTechnieker));
                MockedConstruction<MachineController> mockedMc = Mockito.mockConstruction(MachineController.class,
                        (mock, context) -> when(mock.getRealMachine(validMachineId)).thenReturn(mockMachine))) {

            Onderhoud onderhoud = new Onderhoud(validDatum, validStartTijd, validEindTijd,
                    validTechniekerId, validReden, validRapport, validOpmerkingen,
                    validStatus, validMachineId);

            String expected = String.format("Onderhoud op %s (%s - %s) door technieker %s | Status: %s",
                    validDatum, validStartTijd, validEindTijd, "Jan Technieker", validStatus);
            assertEquals(expected, onderhoud.toString());
        }
    }

    @Test
    void toString_WithNullTechnieker_ReturnsCorrectFormat() {
        Onderhoud onderhoud = createOnderhoudWithProtectedConstructor();
        onderhoud.setDatum(validDatum);
        onderhoud.setStartTijd(validStartTijd);
        onderhoud.setEindTijd(validEindTijd);
        onderhoud.setStatus(validStatus);

        String expected = String.format("Onderhoud op %s (%s - %s) door technieker %s | Status: %s",
                validDatum, validStartTijd, validEindTijd, "Onbekend", validStatus);
        assertEquals(expected, onderhoud.toString());
    }
}
