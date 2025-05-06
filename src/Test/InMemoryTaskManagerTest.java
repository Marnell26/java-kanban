package Test;

import static org.junit.jupiter.api.Assertions.*;

import controller.*;
import model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

class InMemoryTaskManagerTest {
    TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    void addNewTask() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        taskManager.createTask(task);

        Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");

        List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    void addTaskToHistory() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }



}