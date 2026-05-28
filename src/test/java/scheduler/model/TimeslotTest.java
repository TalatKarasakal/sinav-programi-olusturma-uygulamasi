package scheduler.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeslotTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Test
    @DisplayName("Timeslot — geçerli argümanlarla oluşturulur")
    void constructor_validArgs() {
        Timeslot ts = new Timeslot(DATE, LocalTime.parse("09:00"), LocalTime.parse("10:30"));
        assertEquals(DATE, ts.getDate());
        assertEquals(LocalTime.parse("09:00"), ts.getStart());
        assertEquals(LocalTime.parse("10:30"), ts.getEnd());
    }

    @Test
    @DisplayName("Timeslot — null tarih ile IllegalArgumentException fırlatır")
    void constructor_nullDate_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                new Timeslot(null, LocalTime.parse("09:00"), LocalTime.parse("10:30")));
    }

    @Test
    @DisplayName("Timeslot — start >= end ise IllegalArgumentException fırlatır")
    void constructor_startAfterEnd_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                new Timeslot(DATE, LocalTime.parse("10:30"), LocalTime.parse("09:00")));
    }

    @Test
    @DisplayName("Timeslot — start == end ise IllegalArgumentException fırlatır")
    void constructor_startEqualsEnd_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                new Timeslot(DATE, LocalTime.parse("09:00"), LocalTime.parse("09:00")));
    }
}
