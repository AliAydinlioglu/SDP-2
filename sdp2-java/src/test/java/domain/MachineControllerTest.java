package domain;

import dto.GebruikerDTO;
import dto.MachineDTO;
import dto.SiteDTO;
import enums.MachineStatus;
import enums.ProductionStatus;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MachineControllerTest {
    private MachineController machineController;
    private GebruikerDTO technieker;
    private SiteDTO.SiteSummaryDTO site;

    @BeforeEach
    void setUp() {
        machineController = new MachineController();
        technieker = new GebruikerDTO(1, "Jan", "Jansen", LocalDate.of(1990, 1, 1), null, "jan@ex.com", "123",
                enums.Rol.TECHNIEKER, true);
        site = new SiteDTO.SiteSummaryDTO(1, "Site1");
    }

    @Test
    void testGetAll() {
        ObservableList<MachineDTO> all = machineController.getAll();
        assertNotNull(all);
        all.forEach(item -> assertInstanceOf(MachineDTO.class, item));
    }

    @Test
    void testChangeFilter() {
        machineController.changeFilter("");
        ObservableList<MachineDTO> all = machineController.getAll();
        assertNotNull(all);
        all.forEach(item -> assertInstanceOf(MachineDTO.class, item));
    }

    @Test
    void testGetMachinesByStatus() {
        ObservableList<MachineDTO> result = machineController.getMachinesByStatus(MachineStatus.DRAAIT);
        assertNotNull(result);
        result.forEach(item -> assertInstanceOf(MachineDTO.class, item));
    }

    @Test
    void testGetMachinesForTechnieker() {
        ObservableList<MachineDTO> result = machineController.getMachinesForTechnieker(1);
        assertNotNull(result);
        result.forEach(item -> assertInstanceOf(MachineDTO.class, item));
    }

    @Test
    void testGetMachinesBySite() {
        ObservableList<MachineDTO> result = machineController.getMachinesBySite(1);
        assertNotNull(result);
        result.forEach(item -> assertInstanceOf(MachineDTO.class, item));
    }

    @Test
    void testValidateMachineStatusThrows() {
        MachineDTO dto = new MachineDTO(1, "n", "i", "l", MachineStatus.DRAAIT, ProductionStatus.IN_ORDE, 0, 0,
                LocalDate.now(), technieker, site);
        assertThrows(IllegalArgumentException.class, () -> machineController.validateMachineStatus(dto));
    }
}
