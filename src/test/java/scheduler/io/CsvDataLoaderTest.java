package scheduler.io;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scheduler.model.Classroom;
import scheduler.model.Course;
import scheduler.model.Enrollment;
import scheduler.model.Student;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvDataLoaderTest {

    private Path fixture(String name) throws URISyntaxException {
        var url = getClass().getClassLoader().getResource("fixtures/" + name);
        assertNotNull(url, "Fixture not found: " + name);
        return Paths.get(url.toURI());
    }

    @Test
    @DisplayName("CsvDataLoader — eski format öğrenciler (Std_ID_###) yüklenir")
    void loadStudents_oldFormat() throws IOException, URISyntaxException {
        List<Student> students = CsvDataLoader.loadStudents(fixture("students_old.csv"));
        assertEquals(4, students.size());
        assertTrue(students.stream().anyMatch(s -> s.getId().equals("Std_ID_001")));
        assertTrue(students.stream().anyMatch(s -> s.getId().equals("Std_ID_004")));
    }

    @Test
    @DisplayName("CsvDataLoader — yeni format öğrenciler (S000001;Ad;Soyad) yüklenir")
    void loadStudents_newFormat() throws IOException, URISyntaxException {
        List<Student> students = CsvDataLoader.loadStudents(fixture("students_new.csv"));
        assertEquals(3, students.size());
        var ali = students.stream().filter(s -> s.getId().equals("S000001")).findFirst();
        assertTrue(ali.isPresent());
        assertEquals("Ali Yilmaz", ali.get().getName());
    }

    @Test
    @DisplayName("CsvDataLoader — dersler ve süreleri doğru yüklenir")
    void loadCourses_withDuration() throws IOException, URISyntaxException {
        List<Course> courses = CsvDataLoader.loadCourses(fixture("courses.csv"));
        assertEquals(3, courses.size());
        var cs101 = courses.stream().filter(c -> c.getId().equals("CS101")).findFirst();
        assertTrue(cs101.isPresent());
        assertEquals(90, cs101.get().getDurationMinutes());
        var math = courses.stream().filter(c -> c.getId().equals("MATH201")).findFirst();
        assertTrue(math.isPresent());
        assertEquals(120, math.get().getDurationMinutes());
    }

    @Test
    @DisplayName("CsvDataLoader — sınıf odaları ve kapasiteleri doğru yüklenir")
    void loadClassrooms_basic() throws IOException, URISyntaxException {
        List<Classroom> rooms = CsvDataLoader.loadClassrooms(fixture("classrooms.csv"));
        assertEquals(3, rooms.size());
        var b202 = rooms.stream().filter(r -> r.getId().equals("B202")).findFirst();
        assertTrue(b202.isPresent());
        assertEquals(50, b202.get().getCapacity());
    }

    @Test
    @DisplayName("CsvDataLoader — pair format kayıtları yüklenir ve deduplicate edilir")
    void loadEnrollments_pairFormat() throws IOException, URISyntaxException {
        List<Enrollment> enrollments = CsvDataLoader.loadEnrollments(fixture("enrollments_pair.csv"));
        assertEquals(6, enrollments.size());
        assertTrue(enrollments.stream()
                .anyMatch(e -> e.getStudentId().equals("S000001") && e.getCourseId().equals("CS101")));
    }

    @Test
    @DisplayName("CsvDataLoader — attendance list format kayıtları yüklenir")
    void loadEnrollments_attendanceFormat() throws IOException, URISyntaxException {
        List<Enrollment> enrollments = CsvDataLoader.loadEnrollments(fixture("enrollments_attendance.csv"));
        // CourseCode_01: Std_ID_001, Std_ID_002 (2)
        // CourseCode_02: Std_ID_002, Std_ID_003 (2)
        // CourseCode_03: Std_ID_001, Std_ID_003 (2)
        assertEquals(6, enrollments.size());
        assertTrue(enrollments.stream()
                .anyMatch(e -> e.getStudentId().equals("Std_ID_001") && e.getCourseId().equals("CourseCode_01")));
    }
}
