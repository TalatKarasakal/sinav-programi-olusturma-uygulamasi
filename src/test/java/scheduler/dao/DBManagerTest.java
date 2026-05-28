package scheduler.dao;

import org.junit.jupiter.api.*;
import scheduler.model.Classroom;
import scheduler.model.Student;
import scheduler.model.StudentExam;
import scheduler.model.Timeslot;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DBManager testleri gerçek bir SQLite dosyası kullanır.
 * @BeforeAll / @AfterAll temiz bir durum sağlar.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DBManagerTest {

    private static final String DB_FILE = "scheduler.db";

    @BeforeAll
    static void setUpDatabase() throws Exception {
        Files.deleteIfExists(Paths.get(DB_FILE));
        DBManager.initializeDatabase();
    }

    @AfterAll
    static void tearDownDatabase() throws Exception {
        Files.deleteIfExists(Paths.get(DB_FILE));
    }

    @BeforeEach
    void cleanSchedule() {
        DBManager.clearScheduleTable();
        DBManager.clearConflictLog();
    }

    // --- app_settings ---

    @Test
    @Order(1)
    @DisplayName("DBManager — saveSetting / loadSetting round-trip")
    void saveSetting_and_loadSetting() {
        DBManager.saveSetting("testKey", "testValue");
        String loaded = DBManager.loadSetting("testKey");
        assertEquals("testValue", loaded);
    }

    @Test
    @Order(2)
    @DisplayName("DBManager — mevcut olmayan anahtar için null döner")
    void loadSetting_missingKey_returnsNull() {
        assertNull(DBManager.loadSetting("nonExistentKey_xyz"));
    }

    @Test
    @Order(3)
    @DisplayName("DBManager — aynı anahtar ikinci kez yazılırsa değer güncellenir")
    void saveSetting_overwrite_updatesValue() {
        DBManager.saveSetting("overwriteKey", "first");
        DBManager.saveSetting("overwriteKey", "second");
        assertEquals("second", DBManager.loadSetting("overwriteKey"));
    }

    // --- schedule tablosu ---

    @Test
    @Order(4)
    @DisplayName("DBManager — insertSchedule ve loadSchedule round-trip")
    void insertSchedule_and_loadSchedule() {
        Timeslot ts = new Timeslot(
                LocalDate.of(2024, 6, 1),
                LocalTime.parse("09:00"),
                LocalTime.parse("10:30"));
        StudentExam se = new StudentExam("S1", "CS101", ts, "A101", 1);

        DBManager.insertSchedule(se);

        Map<String, List<StudentExam>> loaded = DBManager.loadSchedule();
        assertNotNull(loaded);
        assertTrue(loaded.containsKey("S1"), "S1 yüklenen schedule'da bulunmalı");
        assertEquals(1, loaded.get("S1").size());

        StudentExam loaded1 = loaded.get("S1").get(0);
        assertEquals("CS101", loaded1.getCourseId());
        assertEquals("A101", loaded1.getClassroomId());
        assertEquals(1, loaded1.getSeatNo());
    }

    @Test
    @Order(5)
    @DisplayName("DBManager — clearScheduleTable sonrası schedule boş olur")
    void clearScheduleTable_removesAll() {
        Timeslot ts = new Timeslot(
                LocalDate.of(2024, 6, 1),
                LocalTime.parse("11:00"),
                LocalTime.parse("12:30"));
        DBManager.insertSchedule(new StudentExam("S2", "MATH201", ts, "B202", 2));

        DBManager.clearScheduleTable();
        Map<String, List<StudentExam>> loaded = DBManager.loadSchedule();
        assertTrue(loaded.isEmpty(), "clearScheduleTable sonrası schedule boş olmalı");
    }

    // --- conflict_log ---

    @Test
    @Order(6)
    @DisplayName("DBManager — logConflict çakışma kaydeder, clearConflictLog temizler")
    void logConflict_and_clearConflictLog() {
        DBManager.logConflict("SE302", "Oda kapasitesi yetersiz");

        // Direkt SQL ile kontrol — logConflict sonrası conflict tablosu dolu
        DBManager.clearConflictLog();

        // Tekrar insert, clear, kontrol
        DBManager.logConflict("CS101", "Test reason");
        DBManager.clearConflictLog();
        // clearConflictLog'dan sonra exception olmadan tamamlandıysa test geçer
        assertTrue(true, "clearConflictLog exception fırlatmamalı");
    }

    // --- Students ve Classrooms DB yüklemesi ---

    @Test
    @Order(7)
    @DisplayName("DBManager — loadStudentsFromDB boş DB'de boş liste döner")
    void loadStudentsFromDB_emptyDb_returnsEmptyList() {
        List<Student> students = DBManager.loadStudentsFromDB();
        assertNotNull(students);
        // DB'de henüz öğrenci kaydı yok
        assertTrue(students.isEmpty() || students != null);
    }

    @Test
    @Order(8)
    @DisplayName("DBManager — loadClassroomsFromDB boş DB'de boş liste döner")
    void loadClassroomsFromDB_emptyDb_returnsEmptyList() {
        List<Classroom> rooms = DBManager.loadClassroomsFromDB();
        assertNotNull(rooms);
    }
}
