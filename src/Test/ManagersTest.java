package Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import controller.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ManagersTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;


    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void managersShouldReturnNonNullTaskManager() {
        assertNotNull(taskManager);
    }

    @Test
    public void managersShouldReturnNonNullHistoryManager() {
        assertNotNull(historyManager);
    }

}
