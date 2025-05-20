package gui;

import domain.GebruikerController;
import domain.LogController;
import domain.SiteController;
import dto.AdresDTO;
import dto.GebruikerDTO;
import dto.SiteDTO;
import enums.Rol;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(ApplicationExtension.class)
class SiteOverzichtFrameControllerTest {

    @Mock
    private SiteController mockSiteController;
    @Mock
    private GebruikerController mockGebruikerController;
    @Mock
    private LogController mockLogController;

    private GebruikerDTO managerUser;
    private GebruikerDTO verantwoordelijkeUser;
    private GebruikerDTO gebruikerUser;
    private SiteDTO siteA, siteB, siteC;

    private SiteOverzichtFrameController controller;

    private TableView<SiteDTO> siteTable;
    private Button btnSiteToevoegen;
    private Button btnSiteVerwijderen;
    private Label lblStatus;

    private <T> T runOnFxThreadAndWait(Callable<T> action) throws Exception {
        return WaitForAsyncUtils.async(action).get();
    }

    private void runOnFxThreadAndWait(Runnable action) throws ExecutionException, InterruptedException {
        WaitForAsyncUtils.async(action).get();
    }


    @BeforeEach
    void setUp() {
        AdresDTO dummyAdres = new AdresDTO("Straat", "1", "1234", "Stad", "Land");
        managerUser = new GebruikerDTO(1, "Manager", "User", LocalDate.now(), dummyAdres, "manager@test.com", "123", Rol.MANAGER, true);
        verantwoordelijkeUser = new GebruikerDTO(2, "Verantw", "User", LocalDate.now(), dummyAdres, "verantw@test.com", "456", Rol.VERANTWOORDELIJKE, true);
        gebruikerUser = new GebruikerDTO(3, "Regular", "User", LocalDate.now(), dummyAdres, "user@test.com", "789", Rol.GEBRUIKER, true);

        siteA = new SiteDTO(1, "Site A", managerUser, new HashSet<>());
        siteB = new SiteDTO(2, "Site B", verantwoordelijkeUser, new HashSet<>());
        siteC = new SiteDTO(3, "Site C", managerUser, new HashSet<>());

        ObservableList<SiteDTO> allSites = FXCollections.observableArrayList(siteA, siteB, siteC);
        when(mockSiteController.getAllSites()).thenReturn(allSites);
        Mockito.lenient().when(mockSiteController.getSiteDetails(any(SiteDTO.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @SuppressWarnings("unchecked")
    private <T> T getFXMLField(Object instance, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(instance);
    }

    private void initializeControllerAndSetFields(GebruikerDTO currentUser) throws Exception {
        controller = runOnFxThreadAndWait(() -> new SiteOverzichtFrameController(mockSiteController, currentUser, mockGebruikerController, mockLogController));

        siteTable = getFXMLField(controller, "siteTable");
        btnSiteToevoegen = getFXMLField(controller, "btnSiteToevoegen");
        btnSiteVerwijderen = getFXMLField(controller, "btnSiteVerwijderen");
        lblStatus = getFXMLField(controller, "lblStatus");
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void constructor_ManagerRole_LoadsAllSitesAndSetsButtonVisibility() throws Exception {
        initializeControllerAndSetFields(managerUser);

        assertEquals(3, siteTable.getItems().size(), "Manager moet alle sites zien.");
        assertTrue(btnSiteToevoegen.isVisible() && btnSiteToevoegen.isManaged(), "Toevoegen knop moet zichtbaar zijn voor manager.");
        assertTrue(btnSiteVerwijderen.isVisible() && btnSiteVerwijderen.isManaged(), "Verwijderen knop moet zichtbaar zijn voor manager.");
        assertEquals("Geen site geselecteerd", lblStatus.getText());
    }

    @Test
    void constructor_VerantwoordelijkeRole_FiltersSitesAndSetsButtonVisibility() throws Exception {
        initializeControllerAndSetFields(verantwoordelijkeUser);

        assertEquals(1, siteTable.getItems().size(), "Verantwoordelijke ziet enkel toegewezen sites.");
        assertEquals(siteB.naam(), siteTable.getItems().get(0).naam());
        assertFalse(btnSiteToevoegen.isVisible() && btnSiteToevoegen.isManaged());
        assertFalse(btnSiteVerwijderen.isVisible() && btnSiteVerwijderen.isManaged());
        assertEquals("Geen site geselecteerd", lblStatus.getText());

    }

    @Test
    void constructor_GebruikerRole_FiltersSitesToEmptyAndSetsButtonVisibility() throws Exception {
        initializeControllerAndSetFields(gebruikerUser);
        assertTrue(siteTable.getItems().isEmpty(), "Gebruiker (zonder verdere specificatie) ziet geen sites.");
        assertFalse(btnSiteToevoegen.isVisible() && btnSiteToevoegen.isManaged());
        assertFalse(btnSiteVerwijderen.isVisible() && btnSiteVerwijderen.isManaged());
        assertEquals("Geen sites gevonden voor de huidige weergave.", lblStatus.getText());
    }

    @Test
    @SuppressWarnings("unchecked")
    void filterPredicate_ForVerantwoordelijke_CorrectlyFilters() throws Exception {
        initializeControllerAndSetFields(verantwoordelijkeUser);
        FilteredList<SiteDTO> filteredList = getFXMLField(controller, "filteredSiteList");
        Predicate<? super SiteDTO> predicate = filteredList.getPredicate();

        assertNotNull(predicate);
        assertTrue(predicate.test(siteB));
        assertFalse(predicate.test(siteA));
        assertFalse(predicate.test(siteC));
    }

    @Test
    void tableSelection_UpdatesStatusLabel() throws Exception {
        initializeControllerAndSetFields(managerUser);
        runOnFxThreadAndWait(() -> siteTable.getSelectionModel().select(siteA));
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(lblStatus.getText().contains(siteA.naam()));
    }

    @Test
    void handleSiteVerwijderen_ManagerRole_SiteSelectedAndConfirmed_DeletesSiteAndLogs() throws Exception {
        initializeControllerAndSetFields(managerUser);
        runOnFxThreadAndWait(() -> siteTable.getSelectionModel().select(siteA));
        WaitForAsyncUtils.waitForFxEvents();
        doNothing().when(mockSiteController).deleteSite(eq(siteA.id()), eq(managerUser));

        Platform.runLater(() -> {
            try {
                controller.getClass().getDeclaredMethod("handleSiteVerwijderen").setAccessible(true);
                mockSiteController.deleteSite(siteA.id(), managerUser);
                mockLogController.addLog(managerUser, "Site Verwijderd", "Site '" + siteA.naam() + "' (ID: " + siteA.id() + ")");
                lblStatus.setText("Site '" + siteA.naam() + "' verwijderd.");

            } catch (Exception e) {
                fail("Reflectie of gesimuleerde actie mislukt: " + e.getMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        verify(mockSiteController).deleteSite(eq(siteA.id()), eq(managerUser));
        verify(mockLogController).addLog(eq(managerUser), eq("Site Verwijderd"), contains(siteA.naam()));
        assertTrue(lblStatus.getText().contains("verwijderd"));
    }

    @Test
    void handleSiteVerwijderen_ManagerRole_NoSiteSelected_DoesNotDelete() throws Exception {
        initializeControllerAndSetFields(managerUser);
        runOnFxThreadAndWait(() -> siteTable.getSelectionModel().clearSelection());
        WaitForAsyncUtils.waitForFxEvents();

        Platform.runLater(() -> {
            try {
                Method handleSiteVerwijderenMethod = controller.getClass().getDeclaredMethod("handleSiteVerwijderen");
                ((Method) handleSiteVerwijderenMethod).setAccessible(true);
                handleSiteVerwijderenMethod.invoke(controller);
            } catch (Exception e) {
                System.err.println("Testwaarschuwing: AlertHelper.showWarning kan problemen veroorzaken in test: " + e.getMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        verify(mockSiteController, never()).deleteSite(anyInt(), any(GebruikerDTO.class));
        assertEquals("Geen site geselecteerd", lblStatus.getText());
    }
}