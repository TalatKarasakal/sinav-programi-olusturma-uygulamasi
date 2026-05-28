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
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MaxExamsPerDayTest {

    private static final LocalDate DAY = LocalDate.of(2024, 6, 1);
    private Classroom roomA;

    @BeforeEach
    void setUp() {
        roomA = new Classroom("A", 50);
    }

    private Timeslot slotAt(String start, int durationMin) {
        LocalTime s = LocalTime.parse(start);
        return new Timeslot(DAY, s, s.plusMinutes(durationMin));
    }

    @Test
    @DisplayName("Constraint — günlük max sınav (default 2): ilk sınav her zaman geçer")
    void firstExam_alwaysPasses() {
        Map<String, Set<String>> c2s = Map.of("C1", Set.of("S1"));
        MaxExamsPerDay c = new MaxExamsPerDay(c2s, 2);
        PartialSchedule ps = new PartialSchedule();

        Candidate cand = new Candidate("C1", slotAt("09:00", 90), List.of(roomA));
        assertTrue(c.test(ps, cand));
    }

    @Test
    @DisplayName("Constraint — günlük max sınav (default 2): ikinci sınav geçer")
    void secondExam_passes() {
        Map<String, Set<String>> c2s = Map.of(
                "C1", Set.of("S1"),
                "C2", Set.of("S1")
        );
        MaxExamsPerDay c = new MaxExamsPerDay(c2s, 2);
        PartialSchedule ps = new PartialSchedule();
        ps.addPlacement(new Placement("C1", slotAt("09:00", 90), List.of(roomA)));

        Candidate cand = new Candidate("C2", slotAt("12:00", 90), List.of(roomA));
        assertTrue(c.test(ps, cand));
    }

    @Test
    @DisplayName("Constraint — günlük max sınav (default 2): üçüncü sınav reddedilir")
    void thirdExam_fails() {
        Map<String, Set<String>> c2s = Map.of(
                "C1", Set.of("S1"),
                "C2", Set.of("S1"),
                "C3", Set.of("S1")
        );
        MaxExamsPerDay c = new MaxExamsPerDay(c2s, 2);
        PartialSchedule ps = new PartialSchedule();
        ps.addPlacement(new Placement("C1", slotAt("09:00", 90), List.of(roomA)));
        ps.addPlacement(new Placement("C2", slotAt("11:30", 90), List.of(roomA)));

        // S1 zaten 2 sınavı var; üçüncüsü reddedilmeli
        Candidate cand = new Candidate("C3", slotAt("14:00", 90), List.of(roomA));
        assertFalse(c.test(ps, cand));
    }

    @Test
    @DisplayName("Constraint — farklı öğrenciler için limit bağımsız kontrol edilir")
    void differentStudents_independentLimits() {
        Map<String, Set<String>> c2s = Map.of(
                "C1", Set.of("S1"),
                "C2", Set.of("S1"),
                "C3", Set.of("S2") // sadece S2
        );
        MaxExamsPerDay c = new MaxExamsPerDay(c2s, 2);
        PartialSchedule ps = new PartialSchedule();
        ps.addPlacement(new Placement("C1", slotAt("09:00", 90), List.of(roomA)));
        ps.addPlacement(new Placement("C2", slotAt("11:30", 90), List.of(roomA)));

        // S2 için yalnızca 0 sınav var — geçmeli
        Candidate cand = new Candidate("C3", slotAt("14:00", 90), List.of(roomA));
        assertTrue(c.test(ps, cand));
    }
}
