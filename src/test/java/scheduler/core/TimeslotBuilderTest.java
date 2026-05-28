package scheduler.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scheduler.model.DayWindow;
import scheduler.model.TimeRange;
import scheduler.model.Timeslot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TimeslotBuilderTest {

    private TimeslotBuilder builder;
    private static final LocalDate DAY1 = LocalDate.of(2024, 6, 1);
    private static final LocalDate DAY2 = LocalDate.of(2024, 6, 2);

    @BeforeEach
    void setUp() {
        builder = new TimeslotBuilder();
    }

    private DayWindow window(LocalDate date, String start, String end) {
        return new DayWindow(date, List.of(
                new TimeRange(LocalTime.parse(start), LocalTime.parse(end))));
    }

    @Test
    @DisplayName("TimeslotBuilder — 8 saatlik pencerede 90dk sınavlar üretilir")
    void build_singleDay_multipleSlots() {
        // 09:00-17:00 = 480 dk, 90 dk sınav, 10 dk adım
        // İlk slot 09:00-10:30; son slot 15:30-17:00 (15:30 + 90 = 17:00)
        List<Timeslot> slots = builder.build(List.of(window(DAY1, "09:00", "17:00")), 90);
        assertFalse(slots.isEmpty());
        // (480 - 90) / 10 + 1 = 40 slot (eğer 17:00 tam olarak sınırsa)
        // İlk slot 09:00
        assertEquals(LocalTime.parse("09:00"), slots.get(0).getStart());
        assertEquals(LocalTime.parse("10:30"), slots.get(0).getEnd());
    }

    @Test
    @DisplayName("TimeslotBuilder — sınav süresi pencereye tam sığar")
    void build_slotFitsExactly() {
        // 09:00-10:30 = 90 dk, sadece 1 slot üretilmeli
        List<Timeslot> slots = builder.build(List.of(window(DAY1, "09:00", "10:30")), 90);
        assertEquals(1, slots.size());
        assertEquals(LocalTime.parse("09:00"), slots.get(0).getStart());
    }

    @Test
    @DisplayName("TimeslotBuilder — sınav süresi pencereden uzun ise slot yok")
    void build_durationExceedsWindow() {
        // 09:00-10:00 = 60 dk, 90 dk sınav sığmaz
        List<Timeslot> slots = builder.build(List.of(window(DAY1, "09:00", "10:00")), 90);
        assertTrue(slots.isEmpty());
    }

    @Test
    @DisplayName("TimeslotBuilder — iki gün toplamda daha fazla slot")
    void build_multipleDays() {
        List<DayWindow> days = List.of(
                window(DAY1, "09:00", "12:00"),
                window(DAY2, "09:00", "12:00")
        );
        List<Timeslot> slots = builder.build(days, 90);
        // Her günde aynı sayıda slot, iki gün birlikte daha fazla
        List<Timeslot> oneDay = builder.build(List.of(window(DAY1, "09:00", "12:00")), 90);
        assertEquals(oneDay.size() * 2, slots.size());
    }

    @Test
    @DisplayName("TimeslotBuilder — boş DayWindow listesi sonucu boş")
    void build_emptyDayWindows() {
        List<Timeslot> slots = builder.build(List.of(), 90);
        assertTrue(slots.isEmpty());
    }
}
