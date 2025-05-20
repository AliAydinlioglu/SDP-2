package gui;

import domain.*;
import dto.*;
import enums.MachineStatus;
import enums.OnderhoudStatus;
import enums.ProductionStatus;
import enums.Rol;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(ApplicationExtension.class)
class SiteDetailFrameControllerTest {

    @Mock private SiteController mockSiteController;
    @Mock private OnderhoudController mockOnderhoudController;
    @Mock private LogController mockLogController;

    private SiteDTO selectedSite;
    private GebruikerDTO ingelogdeGebruikerDto;

    private MachineDTO machine1DTO, machine2DTO;
    private OnderhoudDTO onderhoudM1DTO;

    private SiteDetailFrameController siteDetailControllerInstance;
    private MainFrameController mainFrameControllerInstance;
    private Stage primaryStage;

    private Label lblSiteNaam;
    private Label lblVerantwoordelijke;
    private Label lblAantalMachines;
    private TableView<MachineDTO> machineTable;
    private Label lblMachineNaam;
    private Label lblMachineCode;
    private Label lblLaatsteOnderhoud;
    private Label lblAantalDagenOnderhoud;
    private Label lblDatumToekomstigOnderhoud;
    private Button btnTerug;
    private Button btnOnderhoud;

    @Start
    public void start(Stage stage) {
        this.primaryStage = stage;
    }

    @BeforeEach
    void setUpDtoData() {
        AdresDTO dummyAdresDto = new AdresDTO("Teststraat", "1", "1234", "Teststad", "Testland");
        ingelogdeGebruikerDto = new GebruikerDTO(5, "TestManager", "User", LocalDate.now().minusYears(30), dummyAdresDto, "manager@test.com", "123", Rol.MANAGER, true);
        GebruikerDTO verantwoordelijkeDto = new GebruikerDTO(2, "SiteVerantw", "Persoon", LocalDate.now().minusYears(40), dummyAdresDto, "verantw@test.com", "456", Rol.VERANTWOORDELIJKE, true);
        SiteDTO.SiteSummaryDTO siteSummary = new SiteDTO.SiteSummaryDTO(1, "Hoofdsite");

        machine1DTO = new MachineDTO(101, "Machine Alpha", "Info Alpha", "Hal A", MachineStatus.DRAAIT, ProductionStatus.IN_ORDE, 95, 30, LocalDate.now().plusMonths(2), ingelogdeGebruikerDto, siteSummary);
        machine2DTO = new MachineDTO(102, "Machine Beta", "Info Beta", "Hal B", MachineStatus.GESTOPT_MANUEEL, ProductionStatus.NOOD_AAN_ONDERHOUD, 70, 15, LocalDate.now().plusDays(10), ingelogdeGebruikerDto, siteSummary);
        Set<MachineDTO> machines = new HashSet<>();
        machines.add(machine1DTO);
        machines.add(machine2DTO);

        selectedSite = new SiteDTO(1, "Hoofdsite", verantwoordelijkeDto, machines);
        onderhoudM1DTO = new OnderhoudDTO(201, LocalDate.now().minusDays(machine1DTO.dagenSindsOnderhoud()), LocalTime.NOON, LocalTime.MIDNIGHT, "Reden", "Rapport", "Opmerkingen", OnderhoudStatus.VOLTOOID, machine1DTO, ingelogdeGebruikerDto);

        Mockito.lenient().when(mockOnderhoudController.getLaatsteVoltooideOnderhoudVanMachine(machine1DTO.id())).thenReturn(onderhoudM1DTO);
        Mockito.lenient().when(mockOnderhoudController.getLaatsteVoltooideOnderhoudVanMachine(machine2DTO.id())).thenReturn(null);
    }

    @SuppressWarnings("unchecked")
    private <T> T getFXMLField(Object instance, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(instance);
    }

    private void initializeSceneWithController(FxRobot robot) throws Exception {
        siteDetailControllerInstance = WaitForAsyncUtils.asyncFx(() -> {
            SiteDetailFrameController ctrl = new SiteDetailFrameController(
                    mockSiteController, selectedSite, ingelogdeGebruikerDto, mockLogController
            );
            try {
                Field ocField = SiteDetailFrameController.class.getDeclaredField("onderhoudController");
                ocField.setAccessible(true);
                ocField.set(ctrl, mockOnderhoudController);
            } catch (Exception e) {
                throw new RuntimeException("Kon mock OnderhoudController niet injecteren in SDFC", e);
            }
            return ctrl;
        }).get();

        mainFrameControllerInstance = WaitForAsyncUtils.asyncFx(
                () -> new MainFrameController(ingelogdeGebruikerDto, primaryStage)
        ).get();

        robot.interact(() -> {
            StackPane mainViewPane = mainFrameControllerInstance.getMainView();
            mainViewPane.getChildren().setAll(siteDetailControllerInstance);

            Scene scene = new Scene(mainFrameControllerInstance);
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.toFront();
        });
        WaitForAsyncUtils.waitForFxEvents();
        lblSiteNaam = getFXMLField(siteDetailControllerInstance, "lblSiteNaam");
        lblVerantwoordelijke = getFXMLField(siteDetailControllerInstance, "lblVerantwoordelijke");
        lblAantalMachines = getFXMLField(siteDetailControllerInstance, "lblAantalMachines");
        machineTable = getFXMLField(siteDetailControllerInstance, "machineTable");
        lblMachineNaam = getFXMLField(siteDetailControllerInstance, "lblMachineNaam");
        lblMachineCode = getFXMLField(siteDetailControllerInstance, "lblMachineCode");
        lblLaatsteOnderhoud = getFXMLField(siteDetailControllerInstance, "lblLaatsteOnderhoud");
        lblAantalDagenOnderhoud = getFXMLField(siteDetailControllerInstance, "lblAantalDagenOnderhoud");
        lblDatumToekomstigOnderhoud = getFXMLField(siteDetailControllerInstance, "lblDatumToekomstigOnderhoud");
        btnTerug = getFXMLField(siteDetailControllerInstance, "btnTerug");
        btnOnderhoud = getFXMLField(siteDetailControllerInstance, "btnOnderhoud");
    }

