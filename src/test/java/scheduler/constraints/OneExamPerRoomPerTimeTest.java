package scheduler.constraints;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scheduler.model.Classroom;
import scheduler.model.Placement;
import scheduler.model.Timeslot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OneExamPerRoomPerTimeTest {

    private static final LocalDate DAY = LocalDate.of(2024, 6, 1);
    private Classroom roomA;
    private Classroom roomB;
    private OneExamPerRoomPerTime constraint;

    @BeforeEach
    void setUp() {
        roomA = new Classroom("A", 30);
        roomB = new Classroom("B", 30);
        constraint = new OneExamPerRoomPerTime();
    }

    private Timeslot slot(String start, String end) {
        return new Timeslot(DAY, LocalTime.parse(start), LocalTime.parse(end));
    }

    @Test
    @DisplayName("Room — farklı odalar aynı slot'ta kullanılabilir")
    void differentRooms_overlappingTimes_passes() {
        PartialSchedule ps = new PartialSchedule();
        ps.addPlacement(new Placement("C1", slot("09:00", "10:30"), List.of(roomA)));

        Candidate cand = new Candidate("C2", slot("09:00", "10:30"), List.of(roomB));
        assertTrue(constraint.test(ps, cand));
    }

    @Test
    @DisplayName("Room capacity — aynı anda aynı sınıf kullanılamaz")
    void sameRoom_overlappingTimes_fails() {
        PartialSchedule ps = new PartialSchedule();
        ps.addPlacement(new Placement("C1", slot("09:00", "10:30"), List.of(roomA)));

        Candidate cand = new Candidate("C2", slot("09:30", "11:00"), List.of(roomA));
        assertFalse(constraint.test(ps, cand));
    }

    @Test
    @DisplayName("Room — aynı oda farklı günlerde kullanılabilir")
    void sameRoom_differentDays_passes() {
        PartialSchedule ps = new PartialSchedule();
        ps.addPlacement(new Placement("C1", slot("09:00", "10:30"), List.of(roomA)));

        LocalDate nextDay = DAY.plusDays(1);
        Timeslot nextSlot = new Timeslot(nextDay, LocalTime.parse("09:00"), LocalTime.parse("10:30"));
        Candidate cand = new Candidate("C2", nextSlot, List.of(roomA));
        assertTrue(constraint.test(ps, cand));
    }

    @Test
    @DisplayName("Room — aynı oda bitişik (çakışmayan) slotlarda kullanılabilir")
    void sameRoom_adjacentNonOverlappingSlots_passes() {
        PartialSchedule ps = new PartialSchedule();
        ps.addPlacement(new Placement("C1", slot("09:00", "10:30"), List.of(roomA)));

        // C2 tam 10:30'da başlıyor — çakışma yok ([09:00,10:30) ve [10:30,12:00))
        Candidate cand = new Candidate("C2", slot("10:30", "12:00"), List.of(roomA));
        assertTrue(constraint.test(ps, cand));
    }
}
