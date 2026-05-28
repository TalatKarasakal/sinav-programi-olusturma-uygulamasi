package scheduler.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DayWindowTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Test
    @DisplayName("DayWindow — tarih ve aralıklar ile oluşturulur")
    void constructor_validArgs() {
        TimeRange range = new TimeRange(LocalTime.parse("09:00"), LocalTime.parse("17:00"));
        DayWindow dw = new DayWindow(DATE, List.of(range));
        assertEquals(DATE, dw.getDate());
        assertEquals(1, dw.getRanges().size());
    }

    @Test
    @DisplayName("DayWindow — null tarih ile IllegalArgumentException fırlatır")
    void constructor_nullDate_throws() {
        TimeRange range = new TimeRange(LocalTime.parse("09:00"), LocalTime.parse("17:00"));
        assertThrows(IllegalArgumentException.class, () ->
                new DayWindow(null, List.of(range)));
    }

    @Test
    @DisplayName("DayWindow — boş aralık listesi ile IllegalArgumentException fırlatır")
    void constructor_emptyRanges_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                new DayWindow(DATE, Collections.emptyList()));
    }

    @Test
    @DisplayName("DayWindow — birden fazla aralık desteklenir")
    void constructor_multipleRanges() {
        TimeRange r1 = new TimeRange(LocalTime.parse("09:00"), LocalTime.parse("12:00"));
        TimeRange r2 = new TimeRange(LocalTime.parse("13:00"), LocalTime.parse("17:00"));
        DayWindow dw = new DayWindow(DATE, List.of(r1, r2));
        assertEquals(2, dw.getRanges().size());
    }
}
