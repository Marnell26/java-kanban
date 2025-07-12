import controller.FileBackedTaskManager;
import controller.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Override
    @BeforeEach
    protected void beforeEach() {
        taskManager = new FileBackedTaskManager(new File("test\\test.csv"));
        super.beforeEach();
    }

    @AfterAll
    static void afterAll() throws IOException {
        Files.deleteIfExists(Path.of("test\\test.csv"));
    }

    @Test
    void saveAndReadFileTest() {
        Epic epic = new Epic("Эпик1", "Описание1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Описание1", epic.getId());
        taskManager.createSubtask(subtask);
        TaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(taskManager.getAutoSaveFile());
        assertEquals(task1, loadedTaskManager.getTaskById(task1.getId()).orElse(null));
        assertEquals(epic, loadedTaskManager.getEpicById(epic.getId()).orElse(null));
        assertEquals(subtask, loadedTaskManager.getSubtaskById(subtask.getId()).orElse(null));
    }

    @Test
    void idMustBeStartWithNumberFromFile() {
        TaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(taskManager.getAutoSaveFile());
        Task task2 = new Task("Задача1", "Описание1");
        loadedTaskManager.createTask(task2);
        assertEquals(4, task2.getId());
    }

    @Test
    void taskMustBeRemovedFromFileIfDelete() {
        Task task2 = new Task("Задача2", "Описание2");
        taskManager.createTask(task2);
        taskManager.deleteTask(task1.getId());
        TaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(taskManager.getAutoSaveFile());
        assertNull(loadedTaskManager.getTaskById(task1.getId()).orElse(null));
    }

    @Test
    void allTasksMustBeRemovedFromFileIfClear() {
        Task task2 = new Task("Задача2", "Описание2");
        taskManager.createTask(task2);
        taskManager.clearTasks();
        TaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(taskManager.getAutoSaveFile());
        assertEquals(0, loadedTaskManager.getTasks().size());
    }

}
