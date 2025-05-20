package domain;

import dto.GebruikerDTO;
import dto.LogDTO;
import enums.Rol;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.GebruikerDao;
import repository.LogDao;
import repository.LogDaoJpa;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogControllerTest {

    @Mock
    private LogDao mockLogDao;

    @Mock
    private GebruikerDao gebruikerRepo;

    private LogController logController;

    private GebruikerController gebruikerController;

    private GebruikerDTO dummyGebruiker;

    private static final Gebruiker VALID_GEBRUIKER = new Gebruiker(
            "Voornaam", "Achternaam", LocalDate.of(2000, 1, 1),
            new Adres("Straat", "1", "1234AB", "Stad", "Land"),
            "test@test.com", "wachtwoord", Rol.GEBRUIKER, true
    );

    @BeforeEach
    void setUp() {
        dummyGebruiker = GebruikerDTO.fromEntity(VALID_GEBRUIKER);
        gebruikerController = new GebruikerController(gebruikerRepo);
        logController = new LogController(mockLogDao, gebruikerController);
    }

    @Test
    void constructor_initializesAndLoadsLogs() {
        List<Log> dummyLogs = List.of(new Log(VALID_GEBRUIKER, "Action", "Detail"));
        when(mockLogDao.findAll()).thenReturn(dummyLogs);

        logController = new LogController(mockLogDao, gebruikerController);
        ObservableList<LogDTO> logs = logController.getAll();

        assertEquals(1, logs.size());
        assertEquals("Action", logs.get(0).actie());
    }

    @Test
    void getAllLogs_returnsCorrectLogs() {
        Log log1 = new Log(VALID_GEBRUIKER, "Ingelogd", "Login detail");
        when(mockLogDao.findAll()).thenReturn(List.of(log1));

        logController = new LogController(mockLogDao, gebruikerController);
        List<LogDTO> logs = logController.getAll();

        assertEquals(1, logs.size());
        assertEquals("Ingelogd", logs.get(0).actie());
        assertEquals("test@test.com", logs.get(0).gebruiker().email());
    }

    @Test
    void addLog_validArguments_createsAndPersistsLog() {
        ArgumentCaptor<Log> logCaptor = ArgumentCaptor.forClass(Log.class);
        doNothing().when(mockLogDao).startTransaction();
        doNothing().when(mockLogDao).insert(any(Log.class));
        doNothing().when(mockLogDao).commitTransaction();
        when(gebruikerRepo.get(0)).thenReturn(VALID_GEBRUIKER);

        dummyGebruiker = GebruikerDTO.fromEntity(VALID_GEBRUIKER);
        logController.addLog(dummyGebruiker, "Site aangemaakt", "Details over de actie");

        verify(mockLogDao).startTransaction();
        verify(mockLogDao).insert(logCaptor.capture());
        verify(mockLogDao).commitTransaction();

        Log createdLog = logCaptor.getValue();
        assertEquals("test@test.com", createdLog.getGebruiker().getEmail());
        assertEquals("Site aangemaakt", createdLog.getActie());
        assertEquals("Details over de actie", createdLog.getOpmerking());
    }

    @Test
    void addLog_nullGebruiker_throwsException() {
        assertThrows(NullPointerException.class, () ->
                logController.addLog(null, "Actie", "Detail")
        );
        verify(mockLogDao, never()).insert(any());
    }

    @Test
    void addLog_emptyAction_throwsException() {
    	when(gebruikerRepo.get(0)).thenReturn(VALID_GEBRUIKER);
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                logController.addLog(dummyGebruiker, "  ", "Detail")
        );
        assertEquals("Actie mag niet leeg zijn", ex.getMessage());
        verify(mockLogDao, never()).insert(any());
    }

    @Test
    void addLog_daoThrowsException_rollsBackTransaction() {
        doNothing().when(mockLogDao).startTransaction();
        doThrow(new RuntimeException("insert fail")).when(mockLogDao).insert(any(Log.class));
        doNothing().when(mockLogDao).rollbackTransaction();
        when(gebruikerRepo.get(0)).thenReturn(VALID_GEBRUIKER);

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                logController.addLog(dummyGebruiker, "Actie", "Detail")
        );
        assertTrue(ex.getMessage().contains("insert fail"));

        verify(mockLogDao).startTransaction();
        verify(mockLogDao).rollbackTransaction();
        verify(mockLogDao, never()).commitTransaction();
    }

    @Test
    void addLog_transactionRollbackOnAnyFailure() {
        doNothing().when(mockLogDao).startTransaction();
        doThrow(new RuntimeException("insert fail")).when(mockLogDao).insert(any(Log.class));
        doNothing().when(mockLogDao).rollbackTransaction();
        when(gebruikerRepo.get(0)).thenReturn(VALID_GEBRUIKER);

        assertThrows(IllegalArgumentException.class, () ->
                logController.addLog(dummyGebruiker, "Crash", "Boom")
        );

        verify(mockLogDao).rollbackTransaction();
    }

    @Test
    void getAllLogs_returnsEmptyListWhenNoLogs() {
        when(mockLogDao.findAll()).thenReturn(Collections.emptyList());
        logController = new LogController(mockLogDao, gebruikerController);
        List<LogDTO> result = logController.getAll();
        assertTrue(result.isEmpty());
    }
}
