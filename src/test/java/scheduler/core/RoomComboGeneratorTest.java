package scheduler.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scheduler.model.Classroom;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoomComboGeneratorTest {

    private RoomComboGenerator gen;
    private List<Classroom> rooms;

    @BeforeEach
    void setUp() {
        gen = new RoomComboGenerator();
        rooms = List.of(
                new Classroom("A", 30),
                new Classroom("B", 50),
                new Classroom("C", 20)
        );
    }

    @Test
    @DisplayName("Room capacity — tek oda yeterli ise tekli kombinasyon döner")
    void singleRoomSufficient() {
        var combos = gen.generateMinimalCombos(rooms, 25, 10);
        assertFalse(combos.isEmpty());
        // En büyük oda (50) tek başına yeter
        assertEquals(1, combos.get(0).size());
    }

    @Test
    @DisplayName("Tek oda yetersiz — ikili kombinasyon döner")
    void twoRoomsNeeded() {
        // 60 öğrenci → A(30)+B(50)=80 ≥ 60
        var combos = gen.generateMinimalCombos(rooms, 60, 10);
        assertFalse(combos.isEmpty());
        // Eğer tekli yetmiyorsa (hiçbiri ≥60), ilk sonuç ikili
        assertTrue(combos.get(0).size() >= 2);
    }

    @Test
    @DisplayName("totalCapacity — odaların toplam kapasitesini toplar")
    void totalCapacity_sums() {
        int total = RoomComboGenerator.totalCapacity(rooms);
        assertEquals(100, total); // 30+50+20
    }

    @Test
    @DisplayName("Boş oda listesi — boş sonuç döner")
    void generateMinimalCombos_empty() {
        var combos = gen.generateMinimalCombos(List.of(), 10, 5);
        assertTrue(combos.isEmpty());
    }

    @Test
    @DisplayName("preferLargeFirst=false — küçük odalar önce sıralanır")
    void generateMinimalCombos_smallFirst() {
        var combos = gen.generateMinimalCombos(rooms, 20, 3, false);
        assertFalse(combos.isEmpty());
        // İlk tekli kombinasyon en küçük oda
        assertEquals("C", combos.get(0).get(0).getId()); // C=20 ≥ 20
    }

    @Test
    @DisplayName("generateGreedyOrdered — kapasiteyi karşılayacak kadar oda seçer")
    void generateGreedyOrdered_sufficientCapacity() {
        var selected = gen.generateGreedyOrdered(rooms, 60, true);
        int total = RoomComboGenerator.totalCapacity(selected);
        assertTrue(total >= 60);
    }
}
