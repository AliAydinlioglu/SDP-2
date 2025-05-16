package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

import enums.MachineStatus;
import enums.ProductionStatus;
import enums.Rol;

class SiteTest {

    private Site site;
    private String validNaam = "Hoofdzetel Gent";

    private Gebruiker verantwoordelijke;
    private Gebruiker andereVerantwoordelijke;
    private Machine machine1;

    private Adres dummyAdres;
    private Adres anderDummyAdres;
    @BeforeEach
    void setUp() {
        dummyAdres = new Adres("Kerkstraat", "10", "9000", "Gent", "België");
        anderDummyAdres = new Adres("Stationstraat", "20", "8500", "Kortrijk", "België");

        verantwoordelijke = new Gebruiker("Jan", "Janssens", LocalDate.of(1980, 1, 1), dummyAdres, "jan.janssens@example.com", "0477123456", Rol.VERANTWOORDELIJKE, true);
        andereVerantwoordelijke = new Gebruiker("Piet", "Pieters", LocalDate.of(1985, 5, 5), anderDummyAdres, "piet.pieters@example.com", "0477654321", Rol.VERANTWOORDELIJKE, true);

        site = new Site(validNaam);

        Gebruiker techniekerVoorMachine = new Gebruiker("Techni", "Kerr", LocalDate.of(1990, 3,3), dummyAdres, "tech@example.com", "0488123123", Rol.TECHNIEKER, true);

        machine1 = new Machine("Compressor X1000", "Industriële compressor", "Hal A", MachineStatus.DRAAIT, ProductionStatus.IN_ORDE, 98, techniekerVoorMachine, 30, LocalDate.now().plusMonths(3), site);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        @Test
        void constructor_withNaamAndVerantwoordelijke_validInputs_createsSite() {
            Site newSite = new Site(validNaam, verantwoordelijke);
            assertEquals(validNaam, newSite.getNaam());
            assertEquals(verantwoordelijke, newSite.getVerantwoordelijke());
            assertNotNull(newSite.getMachines());
            assertTrue(newSite.getMachines().isEmpty());
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("Naam en Verantwoordelijke: Ongeldige Naam")
        void constructor_withNaamAndVerantwoordelijke_invalidNaam_throwsIllegalArgumentException(String invalidNaam) {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                new Site(invalidNaam, verantwoordelijke);
            });
            assertEquals("Site naam mag niet leeg zijn.", exception.getMessage());
        }

