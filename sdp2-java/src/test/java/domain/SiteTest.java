package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SiteTest {

    private Site site;
    private final String DEFAULT_NAAM = "Test Site";

    @Mock
    private Gebruiker mockVerantwoordelijke;

    @Mock
    private Machine mockMachine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockVerantwoordelijke.getEmail()).thenReturn("test@example.com");
        // Initialize site with a default valid state for most tests
        site = new Site(DEFAULT_NAAM, mockVerantwoordelijke);
    }

    @Test
    void constructor_WithNaamAndVerantwoordelijke_InitializesFieldsCorrectly() {
        assertEquals(DEFAULT_NAAM, site.getNaam());
        assertEquals(mockVerantwoordelijke, site.getVerantwoordelijke());
        assertTrue(site.getMachines().isEmpty(),
                "Machines should be empty upon initial creation with this constructor");
    }

    @Test
    void constructor_WithOnlyNaam_InitializesNaamAndNullVerantwoordelijke() {
        Site siteWithOnlyNaam = new Site(DEFAULT_NAAM);
        assertEquals(DEFAULT_NAAM, siteWithOnlyNaam.getNaam());
        assertNull(siteWithOnlyNaam.getVerantwoordelijke(),
                "Verantwoordelijke should be null when using constructor with only naam");
        assertTrue(siteWithOnlyNaam.getMachines().isEmpty(), "Machines should be empty upon initial creation");
    }

    @Test
    void setNaam_WithValidNaam_SetsNaam() {
        String newNaam = "New Site Name";
        site.setNaam(newNaam);
        assertEquals(newNaam, site.getNaam());
    }

    @Test
    void setNaam_WithNullNaam_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> site.setNaam(null));
        assertEquals("Site naam mag niet leeg zijn.", exception.getMessage());
    }

    @Test
    void setNaam_WithEmptyNaam_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> site.setNaam(""));
        assertEquals("Site naam mag niet leeg zijn.", exception.getMessage());
    }

    @Test
    void setNaam_WithBlankNaam_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> site.setNaam("   "));
        assertEquals("Site naam mag niet leeg zijn.", exception.getMessage());
    }

    @Test
    void setVerantwoordelijke_WithValidVerantwoordelijke_SetsVerantwoordelijke() {
        Gebruiker newMockVerantwoordelijke = mock(Gebruiker.class);
        site.setVerantwoordelijke(newMockVerantwoordelijke);
        assertEquals(newMockVerantwoordelijke, site.getVerantwoordelijke());
    }

    @Test
    void setVerantwoordelijke_WithNullVerantwoordelijke_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> site.setVerantwoordelijke(null));
        assertEquals("Site verantwoordelijke mag niet leeg zijn.", exception.getMessage());
    }

    @Test
    void getMachines_AfterSettingMachines_ReturnsCorrectSet() {
        Set<Machine> machines = new HashSet<>();
        machines.add(mockMachine);
        site.setMachines(machines); // Assuming Site has a setMachines method
        assertEquals(machines, site.getMachines());
        assertEquals(1, site.getMachines().size());
    }

    @Test
    void setMachines_WithValidSet_SetsMachines() {
        Set<Machine> newMachines = new HashSet<>();
        newMachines.add(mockMachine);
        site.setMachines(newMachines);
        assertEquals(newMachines, site.getMachines());
        assertFalse(site.getMachines().isEmpty());
    }

    @Test
    void toString_WithVerantwoordelijkeAndMachines_ReturnsCorrectFormat() {
        Set<Machine> machines = new HashSet<>();
        machines.add(mockMachine);
        site.setMachines(machines);
        // siteId is auto-generated, so we get it from the object for the assertion
        String expected = String.format("Site[id=%d, naam='%s', Verantwoordelijke=%s, #Machines=%d]",
                site.getSiteId(), DEFAULT_NAAM, "test@example.com", 1);
        assertEquals(expected, site.toString());
    }

    @Test
    void toString_WithNullVerantwoordelijkeAndNoMachines_ReturnsCorrectFormat() {
        Site siteWithNulls = new Site(DEFAULT_NAAM); // Verantwoordelijke will be null
        // siteId is auto-generated
        String expected = String.format("Site[id=%d, naam='%s', Verantwoordelijke=%s, #Machines=%d]",
                siteWithNulls.getSiteId(), DEFAULT_NAAM, "null", 0);
        assertEquals(expected, siteWithNulls.toString());
    }

    @Test
    void toString_WithVerantwoordelijkeAndNullMachines_ReturnsCorrectFormat() {
        site.setMachines(null); // Explicitly set machines to null
        String expected = String.format("Site[id=%d, naam='%s', Verantwoordelijke=%s, #Machines=%d]",
                site.getSiteId(), DEFAULT_NAAM, "test@example.com", 0);
        assertEquals(expected, site.toString());
    }

    @Test
    void equals_SameObject_ReturnsTrue() {
        assertTrue(site.equals(site));
    }

    @Test
    void equals_NullObject_ReturnsFalse() {
        assertFalse(site.equals(null));
    }

    @Test
    void equals_DifferentClass_ReturnsFalse() {
        assertFalse(site.equals(new Object()));
    }

    @Test
    void equals_DifferentNaam_ReturnsFalse() {
        Site otherSite = new Site("Different Name", mockVerantwoordelijke);
        assertFalse(site.equals(otherSite));
    }

    @Test
    void equals_DifferentVerantwoordelijke_ReturnsFalse() {
        Gebruiker otherMockVerantwoordelijke = mock(Gebruiker.class);
        when(otherMockVerantwoordelijke.getEmail()).thenReturn("other@example.com");
        Site otherSite = new Site(DEFAULT_NAAM, otherMockVerantwoordelijke);
        assertFalse(site.equals(otherSite));
    }

    @Test
    void equals_OneVerantwoordelijkeIsNull_ReturnsFalse() {
        Site siteWithNullVerantwoordelijke = new Site(DEFAULT_NAAM);
        assertFalse(site.equals(siteWithNullVerantwoordelijke));
        assertFalse(siteWithNullVerantwoordelijke.equals(site));
    }

    @Test
    void equals_BothVerantwoordelijkeAreNullSameNaam_ReturnsTrue() {
        Site site1 = new Site(DEFAULT_NAAM);
        Site site2 = new Site(DEFAULT_NAAM);
        assertTrue(site1.equals(site2));
    }

    @Test
    void equals_BothVerantwoordelijkeAreNullDifferentNaam_ReturnsFalse() {
        Site site1 = new Site(DEFAULT_NAAM);
        Site site2 = new Site("Another Name");
        assertFalse(site1.equals(site2));
    }

    @Test
    void equals_SameNaamAndVerantwoordelijke_ReturnsTrue() {
        // siteId is excluded from equals, machines are also excluded
        Site otherSite = new Site(DEFAULT_NAAM, mockVerantwoordelijke);
        assertTrue(site.equals(otherSite));
    }

    @Test
    void equals_SameNaamVerantwoordelijkeDifferentMachines_ReturnsTrue() {
        Site otherSite = new Site(DEFAULT_NAAM, mockVerantwoordelijke);
        Set<Machine> machinesForOther = new HashSet<>();
        machinesForOther.add(mockMachine);
        otherSite.setMachines(machinesForOther); // site has empty machines by default in setup
        assertTrue(site.equals(otherSite), "Equals should be true as machines are excluded");
    }

    @Test
    void hashCode_ConsistentForSameObject() {
        assertEquals(site.hashCode(), site.hashCode());
    }

    @Test
    void hashCode_SameForEqualObjects() {
        Site otherSite = new Site(DEFAULT_NAAM, mockVerantwoordelijke);
        assertEquals(site.hashCode(), otherSite.hashCode());
    }

    @Test
    void hashCode_DifferentForObjectsWithDifferentNaam() {
        Site otherSite = new Site("Different Name", mockVerantwoordelijke);
        assertNotEquals(site.hashCode(), otherSite.hashCode());
    }

    @Test
    void hashCode_DifferentForObjectsWithDifferentVerantwoordelijke() {
        Gebruiker otherMockVerantwoordelijke = mock(Gebruiker.class);
        when(otherMockVerantwoordelijke.getEmail()).thenReturn("other@example.com");
        Site otherSite = new Site(DEFAULT_NAAM, otherMockVerantwoordelijke);
        assertNotEquals(site.hashCode(), otherSite.hashCode());
    }

    @Test
    void hashCode_SameForObjectsWithNullVerantwoordelijkeAndSameNaam() {
        Site site1 = new Site(DEFAULT_NAAM);
        Site site2 = new Site(DEFAULT_NAAM);
        assertEquals(site1.hashCode(), site2.hashCode());
    }

    // Builder Tests
    @Test
    void builder_naam_WithValidNaam_ReturnsBuilder() {
        Site.Builder builder = Site.builder();
        assertSame(builder, builder.naam(DEFAULT_NAAM));
    }

    @Test
    void builder_naam_WithNullNaam_ThrowsIllegalArgumentException() {
        Site.Builder builder = Site.builder();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> builder.naam(null));
        assertEquals("Naam mag niet leeg zijn", exception.getMessage());
    }

    @Test
    void builder_naam_WithBlankNaam_ThrowsIllegalArgumentException() {
        Site.Builder builder = Site.builder();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> builder.naam("   "));
        assertEquals("Naam mag niet leeg zijn", exception.getMessage());
    }

    @Test
    void builder_verantwoordelijke_WithValidVerantwoordelijke_ReturnsBuilder() {
        Site.Builder builder = Site.builder();
        assertSame(builder, builder.verantwoordelijke(mockVerantwoordelijke));
    }

    @Test
    void builder_verantwoordelijke_WithNullVerantwoordelijke_ReturnsBuilder() {
        // Builder allows null for verantwoordelijke at this stage, build() method will
        // perform validation
        Site.Builder builder = Site.builder();
        assertSame(builder, builder.verantwoordelijke(null));
    }

    @Test
    void builder_machines_WithValidMachines_ReturnsBuilder() {
        Site.Builder builder = Site.builder();
        Set<Machine> machines = new HashSet<>();
        machines.add(mockMachine);
        assertSame(builder, builder.machines(machines));
    }

    @Test
    void builder_machines_WithNullMachines_ThrowsIllegalArgumentException() {
        Site.Builder builder = Site.builder();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> builder.machines(null));
        assertEquals("Machines mag niet leeg zijn", exception.getMessage());
    }

    @Test
    void builder_machines_WithEmptyMachines_ThrowsIllegalArgumentException() {
        Site.Builder builder = Site.builder();
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> builder.machines(Collections.emptySet()));
        assertEquals("Machines mag niet leeg zijn", exception.getMessage());
    }

    @Test
    void builder_build_WithNaamAndVerantwoordelijke_CreatesSiteCorrectly() {
        Site builtSite = Site.builder()
                .naam(DEFAULT_NAAM)
                .verantwoordelijke(mockVerantwoordelijke)
                .build();
        assertNotNull(builtSite);
        assertEquals(DEFAULT_NAAM, builtSite.getNaam());
        assertEquals(mockVerantwoordelijke, builtSite.getVerantwoordelijke());
        assertTrue(builtSite.getMachines().isEmpty(),
                "Machines should be empty as they are not set by the builder's build method for Site");
    }

    @Test
    void builder_build_WithNaamVerantwoordelijkeAndMachines_CreatesSiteButMachinesNotSetOnSite() {
        Set<Machine> machinesArg = new HashSet<>();
        machinesArg.add(mockMachine);

        Site builtSite = Site.builder()
                .naam(DEFAULT_NAAM)
                .verantwoordelijke(mockVerantwoordelijke)
                .machines(machinesArg) // This sets machines in builder, but build() doesn't use it for Site
                .build();

        assertNotNull(builtSite);
        assertEquals(DEFAULT_NAAM, builtSite.getNaam());
        assertEquals(mockVerantwoordelijke, builtSite.getVerantwoordelijke());
        // The current Site.Builder.build() method does not pass the machines to the
        // Site constructor.
        // The private Site(String naam, Gebruiker verantwoordelijke, Set<Machine>
        // machines) constructor is not used by the builder.
        // Therefore, the machines collection on the built Site object will be an empty
        // HashSet.
        assertTrue(builtSite.getMachines().isEmpty(),
                "Machines should be empty because the builder does not currently set them on the Site object.");
    }

    @Test
    void builder_build_WithoutNaam_ThrowsIllegalStateException() {
        Site.Builder builder = Site.builder().verantwoordelijke(mockVerantwoordelijke);
        Exception exception = assertThrows(IllegalStateException.class, builder::build);
        assertEquals("Naam is niet ingesteld in de builder en is verplicht.", exception.getMessage());
    }

    @Test
    void builder_build_WithoutVerantwoordelijke_ThrowsIllegalStateException() {
        Site.Builder builder = Site.builder().naam(DEFAULT_NAAM);
        Exception exception = assertThrows(IllegalStateException.class, builder::build);
        assertEquals("Verantwoordelijke is niet ingesteld en is verplicht.", exception.getMessage());
    }

    @Test
    void builder_build_WithNullVerantwoordelijkeSetInBuilder_ThrowsIllegalStateException() {
        Site.Builder builder = Site.builder()
                .naam(DEFAULT_NAAM)
                .verantwoordelijke(null); // Explicitly set verantwoordelijke to null in builder

        Exception exception = assertThrows(IllegalStateException.class, builder::build);
        assertEquals("Verantwoordelijke is niet ingesteld en is verplicht.", exception.getMessage());
    }
}
