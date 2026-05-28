package scheduler.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    @Test
    @DisplayName("Student — id ve isim ile oluşturulur")
    void constructor_withName() {
        Student s = new Student("S001", "Ali Yilmaz");
        assertEquals("S001", s.getId());
        assertEquals("Ali Yilmaz", s.getName());
    }

    @Test
    @DisplayName("Student — sadece id ile oluşturulur, isim boş kalır")
    void constructor_idOnly_nameEmpty() {
        Student s = new Student("S002");
        assertEquals("S002", s.getId());
        assertEquals("", s.getName());
    }

    @Test
    @DisplayName("Student — null isim boş stringe dönüştürülür")
    void constructor_nullName_becomesEmpty() {
        Student s = new Student("S003", null);
        assertEquals("", s.getName());
    }

    @Test
    @DisplayName("Student — toString id ve ismi birleştirir")
    void toString_withName() {
        Student s = new Student("S001", "Ali");
        assertEquals("S001 (Ali)", s.toString());
    }

    @Test
    @DisplayName("Student — toString sadece id döndürür (isim yoksa)")
    void toString_withoutName() {
        Student s = new Student("S001");
        assertEquals("S001", s.toString());
    }
}