        @Test
        @DisplayName("Naam en Verantwoordelijke: Null Verantwoordelijke")
        void constructor_withNaamAndVerantwoordelijke_nullVerantwoordelijke_throwsIllegalArgumentException() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                new Site(validNaam, null);
            });
            assertEquals("Site verantwoordelijke mag niet leeg zijn.", exception.getMessage());
        }

        @Test
        @DisplayName("Alleen Naam: Geldige Input")
        void constructor_withNaamOnly_validInput_createsSite() {
            Site newSite = new Site(validNaam);
            assertEquals(validNaam, newSite.getNaam());
            assertNull(newSite.getVerantwoordelijke(), "Verantwoordelijke should be null when using constructor with name only");
            assertNotNull(newSite.getMachines());
            assertTrue(newSite.getMachines().isEmpty());
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("Alleen Naam: Ongeldige Naam")
        void constructor_withNaamOnly_invalidNaam_throwsIllegalArgumentException(String invalidNaam) {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                new Site(invalidNaam);
            });
            assertEquals("Site naam mag niet leeg zijn.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Setter Tests")
    class SetterTests {
        @Test
        void setNaam_validNaam_updatesNaam() {
            String nieuweNaam = "Productie Antwerpen";
            site.setNaam(nieuweNaam);
            assertEquals(nieuweNaam, site.getNaam());
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("setNaam: Ongeldige Naam")
        void setNaam_invalidNaam_throwsIllegalArgumentException(String invalidNaam) {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                site.setNaam(invalidNaam);
            });
            assertEquals("Site naam mag niet leeg zijn.", exception.getMessage());
        }

        @Test
        void setVerantwoordelijke_validVerantwoordelijke_updatesVerantwoordelijke() {
            site.setVerantwoordelijke(verantwoordelijke);
            assertEquals(verantwoordelijke, site.getVerantwoordelijke());
        }

        @Test
        @DisplayName("setVerantwoordelijke: Null Verantwoordelijke")
        void setVerantwoordelijke_nullVerantwoordelijke_throwsIllegalArgumentException() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                site.setVerantwoordelijke(null);
            });
            assertEquals("Site verantwoordelijke mag niet leeg zijn.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {
        @Test
        void getMachines_initializesEmptySetAndAllowsModification() {
            assertNotNull(site.getMachines(), "Machines set should be initialized.");
            assertTrue(site.getMachines().isEmpty(), "Machines set should be initially empty.");

            site.getMachines().add(machine1);
            assertEquals(1, site.getMachines().size());
            assertTrue(site.getMachines().contains(machine1));
        }
    }

    @Nested
    @DisplayName("toString Tests")
    class ToStringTests {
        @Test
        void toString_withVerantwoordelijkeAndMachines_returnsCorrectFormat() {
            site.setVerantwoordelijke(verantwoordelijke);
            site.getMachines().add(machine1);
            String expected = String.format("Site[id=0, naam='%s', Verantwoordelijke=%s, #Machines=%d]",
                    validNaam, verantwoordelijke.getEmail(), 1);
            assertEquals(expected, site.toString());
        }

        @Test
        void toString_withoutVerantwoordelijke_returnsCorrectFormat() {
            site.getMachines().add(machine1);
            String expected = String.format("Site[id=0, naam='%s', Verantwoordelijke=%s, #Machines=%d]",
                    validNaam, "null", 1);
            assertEquals(expected, site.toString());
        }

        @Test
        void toString_withoutMachines_returnsCorrectFormat() {
            site.setVerantwoordelijke(verantwoordelijke);
            String expected = String.format("Site[id=0, naam='%s', Verantwoordelijke=%s, #Machines=%d]",
                    validNaam, verantwoordelijke.getEmail(), 0);
            assertEquals(expected, site.toString());
        }
    }

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {
        @Test
        void builder_validNaamAndVerantwoordelijke_buildsSite() {
            Site builtSite = Site.builder()
                    .naam(validNaam)
                    .verantwoordelijke(verantwoordelijke)
                    .build();
            assertEquals(validNaam, builtSite.getNaam());
            assertEquals(verantwoordelijke, builtSite.getVerantwoordelijke());
            assertNotNull(builtSite.getMachines());
            assertTrue(builtSite.getMachines().isEmpty());
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("Builder: Ongeldige Naam via .naam()")
        void builder_invalidNaamInMethod_throwsIllegalArgumentException(String invalidNaam) {
            Site.Builder builder = Site.builder();
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                builder.naam(invalidNaam);
            });
            assertEquals("Naam mag niet leeg zijn", exception.getMessage());
        }

        @Test
        @DisplayName("Builder: build() zonder Naam")
        void builder_buildWithoutNaam_throwsIllegalStateException() {
            Site.Builder builder = Site.builder().verantwoordelijke(verantwoordelijke);
            Exception exception = assertThrows(IllegalStateException.class, builder::build);
            assertEquals("Naam is niet ingesteld in de builder en is verplicht.", exception.getMessage());
        }

        @Test
        @DisplayName("Builder: build() zonder Verantwoordelijke")
        void builder_buildWithoutVerantwoordelijke_throwsIllegalStateException() {
            Site.Builder builder = Site.builder().naam(validNaam);
            Exception exception = assertThrows(IllegalStateException.class, builder::build);
            assertEquals("Verantwoordelijke is niet ingesteld en is verplicht.", exception.getMessage());
        }

    }
}
