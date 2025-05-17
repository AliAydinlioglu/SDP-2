package domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dto.GebruikerDTO;
import dto.LogDTO;
import enums.Rol;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import repository.LogDao;

@ExtendWith(MockitoExtension.class)
public class LogControllerTest {

    @Mock
    private LogDao logRepo;

    @Mock
    private GebruikerController gebruikerController;

    @InjectMocks
    private LogController logController;

    private static final Gebruiker VALID_GEBRUIKER = new Gebruiker(
            "Voornaam", "Achternaam", LocalDate.of(2000, 1, 1),
            new Adres("Straat", "1", "1234AB", "Stad", "Land"),
            "test@test.com", "wachtwoord", Rol.GEBRUIKER, true
    );

    private static final Log VALID_LOG = new Log(VALID_GEBRUIKER, "Login", "Opmerking");

    private static final GebruikerDTO VALID_GEBRUIKER_DTO = GebruikerDTO.fromEntity(VALID_GEBRUIKER);
    private static final LogDTO VALID_LOG_DTO = LogDTO.fromEntity(VALID_LOG);


    @Test
    public void initData_ShouldInitializeLogList() {
		when(logRepo.findAll()).thenReturn(Arrays.asList(VALID_LOG));

		assertEquals(Arrays.asList(VALID_LOG_DTO), logController.getAll());
	}
    @Test
    public void getLog_ShouldReturnCorrectLogDTO() {
        when(logRepo.get(1)).thenReturn(VALID_LOG);

        LogDTO result = logController.getLog(1);

        assertEquals(VALID_LOG_DTO, result);
        verify(logRepo).get(1);
    }

    @Test
    public void getAll_ShouldReturnObservableListOfLogs() {
        ObservableList<LogDTO> result = logController.getAll();

        assertEquals(1, result.size());
        assertEquals(VALID_LOG_DTO, result.get(0));
    }

    @Test
    public void addLog_ShouldInsertLog_WhenValid() {
        when(gebruikerController.getRealGebruiker(VALID_GEBRUIKER.getGebruikerID())).thenReturn(VALID_GEBRUIKER);

        logController = new LogController(logRepo); // Re-init with mocked repo
        logController.addLog(VALID_GEBRUIKER_DTO, "Login", "Ingelogd");

        verify(logRepo).startTransaction();
        verify(logRepo).insert(any(Log.class));
        verify(logRepo).commitTransaction();
    }

    @Test
    public void addLog_ShouldRollbackTransaction_WhenExceptionOccurs() {
        when(gebruikerController.getRealGebruiker(VALID_GEBRUIKER.getGebruikerID())).thenReturn(VALID_GEBRUIKER);
        doThrow(new RuntimeException("DB error")).when(logRepo).insert(any(Log.class));

        logController = new LogController(logRepo);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            logController.addLog(VALID_GEBRUIKER_DTO, "Actie", "Fout");
        });

        assertEquals("DB error", exception.getMessage());
        verify(logRepo).rollbackTransaction();
    }
}
