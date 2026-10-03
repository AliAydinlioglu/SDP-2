package domain;

import dto.AdresDTO;
import dto.GebruikerDTO;
import dto.MachineDTO;
import dto.SiteDTO;
import enums.MachineStatus;
import enums.ProductionStatus;
import enums.Rol;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.SiteDao;
import repository.SiteDaoJpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SiteControllerTest {

    @Mock
    private GebruikerController mockGebruikerController;

    @Mock
    private LogController mockLogController;

    private SiteDao mockSiteDao;
    private SiteController siteController;

    private GebruikerDTO dummyVerantwoordelijkeDto;
    private Gebruiker dummyVerantwoordelijkeEntity;
    private GebruikerDTO dummyIngelogdeGebruikerDto;
    private AdresDTO dummyAdresDto;

    @Captor
    ArgumentCaptor<Site> siteCaptor;

    @BeforeEach
    void setUp() {
        dummyAdresDto = new AdresDTO("Straat", "1", "9000", "Gent", "België");
        dummyVerantwoordelijkeDto = new GebruikerDTO(1, "Jan", "Verantwoordelijk", LocalDate.now().minusYears(30), dummyAdresDto, "jan.ver@example.com", "0477112233", Rol.VERANTWOORDELIJKE, true);
        dummyVerantwoordelijkeEntity = new Gebruiker("Jan", "Verantwoordelijk", LocalDate.now().minusYears(30), new Adres("Straat", "1", "9000", "Gent", "België"), "jan.ver@example.com", "0477112233", Rol.VERANTWOORDELIJKE, true);
        dummyIngelogdeGebruikerDto = new GebruikerDTO(2, "Admin", "User", LocalDate.now().minusYears(40), dummyAdresDto, "admin@example.com", "0477445566", Rol.ADMINISTRATOR, true);

        try (MockedConstruction<SiteDaoJpa> mockedConstruction = Mockito.mockConstruction(SiteDaoJpa.class,
                (mock, context) -> {
                    this.mockSiteDao = mock;
                    when(mock.findAll()).thenReturn(new ArrayList<>());
                })) {
            siteController = new SiteController(mockGebruikerController, mockLogController);
        }
    }

    @Test
    void constructorWithParams_initializesAndLoadsSites() {
        Site site1 = new Site("Test Site 1", dummyVerantwoordelijkeEntity);
        List<Site> sitesFromDb = Arrays.asList(site1);

        try (MockedConstruction<SiteDaoJpa> mockedConstruction = Mockito.mockConstruction(SiteDaoJpa.class,
                (mock, context) -> {
                    this.mockSiteDao = mock;
                    when(mock.findAll()).thenReturn(sitesFromDb);
                })) {
            siteController = new SiteController(mockGebruikerController, mockLogController);
        }

        assertNotNull(siteController.getAllSites());
        assertEquals(1, siteController.getAllSites().size());
        assertEquals("Test Site 1", siteController.getAllSites().get(0).naam());
        verify(mockSiteDao).findAll();
    }

    @Test
    void parameterlessConstructor_callsOtherConstructorAndLoadsSites() {
        try (MockedConstruction<SiteDaoJpa> mockedDaoConstruction = Mockito.mockConstruction(SiteDaoJpa.class,
                (mock, context) -> {
                    this.mockSiteDao = mock;
                    when(mock.findAll()).thenReturn(Collections.emptyList());
                });
             MockedConstruction<GebruikerController> mockedGcConstruction = Mockito.mockConstruction(GebruikerController.class);
             MockedConstruction<LogController> mockedLcConstruction = Mockito.mockConstruction(LogController.class)) {

            SiteController sc = new SiteController();
            assertNotNull(sc);
            verify(mockSiteDao).findAll();
        }
    }

    @Test
    void getAllSites_returnsCorrectlyLoadedList() {
        Site siteEntity = new Site("Site A", dummyVerantwoordelijkeEntity);
        List<Site> dbSites = List.of(siteEntity);

        try (MockedConstruction<SiteDaoJpa> mocked = Mockito.mockConstruction(SiteDaoJpa.class,
                (m, context) -> {
                    this.mockSiteDao = m;
                    when(m.findAll()).thenReturn(dbSites);
                })) {
            siteController = new SiteController(mockGebruikerController, mockLogController);
        }

        ObservableList<SiteDTO> result = siteController.getAllSites();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Site A", result.get(0).naam());
    }

    @Test
    void getSiteDetails_validSite_returnsDetails() {
        SiteDTO siteDtoInList = new SiteDTO(1, "Detail Site", dummyVerantwoordelijkeDto, Collections.emptySet());
        siteController.getAllSites().add(siteDtoInList);

        SiteDTO result = siteController.getSiteDetails(siteDtoInList);
        assertEquals(siteDtoInList, result);
    }

    @Test
    void getSiteDetails_nullSite_throwsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> siteController.getSiteDetails(null));
        assertEquals("Kan details niet ophalen van een null site.", exception.getMessage());
    }

    @Test
    void getSiteDetails_siteNotInList_throwsIllegalArgumentException() {
        SiteDTO siteDtoNotInList = new SiteDTO(99, "Niet Bestaande Site", dummyVerantwoordelijkeDto, Collections.emptySet());
        siteController.getAllSites().clear();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> siteController.getSiteDetails(siteDtoNotInList));
        assertEquals("Site not found", exception.getMessage());
    }

    @Test
    void getAantalMachinesVoorSite_siteWithMachines_returnsCorrectCount() {
        Set<MachineDTO> machines = new HashSet<>(Arrays.asList(
                new MachineDTO(1, "M1", "Info1", "Loc1", MachineStatus.DRAAIT, ProductionStatus.IN_ORDE, 100, 10, LocalDate.now(), dummyVerantwoordelijkeDto, null),
                new MachineDTO(2, "M2", "Info2", "Loc2", MachineStatus.GESTOPT_MANUEEL, ProductionStatus.FALEND, 50, 20, LocalDate.now(), dummyVerantwoordelijkeDto, null)
        ));
        SiteDTO siteDto = new SiteDTO(1, "Site Met Machines", dummyVerantwoordelijkeDto, machines);
        assertEquals(2, siteController.getAantalMachinesVoorSite(siteDto));
    }

    @Test
    void getAantalMachinesVoorSite_siteWithEmptyOrNullMachineSet_returnsZero() {
        SiteDTO siteDtoEmpty = new SiteDTO(1, "Site Zonder Machines", dummyVerantwoordelijkeDto, Collections.emptySet());
        assertEquals(0, siteController.getAantalMachinesVoorSite(siteDtoEmpty));

        SiteDTO siteDtoNull = new SiteDTO(2, "Site Null Machines", dummyVerantwoordelijkeDto, null);
        assertEquals(0, siteController.getAantalMachinesVoorSite(siteDtoNull));
    }

    @Test
    void getAantalMachinesVoorSite_nullSite_returnsZero() {
        assertEquals(0, siteController.getAantalMachinesVoorSite(null));
    }

    @Test
    void getSitesByUserId_validUserId_returnsSites() {
        int userId = 1;
        Site siteEntity = new Site("User Site", dummyVerantwoordelijkeEntity);
        List<Site> dbSites = List.of(siteEntity);
        when(mockSiteDao.getSitesByVerantwoordelijkeId(userId)).thenReturn(dbSites);

        ObservableList<SiteDTO> result = siteController.getSitesByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("User Site", result.get(0).naam());
        verify(mockSiteDao, times(2)).getSitesByVerantwoordelijkeId(userId);
    }

    @Test
    void getSitesByUserId_userIdWithNoSites_returnsEmptyList() {
        int userId = 2;
        when(mockSiteDao.getSitesByVerantwoordelijkeId(userId)).thenReturn(Collections.emptyList());
        ObservableList<SiteDTO> result = siteController.getSitesByUserId(userId);
        assertTrue(result.isEmpty());
    }

    @Test
    void addSite_validData_addsSiteAndLogs() {
        String siteNaam = "Nieuwe Site";
        when(mockGebruikerController.getRealGebruiker(dummyVerantwoordelijkeDto.id())).thenReturn(dummyVerantwoordelijkeEntity);
        doNothing().when(mockSiteDao).insert(any(Site.class));

        siteController.addSite(siteNaam, dummyVerantwoordelijkeDto, dummyIngelogdeGebruikerDto);

        assertEquals(1, siteController.getAllSites().size());
        assertEquals(siteNaam, siteController.getAllSites().get(0).naam());
        assertEquals(dummyVerantwoordelijkeDto.email(), siteController.getAllSites().get(0).verantwoordelijke().email());

        verify(mockSiteDao).startTransaction();
        verify(mockSiteDao).insert(siteCaptor.capture());
        verify(mockSiteDao).commitTransaction();
        verify(mockLogController).addLog(eq(dummyIngelogdeGebruikerDto), eq("Nieuwe site aangemaakt"), anyString());

        assertEquals(siteNaam, siteCaptor.getValue().getNaam());
        assertEquals(dummyVerantwoordelijkeEntity, siteCaptor.getValue().getVerantwoordelijke());
    }

    @ParameterizedTest
    @NullSource @EmptySource @ValueSource(strings = {"  "})
    void addSite_invalidName_throwsIllegalArgumentException(String invalidNaam) {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                siteController.addSite(invalidNaam, dummyVerantwoordelijkeDto, dummyIngelogdeGebruikerDto)
        );
        assertEquals("Site naam mag niet leeg zijn.", exception.getMessage());
        verify(mockSiteDao, never()).insert(any());
    }

    @Test
    void addSite_nullVerantwoordelijkeDto_throwsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                siteController.addSite("Test Site", null, dummyIngelogdeGebruikerDto)
        );
        assertEquals("Verantwoordelijke voor de site is verplicht.", exception.getMessage());
        verify(mockSiteDao, never()).insert(any());
    }

    @Test
    void addSite_nonExistingVerantwoordelijke_throwsIllegalArgumentException() {
        when(mockGebruikerController.getRealGebruiker(dummyVerantwoordelijkeDto.id())).thenReturn(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                siteController.addSite("Test Site", dummyVerantwoordelijkeDto, dummyIngelogdeGebruikerDto)
        );
        assertTrue(exception.getMessage().contains("Verantwoordelijke gebruiker met id " + dummyVerantwoordelijkeDto.id() + " niet gevonden."));
        verify(mockSiteDao, never()).insert(any());
    }

    @Test
    void addSite_daoException_throwsRuntimeExceptionAndRollsBack() {
        when(mockGebruikerController.getRealGebruiker(dummyVerantwoordelijkeDto.id())).thenReturn(dummyVerantwoordelijkeEntity);
        doThrow(new RuntimeException("DB error")).when(mockSiteDao).insert(any(Site.class));

        Exception exception = assertThrows(RuntimeException.class, () ->
                siteController.addSite("Test Site", dummyVerantwoordelijkeDto, dummyIngelogdeGebruikerDto)
        );
        assertEquals("Kon nieuwe site niet opslaan: DB error", exception.getMessage());
        verify(mockSiteDao).startTransaction();
        verify(mockSiteDao).rollbackTransaction();
        verify(mockSiteDao, never()).commitTransaction();
        assertTrue(siteController.getAllSites().isEmpty());
    }

    private Site siteEntityToDelete;

    private void reinitializeSiteControllerWithMockSetup(Consumer<SiteDao> daoConfigurator) {
        try (MockedConstruction<SiteDaoJpa> mockedConstruction = Mockito.mockConstruction(SiteDaoJpa.class,
                (mock, context) -> {
                    this.mockSiteDao = mock;
                    daoConfigurator.accept(mock);
                })) {
            siteController = new SiteController(mockGebruikerController, mockLogController);
        }
    }
    @FunctionalInterface
    interface Consumer<T> {
        void accept(T t);
    }


    @Test
    void deleteSite_existingSiteNoMachines_deletesAndLogs() {
        siteEntityToDelete = new Site("Te Verwijderen Site", dummyVerantwoordelijkeEntity);
        try {
            java.lang.reflect.Field idField = Site.class.getDeclaredField("siteId");
            idField.setAccessible(true);
            idField.set(siteEntityToDelete, 1);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Kon siteId niet instellen voor test: " + e.getMessage());
        }

        reinitializeSiteControllerWithMockSetup(dao -> {
            when(dao.findAll()).thenReturn(List.of(siteEntityToDelete));
            when(dao.get(siteEntityToDelete.getSiteId())).thenReturn(siteEntityToDelete);
        });

        assertEquals(1, siteController.getAllSites().size());
        assertEquals(siteEntityToDelete.getSiteId(), siteController.getAllSites().get(0).id());

        siteController.deleteSite(siteEntityToDelete.getSiteId(), dummyIngelogdeGebruikerDto);

        assertTrue(siteController.getAllSites().isEmpty());
        verify(mockSiteDao, times(2)).startTransaction();
        verify(mockSiteDao).delete(siteEntityToDelete);
        verify(mockSiteDao, times(2)).commitTransaction();
        verify(mockLogController).addLog(eq(dummyIngelogdeGebruikerDto), eq("Site Verwijderd"), anyString());
    }

    @Test
    void deleteSite_existingSiteWithMachines_throwsIllegalStateException() {
        siteEntityToDelete = new Site("Te Verwijderen Site Met Machines", dummyVerantwoordelijkeEntity);
        try {
            java.lang.reflect.Field idField = Site.class.getDeclaredField("siteId");
            idField.setAccessible(true);
            idField.set(siteEntityToDelete, 2);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Kon siteId niet instellen voor test: " + e.getMessage());
        }

        Machine machine = new Machine("M1", "Info", "Loc",
                MachineStatus.DRAAIT, ProductionStatus.IN_ORDE,
                0, dummyVerantwoordelijkeEntity, 0,
                LocalDate.now().plusMonths(1),
                siteEntityToDelete);
        siteEntityToDelete.getMachines().add(machine);

        reinitializeSiteControllerWithMockSetup(dao -> {
            when(dao.findAll()).thenReturn(List.of(siteEntityToDelete));
            when(dao.get(siteEntityToDelete.getSiteId())).thenReturn(siteEntityToDelete);
        });
        assertEquals(1, siteController.getAllSites().size());

        Exception exception = assertThrows(IllegalStateException.class, () ->
                siteController.deleteSite(siteEntityToDelete.getSiteId(), dummyIngelogdeGebruikerDto)
        );
        assertTrue(exception.getMessage().contains("Kan site '" + siteEntityToDelete.getNaam() + "' niet verwijderen"));
        verify(mockSiteDao, never()).delete(any());
    }

    @Test
    void deleteSite_nonExistingSite_logsFailure() {
        siteEntityToDelete = new Site("Bestaande Site", dummyVerantwoordelijkeEntity);
        try {
            java.lang.reflect.Field idField = Site.class.getDeclaredField("siteId");
            idField.setAccessible(true);
            idField.set(siteEntityToDelete, 1);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Kon siteId niet instellen voor test: " + e.getMessage());
        }

        reinitializeSiteControllerWithMockSetup(dao -> {
            when(dao.findAll()).thenReturn(List.of(siteEntityToDelete));
            when(dao.get(1)).thenReturn(siteEntityToDelete);
            when(dao.get(999)).thenReturn(null);
        });

        int nonExistingSiteId = 999;
        assertEquals(1, siteController.getAllSites().size());

        siteController.deleteSite(nonExistingSiteId, dummyIngelogdeGebruikerDto);

        assertEquals(1, siteController.getAllSites().size());
        verify(mockLogController).addLog(eq(dummyIngelogdeGebruikerDto), eq("Site Verwijderen Mislukt"), contains("Site met ID " + nonExistingSiteId + " niet gevonden."));
        verify(mockSiteDao, never()).delete(any());
    }

    @Test
    void deleteSite_daoExceptionOnDelete_throwsRuntimeException() {
        siteEntityToDelete = new Site("Te Verwijderen Site", dummyVerantwoordelijkeEntity);
        try {
            java.lang.reflect.Field idField = Site.class.getDeclaredField("siteId");
            idField.setAccessible(true);
            idField.set(siteEntityToDelete, 1);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Kon siteId niet instellen voor test: " + e.getMessage());
        }

        reinitializeSiteControllerWithMockSetup(dao -> {
            when(dao.findAll()).thenReturn(List.of(siteEntityToDelete));
            when(dao.get(siteEntityToDelete.getSiteId())).thenReturn(siteEntityToDelete);
            doThrow(new RuntimeException("DB delete error")).when(dao).delete(siteEntityToDelete);
        });
        assertEquals(1, siteController.getAllSites().size());

        Exception exception = assertThrows(RuntimeException.class, () ->
                siteController.deleteSite(siteEntityToDelete.getSiteId(), dummyIngelogdeGebruikerDto)
        );
        assertEquals("Kon site (id: " + siteEntityToDelete.getSiteId() + ") niet verwijderen: DB delete error", exception.getMessage());
        verify(mockSiteDao, times(2)).startTransaction();
        verify(mockSiteDao).rollbackTransaction();
        verify(mockSiteDao, times(1)).commitTransaction();

        assertFalse(siteController.getAllSites().isEmpty());
    }
}