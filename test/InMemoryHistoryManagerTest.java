import static org.junit.jupiter.api.Assertions.*;

import controller.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class InMemoryHistoryManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void addTaskToHistory() {

        Task task = new Task("Задача1", "Описание задачи 1");
        taskManager.createTask(task);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой");
        assertEquals(task, history.getFirst());
    }

    @Test
    void historyListShouldNotBeLargerThanTheSpecifiedSize() {
        int maxSize = InMemoryHistoryManager.MAX_SIZE_OF_HISTORY_LIST;
        for (int i = 1; i <= maxSize + 1; i++) {
            Task task = new Task("Задача" + i, "Описание задачи" + i);
            taskManager.createTask(task);
            historyManager.add(task);
        }
        List<Task> history = historyManager.getHistory();
        assertEquals(maxSize, history.size());
        //Проверяем, что при добавлении 11-й задачи в историю 1-я была удалена
        assertEquals(2, history.getFirst().getId());
    }


}
