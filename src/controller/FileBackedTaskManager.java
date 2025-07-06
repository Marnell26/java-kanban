package controller;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File autoSaveFile;

    public FileBackedTaskManager(File autoSaveFile) {
        super();
        this.autoSaveFile = autoSaveFile;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(autoSaveFile))) {
            writer.write('\uFEFF');
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getTasks()) {
                writer.write(task.toString() + "\n");
            }
            for (Epic epic : getEpics()) {
                writer.write(epic.toString() + "\n");
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(subtask.toString() + "\n");
            }
        }
        catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных" + e.getMessage());
        }
    }


    private static Task fromString(String lineFromFile) {
        String[] split = lineFromFile.split(",");
        split[0] = split[0].replace("\uFEFF", "");
        TaskType taskType = TaskType.valueOf(split[1]);
        int id = Integer.parseInt(split[0]);
        String name = split[2];
        String description = split[4];
        Status status = Status.valueOf(split[3]);
        switch (taskType) {
            case TASK:
                return new Task(id, name, description, status);
            case EPIC:
                return new Epic(id, name, description);
            case SUBTASK:
                return new Subtask(id, name, description, status, Integer.parseInt(split[5]));
            default:
                throw new ManagerSaveException("Ошибка при чтении файла");
        }
    }

    public static TaskManager loadFromFile(File file) {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); //Пропускаем строку заголовка
            String line;
            int idFromFile = 0;
            while((line = reader.readLine()) != null) {
                Task task = fromString(line);
                TaskType taskType = task.getType();
                idFromFile = task.getId();
                switch (taskType) {
                    case TASK:
                        fileManager.createTask(task);
                        fileManager.tasks.put(task.getId(), task);
                        break;
                    case EPIC:
                        fileManager.epics.put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        fileManager.subtasks.put(task.getId(), (Subtask) task );
                        break;
                    default:
                        throw new ManagerSaveException("Ошибка при чтении файла");
                }
            }
            fileManager.id = idFromFile;
        }
        catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла");
        }

        return fileManager;
    }

    public File getAutoSaveFile() {
        return autoSaveFile;
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

}
