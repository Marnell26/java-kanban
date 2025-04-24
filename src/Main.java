public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        taskManager.createTask("Задача 1", "Описание задачи 1");
        taskManager.createTask("Задача 2", "Описание задачи 2");
        System.out.println("Список задач: ");
        System.out.println(taskManager.getTasks());

    }
}
