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

class NoStudentClashConstraintTest {

    private static final LocalDate DAY = LocalDate.of(2024, 6, 1);
    private Classroom roomA;

    @BeforeEach
    void setUp() {
        roomA = new Classroom("A", 50);
    }

    private Timeslot slot(String start, String end) {
        return new Timeslot(DAY, LocalTime.parse(start), LocalTime.parse(end));
    }

    private Timeslot slot(LocalDate date, String start, String end) {
        return new Timeslot(date, LocalTime.parse(start), LocalTime.parse(end));
    }

    @Test
    @DisplayName("Constraint — öğrenci paylaşımı yok ise izin verir")
    void noSharedStudents_alwaysPasses() {
        Map<String, Set<String>> c2s = Map.of(
                "C1", Set.of("S1"),
                "C2", Set.of("S2")
        );
        NoStudentClashAndMinGap c = new NoStudentClashAndMinGap(c2s, 60);
        PartialSchedule ps = new PartialSchedule();
        ps.addPlacement(new Placement("C1", slot("09:00", "10:30"), List.of(roomA)));

        Candidate cand = new Candidate("C2", slot("09:00", "10:30"), List.of(roomA));
        assertTrue(c.test(ps, cand));
    }

    @Test
    @DisplayName("Constraint — öğrenci aynı slotta iki sınava giremez")
    void sharedStudent_sameSlot_fails() {
        Map<String, Set<String>> c2s = Map.of(
                "C1", Set.of("S1", "S2"),
                "C2", Set.of("S1", "S3")
        );
        NoStudentClashAndMinGap c = new NoStudentClashAndMinGap(c2s, 60);
        PartialSchedule ps = new PartialSchedule();
        ps.addPlacement(new Placement("C1", slot("09:00", "10:30"), List.of(roomA)));

        Candidate cand = new Candidate("C2", slot("09:00", "10:30"), List.of(roomA));
        assertFalse(c.test(ps, cand));
    }

    @Test
    @DisplayName("Constraint — minimum gap (60dk) zorlanır — aralık yetersizse ret")
    void sharedStudent_insufficientGap_fails() {
        Map<String, Set<String>> c2s = Map.of(
                "C1", Set.of("S1"),
                "C2", Set.of("S1")
        );
        NoStudentClashAndMinGap c = new NoStudentClashAndMinGap(c2s, 60);
        PartialSchedule ps = new PartialSchedule();
        ps.addPlacement(new Placement("C1", slot("09:00", "10:30"), List.of(roomA)));

        // C2 saat 11:00 başlıyor → gap = 30 dk < 60 dk
        Candidate cand = new Candidate("C2", slot("11:00", "12:30"), List.of(roomA));
        assertFalse(c.test(ps, cand));
    }

    @Test
    @DisplayName("Constraint — minimum gap (60dk) zorlanır — yeterli aralıkla geçer")
    void sharedStudent_sufficientGap_passes() {
        Map<String, Set<String>> c2s = Map.of(
                "C1", Set.of("S1"),
                "C2", Set.of("S1")
        );
        NoStudentClashAndMinGap c = new NoStudentClashAndMinGap(c2s, 60);
        PartialSchedule ps = new PartialSchedule();
        ps.addPlacement(new Placement("C1", slot("09:00", "10:30"), List.of(roomA)));

        // C2 saat 11:30 başlıyor → gap = 60 dk = min_gap, kabul edilmeli
        Candidate cand = new Candidate("C2", slot("11:30", "13:00"), List.of(roomA));
        assertTrue(c.test(ps, cand));
    }

    @Test
    @DisplayName("Constraint — farklı günde aynı öğrencinin çakışması sorun değil")
    void sharedStudent_differentDay_passes() {
        Map<String, Set<String>> c2s = Map.of(
                "C1", Set.of("S1"),
                "C2", Set.of("S1")
        );
        NoStudentClashAndMinGap c = new NoStudentClashAndMinGap(c2s, 60);
        PartialSchedule ps = new PartialSchedule();
        ps.addPlacement(new Placement("C1", slot("09:00", "10:30"), List.of(roomA)));

        LocalDate nextDay = DAY.plusDays(1);
        Candidate cand = new Candidate("C2",
                slot(nextDay, "09:00", "10:30"), List.of(roomA));
        assertTrue(c.test(ps, cand));
    }
}
