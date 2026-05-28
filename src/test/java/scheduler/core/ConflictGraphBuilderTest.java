package scheduler.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scheduler.model.Enrollment;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConflictGraphBuilderTest {

    private ConflictGraphBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new ConflictGraphBuilder();
    }

    @Test
    @DisplayName("buildCourseToStudents — tek kayıt doğru eşlenir")
    void buildCourseToStudents_singleEnrollment() {
        var enrollments = List.of(new Enrollment("S1", "CS101"));
        Map<String, Set<String>> map = builder.buildCourseToStudents(enrollments);
        assertEquals(1, map.size());
        assertTrue(map.get("CS101").contains("S1"));
    }

    @Test
    @DisplayName("buildCourseToStudents — birden fazla ders/öğrenci doğru gruplanır")
    void buildCourseToStudents_multipleEnrollments() {
        var enrollments = List.of(
                new Enrollment("S1", "CS101"),
                new Enrollment("S2", "CS101"),
                new Enrollment("S1", "SE302")
        );
        Map<String, Set<String>> map = builder.buildCourseToStudents(enrollments);
        assertEquals(2, map.get("CS101").size());
        assertEquals(1, map.get("SE302").size());
    }

    @Test
    @DisplayName("buildCourseToStudents — boş liste sonucu boş map")
    void buildCourseToStudents_empty() {
        Map<String, Set<String>> map = builder.buildCourseToStudents(List.of());
        assertTrue(map.isEmpty());
    }

    @Test
    @DisplayName("Conflict Graph — aynı öğrenciyi paylaşan dersler conflict eder")
    void buildDegrees_twoConflictingCourses() {
        var enrollments = List.of(
                new Enrollment("S1", "CS101"),
                new Enrollment("S1", "SE302")
        );
        Map<String, Set<String>> c2s = builder.buildCourseToStudents(enrollments);
        Map<String, Integer> degrees = builder.buildDegrees(c2s);

        // S1 her iki derste, conflict var → her birinin derecesi 1
        assertEquals(1, degrees.get("CS101"));
        assertEquals(1, degrees.get("SE302"));
    }

    @Test
    @DisplayName("Conflict Graph — ortak öğrenci yok ise derece 0")
    void buildDegrees_noConflict() {
        var enrollments = List.of(
                new Enrollment("S1", "CS101"),
                new Enrollment("S2", "SE302")
        );
        Map<String, Set<String>> c2s = builder.buildCourseToStudents(enrollments);
        Map<String, Integer> degrees = builder.buildDegrees(c2s);

        assertEquals(0, degrees.get("CS101"));
        assertEquals(0, degrees.get("SE302"));
    }

    @Test
    @DisplayName("buildDegrees — üç derste karmaşık çakışma grafiği doğru")
    void buildDegrees_multiCourseConflict() {
        // S1: C1, C2; S2: C2, C3 → C1-C2 conflict, C2-C3 conflict; C1-C3 no conflict
        var enrollments = List.of(
                new Enrollment("S1", "C1"),
                new Enrollment("S1", "C2"),
                new Enrollment("S2", "C2"),
                new Enrollment("S2", "C3")
        );
        Map<String, Set<String>> c2s = builder.buildCourseToStudents(enrollments);
        Map<String, Integer> degrees = builder.buildDegrees(c2s);

        assertEquals(1, degrees.get("C1")); // çakışıyor: C2
        assertEquals(2, degrees.get("C2")); // çakışıyor: C1, C3
        assertEquals(1, degrees.get("C3")); // çakışıyor: C2
    }

    @Test
    @DisplayName("buildDegrees — boş enrollment listesi sonucu boş map")
    void buildDegrees_empty() {
        Map<String, Integer> degrees = builder.buildDegrees(Map.of());
        assertTrue(degrees.isEmpty());
    }
}
