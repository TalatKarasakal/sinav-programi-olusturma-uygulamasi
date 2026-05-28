package scheduler.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeRangeTest {

    @Test
    @DisplayName("TimeRange — geçerli start/end ile oluşturulur")
    void constructor_validArgs() {
        TimeRange tr = new TimeRange(LocalTime.parse("09:00"), LocalTime.parse("10:30"));
        assertEquals(LocalTime.parse("09:00"), tr.getStart());
        assertEquals(LocalTime.parse("10:30"), tr.getEnd());
    }

    @Test
    @DisplayName("TimeRange — start >= end ise IllegalArgumentException fırlatır")
    void constructor_startAfterEnd_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                new TimeRange(LocalTime.parse("10:00"), LocalTime.parse("09:00")));
    }

    @Test
    @DisplayName("TimeRange — null argümanla IllegalArgumentException fırlatır")
    void constructor_nullArgs_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                new TimeRange(null, LocalTime.parse("10:00")));
    }

    @Test
    @DisplayName("TimeRange — lengthMinutes süreyi dakika cinsinden döndürür")
    void lengthMinutes_correct() {
        TimeRange tr = new TimeRange(LocalTime.parse("09:00"), LocalTime.parse("10:30"));
        assertEquals(90, tr.lengthMinutes());
    }

    @Test
    @DisplayName("TimeRange — contains: start dahil, end hariç")
    void contains_boundaries() {
        TimeRange tr = new TimeRange(LocalTime.parse("09:00"), LocalTime.parse("10:30"));
        assertTrue(tr.contains(LocalTime.parse("09:00")));
        assertTrue(tr.contains(LocalTime.parse("10:00")));
        assertFalse(tr.contains(LocalTime.parse("10:30")));
        assertFalse(tr.contains(LocalTime.parse("08:59")));
    }
}
