import controller.FileBackedTaskManager;
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

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    FileBackedTaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = new FileBackedTaskManager(new File("test\\test.csv"));
    }

    @AfterAll
    static void afterAll() throws IOException {
        Files.deleteIfExists(Path.of("test\\test.csv"));
    }

    @Test
    void saveAndReadFileTest() {
        Task task = new Task("Задача1", "Описание1");
        taskManager.createTask(task);
        Epic epic = new Epic("Эпик1", "Описание1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Описание1", epic.getId());
        taskManager.createSubtask(subtask);
        FileBackedTaskManager.loadFromFile(taskManager.getAutoSaveFile());
        assertEquals(task, taskManager.getTaskById(task.getId()));
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void idMustBeStartWithNumberFromFile() {
        Task task1 = new Task("Задача1", "Описание1");
        taskManager.createTask(task1);
        FileBackedTaskManager.loadFromFile(taskManager.getAutoSaveFile());
        Task task2 = new Task("Задача1", "Описание1");
        taskManager.createTask(task2);
        assertEquals(2, task2.getId());
    }

    @Test
    void taskMustBeRemovedFromFileIfDelete() {
        Task task1 = new Task("Задача1", "Описание1");
        taskManager.createTask(task1);
        Task task2 = new Task("Задача2", "Описание2");
        taskManager.createTask(task2);
        taskManager.deleteTask(task1.getId());
        FileBackedTaskManager.loadFromFile(taskManager.getAutoSaveFile());
        assertNull(taskManager.getTaskById(task1.getId()));
    }

    @Test
    void allTasksMustBeRemovedFromFileIfClear() {
        Task task1 = new Task("Задача1", "Описание1");
        taskManager.createTask(task1);
        Task task2 = new Task("Задача2", "Описание2");
        taskManager.createTask(task2);
        taskManager.clearTasks();
        FileBackedTaskManager.loadFromFile(taskManager.getAutoSaveFile());
        assertEquals(0, taskManager.getTasks().size());
    }

}
