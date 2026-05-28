package scheduler.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentTest {

    @Test
    @DisplayName("Enrollment — studentId ve courseId ile oluşturulur")
    void constructor_setsFields() {
        Enrollment e = new Enrollment("S001", "CS101");
        assertEquals("S001", e.getStudentId());
        assertEquals("CS101", e.getCourseId());
    }

    @Test
    @DisplayName("Enrollment — farklı iki kayıt bağımsız veriler tutar")
    void twoEnrollments_holdIndependentData() {
        Enrollment e1 = new Enrollment("S001", "CS101");
        Enrollment e2 = new Enrollment("S002", "MATH201");
        assertNotEquals(e1.getStudentId(), e2.getStudentId());
        assertNotEquals(e1.getCourseId(), e2.getCourseId());
    }
}
