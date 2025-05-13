package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import enums.MachineStatus;
import enums.ProductionStatus;
import enums.Rol;

class SiteTest {

    private Site site;
    private Gebruiker verantwoordelijke;
    private Machine machine;
    private String validNaam = "Test Site";

    @BeforeEach
    void setUp() {
        // Create a test verantwoordelijke
        Adres adres = new Adres("Teststraat", "1", "9000", "Gent", "België");
        verantwoordelijke = new Gebruiker(
                "Test", "Verantwoordelijke",
                LocalDate.of(1990, 1, 1),
                adres,
                "test.verantwoordelijke@example.com",
                "0499123456",
                Rol.VERANTWOORDELIJKE,
                true
        );

        // Create test site
        site = new Site(validNaam);

        // Create test machine
        machine = new Machine(
                "Test Machine",
                "Test Product Info",
                "Test Location",
                MachineStatus.DRAAIT,
                ProductionStatus.IN_ORDE,
                100,
                verantwoordelijke,
                10,
                LocalDate.now().plusMonths(1),
                site
        );
    }

    @Test
    void constructor_validName_shouldCreateSite() {
        Site newSite = new Site(validNaam);

        assertEquals(validNaam, newSite.getNaam(), "Site name should match the name provided in constructor");
        assertNotNull(newSite.getMachines(), "Machines collection should be initialized");
        assertTrue(newSite.getMachines().isEmpty(), "Machines collection should be empty for new site");
        assertNull(newSite.getVerantwoordelijke(), "New site should not have a verantwoordelijke");
    }

    @Test
    void setNaam_validName_shouldSetName() {
        String newName = "Updated Site Name";

        site.setNaam(newName);

        assertEquals(newName, site.getNaam(), "Site name should be updated to the new value");
    }

    @Test
    void setNaam_nullName_shouldAcceptNull() {
        site.setNaam(null);
        assertNull(site.getNaam(), "Setting name to null should be accepted");
    }

    @Test
    void setNaam_emptyName_shouldAcceptEmptyString() {
        site.setNaam("");
        assertEquals("", site.getNaam(), "Setting name to empty string should be accepted");
    }

    @Test
    void setNaam_blankName_shouldAcceptBlankString() {
        String blankName = "   ";
        site.setNaam(blankName);
        assertEquals(blankName, site.getNaam(), "Setting name to blank string should be accepted");
    }

    @Test
    void setVerantwoordelijke_validVerantwoordelijke_shouldSetVerantwoordelijke() {
        site.setVerantwoordelijke(verantwoordelijke);

        assertEquals(verantwoordelijke, site.getVerantwoordelijke(), "Verantwoordelijke should be set to the provided value");
    }

    @Test
    void setVerantwoordelijke_nullVerantwoordelijke_shouldSetToNull() {
        site.setVerantwoordelijke(verantwoordelijke);

        site.setVerantwoordelijke(null);

        assertNull(site.getVerantwoordelijke(), "Verantwoordelijke should be set to null");
    }

    @Test
    void getMachines_returnsCollectionThatCanBeModified() {
        int initialSize = site.getMachines().size();

        Machine newMachine = new Machine(
                "New Machine",
                "New Product Info",
                "New Location",
                MachineStatus.DRAAIT,
                ProductionStatus.IN_ORDE,
                100,
                verantwoordelijke,
                10,
                LocalDate.now().plusMonths(1),
                site
        );

        site.getMachines().add(newMachine);

        assertEquals(initialSize + 1, site.getMachines().size(), "Collection size should increase after adding a machine");
        assertTrue(site.getMachines().contains(newMachine), "Added machine should be in the collection");
    }

    @Test
    void toString_shouldReturnCorrectFormat() {
        site.setVerantwoordelijke(verantwoordelijke);

        String result = site.toString();

        assertTrue(result.contains(Integer.toString(site.getSiteId())), "toString should include the site ID");
        assertTrue(result.contains(site.getNaam()), "toString should include the site name");
        assertTrue(result.contains(verantwoordelijke.getEmail()), "toString should include the verantwoordelijke email");
        assertTrue(result.contains(Integer.toString(site.getMachines().size())), "toString should include the number of machines");
    }

    @Test
    void toString_nullVerantwoordelijke_shouldHandleNull() {
        site.setVerantwoordelijke(null);

        String result = site.toString();

        assertTrue(result.contains("null"), "toString should handle null verantwoordelijke");
    }

    @Test
    void equalsAndHashCode_sameNameDifferentId_shouldBeEqual() {
        Site site1 = new Site("Same Name");
        Site site2 = new Site("Same Name");

        assertEquals(site1, site2, "Sites with the same name should be equal");
        assertEquals(site1.hashCode(), site2.hashCode(), "Sites with the same name should have the same hash code");
    }

    @Test
    void equalsAndHashCode_differentName_shouldNotBeEqual() {
        Site site1 = new Site("Site 1");
        Site site2 = new Site("Site 2");

        assertNotEquals(site1, site2, "Sites with different names should not be equal");
        assertNotEquals(site1.hashCode(), site2.hashCode(), "Sites with different names should have different hash codes");
    }
}
