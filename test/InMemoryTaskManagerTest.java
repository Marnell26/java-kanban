import controller.InMemoryTaskManager;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    @BeforeEach
    protected void beforeEach() {
        taskManager = new InMemoryTaskManager();
        super.beforeEach();
    }

    @Test
    void isTasksTimeIntersectionTest() {
        Subtask subtask2 = new Subtask("Подзадача1", "Описание1", epic1.getId(), Duration.ofHours(1),
                LocalDateTime.of(2025, 1, 1, 0, 30));
        taskManager.createSubtask(subtask2);
        //subtask1 не пересекается с task1, subtask2 имеет пересечение с task
        assertNotNull(taskManager.getSubtaskById(subtask1.getId()).orElse(null));
        assertNull(taskManager.getSubtaskById(subtask2.getId()).orElse(null));
    }

    @Test
    void addTaskToPrioritizedTasks() {
        Task task2 = new Task("Задача1", "Описание1");
        taskManager.createTask(task2);
        assertTrue(taskManager.getPrioritizedTasks().contains(task1));
        assertFalse(taskManager.getPrioritizedTasks().contains(task2));
    }

    @Test
    void setEpicTimeProperties() {
        Subtask subtask2 = new Subtask("Подзадача2", "Описание2", epic1.getId(), Duration.ofMinutes(15),
                LocalDateTime.of(2025, 1, 2, 2, 0));
        taskManager.createSubtask(subtask2);
        assertEquals(subtask1.getStartTime(), epic1.getStartTime());
        assertEquals(subtask2.getEndTime(), epic1.getEndTime());
    }

}