package domain;

import enums.MachineStatus;
import enums.ProductionStatus;
import enums.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MachineTest {

    private Gebruiker testTechnieker;
    private Site testSite;
    private Adres testAdres;

    @BeforeEach
    void setUp() {
        testAdres = new Adres("Teststraat", "1", "1000", "Brussel", "België");
        testTechnieker = new Gebruiker(
                "Tech",
                "Niek",
                LocalDate.of(1980, 1, 1),
                testAdres,
                "tech.niek@example.com",
                "0477123456",
                Rol.TECHNIEKER,
                true);
        testSite = new Site("TestSite");
        testSite.setVerantwoordelijke(testTechnieker);
    }

    @Test
    void constructor_validArguments_shouldCreateMachine() {
        Machine machine = new Machine(
                "MachineNaam",
                "ProductInfo",
                "LocatieA",
                MachineStatus.DRAAIT,
                ProductionStatus.IN_ORDE,
                100,
                testTechnieker,
                10,
                LocalDate.now().plusMonths(6),
                testSite);

        assertEquals("MachineNaam", machine.getNaam());
        assertEquals("ProductInfo", machine.getProductInfo());
        assertEquals("LocatieA", machine.getLocatie());
        assertEquals(MachineStatus.DRAAIT, machine.getStatus());
        assertEquals(ProductionStatus.IN_ORDE, machine.getProductieStatus());
        assertEquals(100, machine.getUptime());
        assertEquals(testTechnieker, machine.getTechnieker());
        assertEquals(10, machine.getDagenSindsOnderhoud());
        assertEquals(LocalDate.now().plusMonths(6), machine.getVolgendOnderhoud());
        assertEquals(testSite, machine.getSite());
    }

    @Test
    void constructor_nullNaam_shouldThrowIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Machine(
                    null, "ProductInfo", "LocatieA", MachineStatus.DRAAIT,
                    ProductionStatus.IN_ORDE, 100, testTechnieker, 10,
                    LocalDate.now().plusMonths(6), testSite);
        });
        assertEquals("Naam en locatie moeten ingevuld zijn.", exception.getMessage());
    }

    @Test
    void constructor_blankNaam_shouldThrowIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Machine(
                    "  ", "ProductInfo", "LocatieA", MachineStatus.DRAAIT,
                    ProductionStatus.IN_ORDE, 100, testTechnieker, 10,
                    LocalDate.now().plusMonths(6), testSite);
        });
        assertEquals("Naam en locatie moeten ingevuld zijn.", exception.getMessage());
    }

    @Test
    void constructor_nullLocatie_shouldThrowIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Machine(
                    "MachineNaam", "ProductInfo", null, MachineStatus.DRAAIT,
                    ProductionStatus.IN_ORDE, 100, testTechnieker, 10,
                    LocalDate.now().plusMonths(6), testSite);
        });
        assertEquals("Naam en locatie moeten ingevuld zijn.", exception.getMessage());
    }

    @Test
    void constructor_blankLocatie_shouldThrowIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Machine(
                    "MachineNaam", "ProductInfo", " ", MachineStatus.DRAAIT,
                    ProductionStatus.IN_ORDE, 100, testTechnieker, 10,
                    LocalDate.now().plusMonths(6), testSite);
        });
        assertEquals("Naam en locatie moeten ingevuld zijn.", exception.getMessage());
    }

    @Test
    void constructor_nullTechnieker_shouldThrowIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Machine(
                    "MachineNaam", "ProductInfo", "LocatieA", MachineStatus.DRAAIT,
                    ProductionStatus.IN_ORDE, 100, null, 10,
                    LocalDate.now().plusMonths(6), testSite);
        });
        assertEquals("Technieker moet opgegeven zijn.", exception.getMessage());
    }

    @Test
    void constructor_nullSite_shouldThrowIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Machine(
                    "MachineNaam", "ProductInfo", "LocatieA", MachineStatus.DRAAIT,
                    ProductionStatus.IN_ORDE, 100, testTechnieker, 10,
                    LocalDate.now().plusMonths(6), null);
        });
        assertEquals("Site moet opgegeven zijn.", exception.getMessage());
    }

    @Test
    void constructor_negativeUptime_shouldThrowIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Machine(
                    "MachineNaam", "ProductInfo", "LocatieA", MachineStatus.DRAAIT,
                    ProductionStatus.IN_ORDE, -5, testTechnieker, 10,
                    LocalDate.now().plusMonths(6), testSite);
        });
        assertEquals("Uptime mag niet negatief zijn.", exception.getMessage());
    }

    @Test
    void constructor_negativeDagenSindsOnderhoud_shouldThrowIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Machine(
                    "MachineNaam", "ProductInfo", "LocatieA", MachineStatus.DRAAIT,
                    ProductionStatus.IN_ORDE, 100, testTechnieker, -5,
                    LocalDate.now().plusMonths(6), testSite);
        });
        assertEquals("Aantal dagen sinds het laatste onderhoud mag niet negatief zijn.", exception.getMessage());
    }

    @Test
    void constructor_nullVolgendOnderhoud_shouldThrowIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Machine(
                    "MachineNaam", "ProductInfo", "LocatieA", MachineStatus.DRAAIT,
                    ProductionStatus.IN_ORDE, 100, testTechnieker, 10,
                    null, testSite);
        });
        assertEquals("De datum voor het volgende onderhoud mag niet leeg zijn.", exception.getMessage());
    }

    @Test
    void toString_shouldReturnCorrectFormat() {
        Machine machine = new Machine(
                "M001", "InfoXYZ", "Hal_1", MachineStatus.DRAAIT,
                ProductionStatus.IN_ORDE, 200, testTechnieker, 5,
                LocalDate.of(2025, 12, 31), testSite);
        String machineString = machine.toString();
        assertTrue(machineString.contains("Naam: M001"));
        assertTrue(machineString.contains("Info: InfoXYZ"));
        assertTrue(machineString.contains("Status: DRAAIT"));
        assertTrue(machineString.contains("Uptime: 200"));
        assertTrue(machineString.contains("Tech: tech.niek@example.com"));
        assertTrue(machineString.contains("Site: TestSite"));
    }
}
