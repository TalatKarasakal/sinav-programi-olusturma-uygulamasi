package scheduler.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {

    @Test
    @DisplayName("Course — id ve süre ile oluşturulur")
    void constructor_setsIdAndDuration() {
        Course c = new Course("CS101", 90);
        assertEquals("CS101", c.getId());
        assertEquals(90, c.getDurationMinutes());
        assertFalse(c.isIgnored());
    }

    @Test
    @DisplayName("Course — setDurationMinutes günceller")
    void setDurationMinutes_updates() {
        Course c = new Course("CS101", 90);
        c.setDurationMinutes(120);
        assertEquals(120, c.getDurationMinutes());
    }

    @Test
    @DisplayName("Course — setIgnored true yapıldığında isIgnored true döner")
    void setIgnored_true() {
        Course c = new Course("CS101", 90);
        c.setIgnored(true);
        assertTrue(c.isIgnored());
    }

    @Test
    @DisplayName("Course — toString id döndürür")
    void toString_returnsId() {
        Course c = new Course("MATH201", 60);
        assertEquals("MATH201", c.toString());
    }

    @Test
    @DisplayName("Course — kapasite sınırları setter/getter çalışır")
    void capacityFilters_setAndGet() {
        Course c = new Course("SE302", 90);
        c.setMinRoomCapacity(10);
        c.setMaxRoomCapacity(50);
        assertEquals(10, c.getMinRoomCapacity());
        assertEquals(50, c.getMaxRoomCapacity());
    }
}
