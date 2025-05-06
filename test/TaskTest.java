import static org.junit.jupiter.api.Assertions.assertEquals;

import model.*;
import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    void tasksWithSameIdShouldEquals() {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2);
    }

}
