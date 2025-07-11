import static org.junit.jupiter.api.Assertions.*;

import controller.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void addNewTaskAndGetTaskById() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        taskManager.createTask(task);
        Task savedTask = taskManager.getTaskById(task.getId()).orElse(null);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");
        List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают");
    }

    @Test
    void addNewEpicAndGetEpicById() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic);
        Epic savedEpic = taskManager.getEpicById(epic.getId()).orElse(null);

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic, savedEpic, "Эпики не совпадают");
        List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(1, epics.size());
        assertEquals(epic, epics.getFirst(), "Задачи не совпадают");

    }

    @Test
    void addNewSubtaskAndGetSubtaskById() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", epic.getId());
        taskManager.createSubtask(subtask);
        Subtask savedSubtask = taskManager.getSubtaskById(subtask.getId()).orElse(null);

        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают");
        List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются");
        assertEquals(1, subtasks.size());
        assertEquals(subtask, subtasks.getFirst(), "Задачи не совпадают");
    }


    @Test
    void subTaskCantBeOwnEpic() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", epic.getId());
        taskManager.createSubtask(subtask1);
        //Пытаемся обновить сабтаск, заменив его epicId на Id самого сабтаска
        Subtask updatedSubtask1 = new Subtask(subtask1.getId(), "Подзадача 1", "Описание 1",
                Status.IN_PROGRESS, subtask1.getId());
        taskManager.updateSubtask(updatedSubtask1);
        List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotEquals(subtasks.getFirst().getEpicId(), subtask1.getId(),
                "Объект Subtask получилось сделать своим же эпиком");
    }

    @Test
    void updateTask() {
        Task task = new Task("Задача", "Описание задачи");
        taskManager.createTask(task);
        Task updatedTask = new Task(task.getId(), "Задача", "Описание задачи", Status.IN_PROGRESS);
        taskManager.updateTask(updatedTask);
        Task comparedTask = taskManager.getTasks().getFirst();
        assertEquals(comparedTask.getStatus(), updatedTask.getStatus());
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        Epic updatedEpic = new Epic(epic.getId(), "Эпик", "Измененное описание");
        taskManager.updateEpic(updatedEpic);
        Epic comparedEpic = taskManager.getEpics().getFirst();
        assertEquals(comparedEpic.getDescription(), updatedEpic.getDescription());
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", epic.getId());
        taskManager.createSubtask(subtask);
        Subtask updatedSubtask = new Subtask(subtask.getId(), "Подзадача", "Описание", Status.IN_PROGRESS,
                subtask.getEpicId());
        taskManager.updateSubtask(updatedSubtask);
        Subtask comparedSubtask = taskManager.getSubtasks().getFirst();
        assertEquals(comparedSubtask.getStatus(), updatedSubtask.getStatus());
    }

    @Test
    void shouldGenerateUniqueId() {
        Task task1 = new Task("Задача1", "Описание1");
        taskManager.createTask(task1);
        Task task2 = new Task("Задача2", "Описание2");
        taskManager.createTask(task2);
        System.out.println(taskManager.getTasks());
        assertNotEquals(task1.getId(), task2.getId(), "Id должны быть уникальными");
    }

    @Test
    void testTaskFieldsImmutability() {
        Task task = new Task("Задача1", "Описание1");
        String taskName = task.getName();
        String taskDescription = task.getDescription();
        Status taskStatus = task.getStatus();
        taskManager.createTask(task);

        Task comparedTask = taskManager.getTaskById(task.getId()).orElse(task);

        assertEquals(taskName, comparedTask.getName());
        assertEquals(taskDescription, comparedTask.getDescription());
        assertEquals(taskStatus, comparedTask.getStatus());
    }

    @Test
    void shouldNotHaveIdAfterRemoveTask() {
        Task task = new Task("Задача1", "Описание1");
        taskManager.createTask(task);
        taskManager.deleteTask(task.getId());
        assertTrue(taskManager.getTasks().isEmpty(), "Задача не удалена");
    }

    @Test
    void epicShouldNotHaveDeletedSubtask() {
        Epic epic = new Epic("Эпик1", "Описание1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Описание1", epic.getId());
        taskManager.createSubtask(subtask);
        taskManager.deleteSubtask(subtask.getId());
        assertTrue(taskManager.getSubtasksInEpic(epic.getId()).isEmpty(), "Подзадача не удалена из эпика");
    }

    @Test
    void epicStatusShouldBeNewIfAllSubtasksNew() {
        Epic epic = new Epic("Эпик1", "Описание1");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача1", "Описание1", epic.getId());
        Subtask subtask2 = new Subtask("Подзадача2", "Описание2", epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void epicStatusShouldBeDoneIfAllSubtasksDone() {
        Epic epic = new Epic("Эпик1", "Описание1");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача1", "Описание1", epic.getId());
        Subtask subtask2 = new Subtask("Подзадача2", "Описание2", epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.updateSubtask(new Subtask(subtask1.getId(), "Подзадача1", "Описание1", Status.DONE, epic.getId()));
        taskManager.updateSubtask(new Subtask(subtask2.getId(), "Подзадача2", "Описание2", Status.DONE, epic.getId()));
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void epicStatusShouldBeNewIfSubtasksNewAndDone() {
        Epic epic = new Epic("Эпик1", "Описание1");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача1", "Описание1", epic.getId());
        Subtask subtask2 = new Subtask("Подзадача2", "Описание2", epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.updateSubtask(new Subtask(subtask1.getId(), "Подзадача1", "Описание1", Status.DONE, epic.getId()));
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void epicStatusShouldBeInProgressIfSubtaskInProgress() {
        Epic epic = new Epic("Эпик1", "Описание1");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача1", "Описание1", epic.getId());
        Subtask subtask2 = new Subtask("Подзадача2", "Описание2", epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.updateSubtask(new Subtask(subtask1.getId(), "Подзадача1", "Описание1", Status.IN_PROGRESS,
                epic.getId()));
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void isTasksTimeIntersectionTest() {
        Task task1 = new Task("Задача1", "Описание1", Duration.ofHours(3), LocalDateTime.of(2025, 2, 1, 12, 0));
        taskManager.createTask(task1);
        Epic epic = new Epic("Эпик1", "Описание1");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача1", "Описание1", epic.getId(), Duration.ofHours(2),
                LocalDateTime.of(2025, 2, 1, 17, 0));
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача1", "Описание1", epic.getId(), Duration.ofHours(2),
                LocalDateTime.of(2025, 2, 1, 11, 0));
        taskManager.createSubtask(subtask2);
        //Subtask1 не пересекается с task, subtask2 имеет пересечение с task
        assertNotNull(taskManager.getSubtaskById(subtask1.getId()).orElse(null));
        assertNull(taskManager.getSubtaskById(subtask2.getId()).orElse(null));
    }

    @Test
    void addTaskToPrioritizedTasks() {
        Task task1 = new Task("Задача1", "Описание1", Duration.ofHours(3), LocalDateTime.of(2025, 2, 1, 12, 0));
        Task task2 = new Task("Задача1", "Описание1");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        assertTrue(taskManager.getPrioritizedTasks().contains(task1));
        assertFalse(taskManager.getPrioritizedTasks().contains(task2));
    }

    @Test
    void setEpicTimeProperties() {
        Epic epic = new Epic("Эпик1", "Описание1");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача1", "Описание1", epic.getId(), Duration.ofMinutes(30),
                LocalDateTime.of(2025, 1, 1, 0, 15));
        Subtask subtask2 = new Subtask("Подзадача2", "Описание2", epic.getId(), Duration.ofMinutes(15),
                LocalDateTime.of(2025, 1, 1, 1, 0));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        assertEquals(subtask1.getStartTime(), epic.getStartTime());
        assertEquals(subtask2.getEndTime(), epic.getEndTime());
    }

}