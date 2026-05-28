package scheduler.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scheduler.model.*;

import java.time.LocalDate;
import java.util.Collection;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExamSchedulerTest {

    private static final LocalDate DAY = LocalDate.of(2024, 6, 1);

    private List<Student> students;
    private List<Classroom> rooms;
    private DayWindow dayWindow;

    @BeforeEach
    void setUp() {
        students = List.of(
                new Student("S1"),
                new Student("S2"),
                new Student("S3")
        );
        rooms = List.of(
                new Classroom("R1", 50),
                new Classroom("R2", 50)
        );
        dayWindow = new DayWindow(DAY, List.of(
                new TimeRange(LocalTime.parse("09:00"), LocalTime.parse("17:00"))
        ));
    }

    @Test
    @DisplayName("Backtracking — çakışmasız dersler her ikisi de planlanır")
    void twoIndependentCourses_bothScheduled() {
        var courses = List.of(
                new Course("C1", 90),
                new Course("C2", 90)
        );
        // C1: S1, C2: S2 — çakışma yok
        var enrollments = List.of(
                new Enrollment("S1", "C1"),
                new Enrollment("S2", "C2")
        );

        ExamScheduler scheduler = new ExamScheduler();
        Map<String, List<StudentExam>> result = scheduler.run(
                students, courses, enrollments, rooms, List.of(dayWindow));

        assertTrue(scheduler.getUnscheduledReasons().isEmpty(),
                "Planlanamayan ders olmamalı: " + scheduler.getUnscheduledReasons());
        assertTrue(result.containsKey("S1"));
        assertTrue(result.containsKey("S2"));
    }

    @Test
    @DisplayName("Constraint — çakışan dersler farklı slot'lara atanır")
    void conflictingCourses_scheduledAtDifferentTimes() {
        var courses = List.of(
                new Course("C1", 90),
                new Course("C2", 90)
        );
        // S1 her iki derste → C1 ve C2 çakışıyor, farklı slotlara gitmeli
        var enrollments = List.of(
                new Enrollment("S1", "C1"),
                new Enrollment("S1", "C2"),
                new Enrollment("S2", "C2")
        );

        ExamScheduler scheduler = new ExamScheduler();
        Map<String, List<StudentExam>> result = scheduler.run(
                students, courses, enrollments, rooms, List.of(dayWindow));

        assertTrue(scheduler.getUnscheduledReasons().isEmpty(),
                "Planlanamayan ders: " + scheduler.getUnscheduledReasons());

        // S1'in iki sınavı farklı zamanlarda olmalı
        List<StudentExam> s1Exams = result.get("S1");
        assertNotNull(s1Exams);
        assertEquals(2, s1Exams.size());

        Timeslot t1 = s1Exams.get(0).getTimeslot();
        Timeslot t2 = s1Exams.get(1).getTimeslot();
        assertFalse(
                t1.getStart().equals(t2.getStart()) && t1.getDate().equals(t2.getDate()),
                "S1'in iki sınavı aynı slotta olamaz"
        );
    }

    @Test
    @DisplayName("Deterministic seed (42L) — aynı input → aynı output")
    void deterministicSeed_sameInputSameOutput() {
        var courses = List.of(
                new Course("C1", 90),
                new Course("C2", 90),
                new Course("C3", 90)
        );
        var enrollments = List.of(
                new Enrollment("S1", "C1"),
                new Enrollment("S1", "C2"),
                new Enrollment("S2", "C2"),
                new Enrollment("S2", "C3"),
                new Enrollment("S3", "C3")
        );

        ExamScheduler s1 = new ExamScheduler();
        Map<String, List<StudentExam>> run1 = s1.run(
                students, courses, enrollments, rooms, List.of(dayWindow));

        ExamScheduler s2 = new ExamScheduler();
        Map<String, List<StudentExam>> run2 = s2.run(
                students, courses, enrollments, rooms, List.of(dayWindow));

        // Oturumlar aynı olmalı: her öğrenci için aynı courseId sırası ve aynı seatNo
        for (String sid : run1.keySet()) {
            List<StudentExam> exams1 = run1.get(sid);
            List<StudentExam> exams2 = run2.get(sid);
            assertNotNull(exams2, "İkinci çalıştırmada " + sid + " bulunamadı");
            assertEquals(exams1.size(), exams2.size());
            for (int i = 0; i < exams1.size(); i++) {
                assertEquals(exams1.get(i).getSeatNo(), exams2.get(i).getSeatNo(),
                        sid + " için koltuk numarası farklı");
                assertEquals(exams1.get(i).getClassroomId(), exams2.get(i).getClassroomId(),
                        sid + " için sınıf farklı");
            }
        }
    }

    @Test
    @DisplayName("Room capacity — yeterli oda kapasitesi yoksa ders planlanamaz")
    void noRoomCapacity_courseUnscheduled() {
        var courses = List.of(new Course("BIG", 90));
        // 200 öğrenci, ama en büyük oda 50 kişilik
        var bigEnrollments = new java.util.ArrayList<Enrollment>();
        var bigStudents = new java.util.ArrayList<Student>();
        for (int i = 0; i < 200; i++) {
            String id = "BIG_S" + i;
            bigStudents.add(new Student(id));
            bigEnrollments.add(new Enrollment(id, "BIG"));
        }

        // R1=50, R2=50 → toplam 100, 200 için yetmez
        ExamScheduler scheduler = new ExamScheduler();
        scheduler.run(bigStudents, courses, bigEnrollments, rooms, List.of(dayWindow));

        assertTrue(scheduler.getUnscheduledReasons().containsKey("BIG"),
                "200 öğrencili ders 100 kapasiteli odalara sığmamalı");
    }

    @Test
    @DisplayName("Backtracking — çıkmaz sokakta geri sarar")
    void backtracking_deadEndResolved() {
        // Two courses sharing one student, window contains exactly one 90-min slot.
        // Greedy places CA first; CB then fails all slots due to student+room conflict.
        // tryBacktracking is invoked: it removes CA, places CB, tries to restore CA —
        // but CA also conflicts → backtracking rolls back and analyzeFailure is called.
        // Verifies that exactly one course remains unscheduled (not both).
        List<Student> students = List.of(new Student("SA"));
        List<Course> courses = List.of(new Course("CA", 90), new Course("CB", 90));
        List<Enrollment> enrollments = List.of(
                new Enrollment("SA", "CA"),
                new Enrollment("SA", "CB")
        );
        List<Classroom> rooms = List.of(new Classroom("R1", 10));

        // Exactly 90-min window → single slot 09:00-10:30; no room to fit both conflicting courses
        DayWindow narrow = new DayWindow(
                LocalDate.of(2024, 6, 3),
                List.of(new TimeRange(LocalTime.parse("09:00"), LocalTime.parse("10:30")))
        );

        ExamScheduler scheduler = new ExamScheduler();
        scheduler.run(students, courses, enrollments, rooms, List.of(narrow));

        // One course must fail — two conflicting exams cannot share the only slot
        assertEquals(1, scheduler.getUnscheduledReasons().size(),
                "Tek slot ile çakışan iki dersten tam olarak biri planlanamaz: "
                + scheduler.getUnscheduledReasons());
    }

    @Test
    @DisplayName("ExamScheduler — sıfır kayıtlı ders planlanamaz olarak işaretlenir")
    void courseWithNoEnrollments_markedUnscheduled() {
        var courses = List.of(new Course("EMPTY", 90));
        // No enrollments for EMPTY course
        ExamScheduler scheduler = new ExamScheduler();
        scheduler.run(students, courses, List.of(), rooms, List.of(dayWindow));

        assertTrue(scheduler.getUnscheduledReasons().containsKey("EMPTY"),
                "Kayıtsız ders planlanamaz olarak işaretlenmeli");
    }

    @Test
    @DisplayName("ExamScheduler — boş güncel pencere verilince sonuç boş map döner")
    void emptyDayWindows_returnsEmpty() {
        var courses = List.of(new Course("C1", 90));
        var enrollments = List.of(new Enrollment("S1", "C1"));

        ExamScheduler scheduler = new ExamScheduler();
        Map<String, List<StudentExam>> result = scheduler.run(
                students, courses, enrollments, rooms, List.of());

        assertTrue(result.isEmpty());
    }
}
