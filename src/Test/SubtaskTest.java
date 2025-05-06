package Test;

import model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {

    @Test
    void subtasksWithSameIdShouldEquals() {
        Subtask subtask1 = new Subtask("Задача 1", "Описание задачи 1", 1);
        Subtask subtask2 = new Subtask("Задача 2", "Описание задачи 2", 1);
        subtask1.setId(1);
        subtask2.setId(1);
        assertEquals(subtask1, subtask2);
    }
}
