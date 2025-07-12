import controller.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task1;
    protected Epic epic1;
    protected Subtask subtask1;

    @BeforeEach
    protected void beforeEach() {
        task1 = new Task("Задача1", "Описание1", Duration.ofHours(1), LocalDateTime.of(2025, 1, 1, 0, 0));
        taskManager.createTask(task1);
        epic1 = new Epic("Эпик1", "Описание1");
        taskManager.createEpic(epic1);
        subtask1 = new Subtask("Подзадача1", "Описание1", epic1.getId(), Duration.ofHours(1),
                LocalDateTime.of(2025, 1, 2, 0, 0));
        taskManager.createSubtask(subtask1);
    }

    @Test
    void addNewTaskAndGetTaskById() {
        Task savedTask = taskManager.getTaskById(task1.getId()).orElse(null);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task1, savedTask, "Задачи не совпадают");
        List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size());
        assertEquals(task1, tasks.getFirst(), "Задачи не совпадают");
    }

    @Test
    void addNewEpicAndGetEpicById() {
        Epic savedEpic = taskManager.getEpicById(epic1.getId()).orElse(null);

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic1, savedEpic, "Эпики не совпадают");
        List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(1, epics.size());
        assertEquals(epic1, epics.getFirst(), "Задачи не совпадают");

    }

    @Test
    void addNewSubtaskAndGetSubtaskById() {
        Subtask savedSubtask = taskManager.getSubtaskById(subtask1.getId()).orElse(null);

        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(subtask1, savedSubtask, "Подзадачи не совпадают");
        List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются");
        assertEquals(1, subtasks.size());
        assertEquals(subtask1, subtasks.getFirst(), "Задачи не совпадают");
    }

    @Test
    void subTaskCantBeOwnEpic() {
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
        Task updatedTask = new Task(task1.getId(), "Задача", "Описание задачи", Status.IN_PROGRESS);
        taskManager.updateTask(updatedTask);
        Task comparedTask = taskManager.getTasks().getFirst();
        assertEquals(comparedTask.getStatus(), updatedTask.getStatus());
    }

    @Test
    void updateEpic() {
        Epic updatedEpic = new Epic(epic1.getId(), "Эпик", "Измененное описание");
        taskManager.updateEpic(updatedEpic);
        Epic comparedEpic = taskManager.getEpics().getFirst();
        assertEquals(comparedEpic.getDescription(), updatedEpic.getDescription());
    }

    @Test
    void updateSubtask() {
        Subtask updatedSubtask = new Subtask(subtask1.getId(), "Подзадача", "Описание", Status.IN_PROGRESS,
                subtask1.getEpicId());
        taskManager.updateSubtask(updatedSubtask);
        Subtask comparedSubtask = taskManager.getSubtasks().getFirst();
        assertEquals(comparedSubtask.getStatus(), updatedSubtask.getStatus());
    }

    @Test
    void shouldGenerateUniqueId() {
        Task task2 = new Task("Задача2", "Описание2");
        taskManager.createTask(task2);
        System.out.println(taskManager.getTasks());
        assertNotEquals(task1.getId(), task2.getId(), "Id должны быть уникальными");
    }

    @Test
    void testTaskFieldsImmutability() {
        String taskName = task1.getName();
        String taskDescription = task1.getDescription();
        Status taskStatus = task1.getStatus();
        taskManager.createTask(task1);

        Task comparedTask = taskManager.getTaskById(task1.getId()).orElse(task1);

        assertEquals(taskName, comparedTask.getName());
        assertEquals(taskDescription, comparedTask.getDescription());
        assertEquals(taskStatus, comparedTask.getStatus());
    }

    @Test
    void shouldNotHaveIdAfterRemoveTask() {
        taskManager.deleteTask(task1.getId());
        assertTrue(taskManager.getTasks().isEmpty(), "Задача не удалена");
    }

    @Test
    void epicShouldNotHaveDeletedSubtask() {
        taskManager.deleteSubtask(subtask1.getId());
        assertTrue(taskManager.getSubtasksInEpic(epic1.getId()).isEmpty(), "Подзадача не удалена из эпика");
    }

    @Test
    void epicStatusShouldBeNewIfAllSubtasksNew() {
        Subtask subtask2 = new Subtask("Подзадача2", "Описание2", epic1.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        assertEquals(Status.NEW, epic1.getStatus());
    }

    @Test
    void epicStatusShouldBeDoneIfAllSubtasksDone() {
        Subtask subtask2 = new Subtask("Подзадача2", "Описание2", epic1.getId());
        taskManager.createSubtask(subtask2);
        taskManager.updateSubtask(new Subtask(subtask1.getId(), "Подзадача1", "Описание1", Status.DONE, epic1.getId()));
        taskManager.updateSubtask(new Subtask(subtask2.getId(), "Подзадача2", "Описание2", Status.DONE, epic1.getId()));
        assertEquals(Status.DONE, epic1.getStatus());
    }

    @Test
    void epicStatusShouldBeNewIfSubtasksNewAndDone() {
        Subtask subtask2 = new Subtask("Подзадача2", "Описание2", epic1.getId());
        taskManager.createSubtask(subtask2);
        taskManager.updateSubtask(new Subtask(subtask1.getId(), "Подзадача1", "Описание1", Status.DONE, epic1.getId()));
        assertEquals(Status.NEW, epic1.getStatus());
    }

    @Test
    void epicStatusShouldBeInProgressIfSubtaskInProgress() {
        Subtask subtask2 = new Subtask("Подзадача2", "Описание2", epic1.getId());
        taskManager.createSubtask(subtask2);
        taskManager.updateSubtask(new Subtask(subtask1.getId(), "Подзадача1", "Описание1", Status.IN_PROGRESS,
                epic1.getId()));
        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }

}
