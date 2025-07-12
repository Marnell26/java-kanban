import controller.FileBackedTaskManager;
import controller.TaskManager;
import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import model.Task;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

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
        Files.deleteIfExists(Path.of("test\\IncorrectFile.csv"));
    }

    @Test
    void saveAndReadFileTest() {
        TaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(taskManager.getAutoSaveFile());
        assertEquals(task1, loadedTaskManager.getTaskById(task1.getId()).orElse(null));
        assertEquals(epic1, loadedTaskManager.getEpicById(epic1.getId()).orElse(null));
        assertEquals(subtask1, loadedTaskManager.getSubtaskById(subtask1.getId()).orElse(null));
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

    @Test
    void shouldThrowExceptionWhenFileDoesNotExist() {
        File file = new File("IncorrectFile.csv");

        assertThrows(ManagerLoadException.class, () -> {
            FileBackedTaskManager.loadFromFile(file);
        }, "Должно выбрасываться исключение, если файл не существует");
    }

    @Test
    void shouldThrowExceptionWhenIncorrectDataFromFile() {
        File file = new File("test\\IncorrectFile.csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write('\uFEFF');
            writer.write("id,type,name,status,description,duration,startTime,epic\n");
            writer.write("IncorrectString");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных" + e.getMessage());
        }

        assertThrows(ManagerLoadException.class, () -> {
            FileBackedTaskManager.loadFromFile(file);
        }, "Должно выбрасываться исключение, если не удается считать строку из файла");
    }

    @Test
    void shouldThrowExceptionWhenIncorrectTaskType() {
        File file = new File("test\\IncorrectFile.csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write('\uFEFF');
            writer.write("id,type,name,status,description,duration,startTime,epic\n");
            writer.write(String.format("%d,IncorrectType,%s,%s,%s,null,null", task1.getId(), task1.getName(),
                    task1.getStatus(), task1.getDescription()));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных" + e.getMessage());
        }

        assertThrows(ManagerLoadException.class, () -> {
            FileBackedTaskManager.loadFromFile(file);
        }, "Должно выбрасываться исключение, если тип задачи некорректный");
    }

}
