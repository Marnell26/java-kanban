import controller.FileBackedTaskManager;
import controller.Managers;
import controller.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        TaskManager taskManager =  Managers.getDefault();
        //Тест добавления задач
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        System.out.println("Список задач: ");
        System.out.println(taskManager.getTasks());
        System.out.println("_".repeat(50));

        //Тест добавления эпиков и подзадач
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic2.getId());
        Subtask subtask4 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic2.getId());
        taskManager.createSubtask(subtask3);
        taskManager.createSubtask(subtask4);

        System.out.println("Список эпиков:");
        System.out.println(taskManager.getEpics());
        System.out.println("_".repeat(50));
        System.out.println("Список подзадач:");
        System.out.println(taskManager.getSubtasks());
        System.out.println("_".repeat(50));
        System.out.println("Подзадачи эпика1:");
        System.out.println(taskManager.getSubtasksInEpic(epic1.getId()));
        System.out.println("_".repeat(50));

        //Тест сохранения в файл
        TaskManager fileManager = FileBackedTaskManager.loadFromFile(new File("C:\\Users\\gorobenkoes\\Documents\\123.csv"));
        Task task01 = new Task("Задача 1", "Описание задачи 1");
        Epic epic01 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic02 = new Epic("Эпик 2", "Описание эпика 2");
        Subtask subtask01 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic01.getId());
        Subtask subtask02 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic01.getId());
        Subtask subtask03 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic02.getId());
        fileManager.createTask(task01);
        fileManager.createEpic(epic01);
        fileManager.createEpic(epic02);
        fileManager.createSubtask(subtask01);
        fileManager.createSubtask(subtask02);
        fileManager.createSubtask(subtask03);

        //Тест получения задачи/эпика/подзадачи по id
        System.out.println(taskManager.getTaskById(task1.getId()));
        System.out.println(taskManager.getEpicById(epic1.getId()));
        System.out.println(taskManager.getSubtaskById(subtask4.getId()));
        System.out.println("_".repeat(50));

        //Тест обновления задачи
        taskManager.updateTask(new Task(task1.getId(), task1.getName(), task1.getDescription(), Status.IN_PROGRESS));
        System.out.println("Задача после обновления: ");
        System.out.println(taskManager.getTaskById(task1.getId()));
        System.out.println("_".repeat(50));

        //Тест обновления эпика
        taskManager.updateEpic(new Epic(epic1.getId(), epic1.getName(), "Обновленное описание эпика 2"));
        System.out.println("Эпик после обновления: ");
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getEpicById(epic1.getId()));
        System.out.println("_".repeat(50));

        //Тест обновления подзадачи и статуса эпика
        taskManager.updateSubtask(new Subtask(subtask3.getId(), subtask3.getName(), subtask3.getDescription(),
                Status.IN_PROGRESS, subtask3.getEpicId()));
        taskManager.updateSubtask(new Subtask(subtask4.getId(), subtask4.getName(), subtask4.getDescription(),
                Status.DONE, subtask4.getEpicId()));
        Subtask subtask5 = new Subtask("Подзадача 3", "Описание подзадачи 3", epic2.getId());
        taskManager.createSubtask(subtask5);
        System.out.println("Подзадачи после обновления:");
        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getEpicById(epic2.getId()));
        System.out.println("_".repeat(50));

        //Тест удаления задачи по id
        taskManager.deleteTask(2);
        System.out.println("Список задач после удаления задачи: ");
        System.out.println(taskManager.getTasks());
        System.out.println("_".repeat(50));

        //Тест удаления эпика по id
        taskManager.deleteEpic(epic2.getId());
        System.out.println("Список эпиков после удаления эпика: ");
        System.out.println(taskManager.getEpics());
        System.out.println("_".repeat(50));

        //Тест удаления подзадачи по id
        taskManager.deleteSubtask(subtask2.getId());
        System.out.println("Список подзадач после удаления подзадачи: ");
        System.out.println(taskManager.getSubtasks());
        System.out.println("_".repeat(50));

        //проверка очистки коллекций
        taskManager.clearTasks();
        System.out.println(taskManager.getTasks());
        taskManager.clearSubtasks();
        System.out.println(taskManager.getSubtasks());
        taskManager.clearEpics();
        System.out.println(taskManager.getEpics());
        //Печать истории просмотра задач
        System.out.println("История просмотра: ");
        List<Task> history = taskManager.getHistory();
        for (Task task: history) {
            System.out.println(task);
        }
    }
}
