import static org.junit.jupiter.api.Assertions.assertEquals;

import model.*;
import org.junit.jupiter.api.Test;

public class EpicTest {

    @Test
    void epicsWithSameIdShouldEquals() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        epic1.setId(1);
        epic2.setId(1);
        assertEquals(epic1, epic2);
    }

}
