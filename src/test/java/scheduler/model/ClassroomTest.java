package scheduler.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClassroomTest {

    @Test
    @DisplayName("Classroom — id ve kapasite ile oluşturulur")
    void constructor_setsFields() {
        Classroom room = new Classroom("A101", 30);
        assertEquals("A101", room.getId());
        assertEquals(30, room.getCapacity());
    }

    @Test
    @DisplayName("Classroom — sıfır kapasiteli oda oluşturulabilir")
    void constructor_zeroCapacity() {
        Classroom room = new Classroom("EMPTY", 0);
        assertEquals(0, room.getCapacity());
    }

    @Test
    @DisplayName("Classroom — büyük kapasiteli oda oluşturulabilir")
    void constructor_largeCapacity() {
        Classroom room = new Classroom("HALL", 500);
        assertEquals("HALL", room.getId());
        assertEquals(500, room.getCapacity());
    }
}