    @Test
    void initializeSiteDetails_PopulatesCorrectly(FxRobot robot) throws Exception {
        initializeSceneWithController(robot);
        assertEquals(selectedSite.naam(), lblSiteNaam.getText());
        String expectedVerantw = selectedSite.verantwoordelijke().voornaam() + " " + selectedSite.verantwoordelijke().achternaam();
        assertEquals(expectedVerantw, lblVerantwoordelijke.getText());
        assertEquals(String.valueOf(selectedSite.machines().size()), lblAantalMachines.getText());
        ObservableList<MachineDTO> tableItems = machineTable.getItems();
        assertNotNull(tableItems);
        assertEquals(selectedSite.machines().size(), tableItems.size());
        assertTrue(tableItems.stream().map(MachineDTO::id).collect(Collectors.toSet())
                .containsAll(selectedSite.machines().stream().map(MachineDTO::id).collect(Collectors.toSet())));
    }

    @Test
    void machineTableSelection_UpdatesMachineDetailsAndOnderhoud(FxRobot robot) throws Exception {
        initializeSceneWithController(robot);
        robot.interact(() -> machineTable.getSelectionModel().select(machine1DTO));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(machine1DTO.naam(), lblMachineNaam.getText());
        assertEquals(String.valueOf(machine1DTO.id()), lblMachineCode.getText());
        verify(mockOnderhoudController).getLaatsteVoltooideOnderhoudVanMachine(machine1DTO.id());
        assertEquals(onderhoudM1DTO.datum().toString(), lblLaatsteOnderhoud.getText());
        long expectedDagen = ChronoUnit.DAYS.between(onderhoudM1DTO.datum(), LocalDate.now());
        assertEquals(String.valueOf(expectedDagen), lblAantalDagenOnderhoud.getText());
        assertEquals(machine1DTO.volgendOnderhoud().toString(), lblDatumToekomstigOnderhoud.getText());

        robot.interact(() -> machineTable.getSelectionModel().select(machine2DTO));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(machine2DTO.naam(), lblMachineNaam.getText());
        assertEquals(String.valueOf(machine2DTO.id()), lblMachineCode.getText());
        verify(mockOnderhoudController).getLaatsteVoltooideOnderhoudVanMachine(machine2DTO.id());
        assertEquals("Geen onderhoud gevonden", lblLaatsteOnderhoud.getText());
        assertEquals("N/A", lblAantalDagenOnderhoud.getText());
        assertEquals(machine2DTO.volgendOnderhoud().toString(), lblDatumToekomstigOnderhoud.getText());
    }

    @Test
    void machineTableSelection_NoPreviousOnderhoud_DisplaysCorrectly(FxRobot robot) throws Exception {
        initializeSceneWithController(robot);
        robot.interact(() -> machineTable.getSelectionModel().select(machine2DTO));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(machine2DTO.naam(), lblMachineNaam.getText());
        assertEquals("Geen onderhoud gevonden", lblLaatsteOnderhoud.getText());
        assertEquals("N/A", lblAantalDagenOnderhoud.getText());
    }

    @Test
    void btnTerug_Action_NavigatesBack(FxRobot robot) throws Exception {
        initializeSceneWithController(robot);
        assertNotNull(btnTerug.getOnAction());

        robot.clickOn(btnTerug);
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(primaryStage.getScene().getRoot() instanceof MainFrameController, "Scene root moet een MainFrameController zijn na 'terug'.");
        MainFrameController activeMainFrame = (MainFrameController) primaryStage.getScene().getRoot();

        StackPane mainViewOfActiveMainFrame = activeMainFrame.getMainView();
        assertNotNull(mainViewOfActiveMainFrame, "MainView van de actieve MainFrameController mag niet null zijn.");
        assertFalse(mainViewOfActiveMainFrame.getChildren().isEmpty(), "MainView van actieve MainFrameController moet content hebben.");

        assertTrue(mainViewOfActiveMainFrame.getChildren().get(0) instanceof SiteOverzichtFrameController, "Navigatie naar SiteOverzichtFrame mislukt in de nieuwe MainFrameController.");
    }

    @Test
    void btnOnderhoud_Action_NavigatesToOnderhoudFrame(FxRobot robot) throws Exception {
        initializeSceneWithController(robot);
        assertNotNull(btnOnderhoud.getOnAction());

        robot.clickOn(btnOnderhoud);
        WaitForAsyncUtils.waitForFxEvents();

        StackPane mainView = mainFrameControllerInstance.getMainView();
        assertNotNull(mainView, "MainView van de oorspronkelijke MainFrameController mag niet null zijn.");
        assertFalse(mainView.getChildren().isEmpty(), "MainView moet content hebben na navigatie naar onderhoud.");
        assertTrue(mainView.getChildren().get(0) instanceof OnderhoudFrameController, "Navigatie naar OnderhoudFrame mislukt.");
    }
}