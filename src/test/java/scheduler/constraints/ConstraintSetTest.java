package scheduler.constraints;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scheduler.model.Classroom;
import scheduler.model.Timeslot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConstraintSetTest {

    private static final Candidate DUMMY_CAND = new Candidate(
            "C1",
            new Timeslot(LocalDate.of(2024, 6, 1), LocalTime.parse("09:00"), LocalTime.parse("10:30")),
            List.of(new Classroom("A", 30))
    );

    private static final PartialSchedule EMPTY_PS = new PartialSchedule();

    @Test
    @DisplayName("ConstraintSet — hiç kural yoksa her zaman geçer")
    void noConstraints_alwaysPasses() {
        ConstraintSet cs = new ConstraintSet();
        assertTrue(cs.ok(EMPTY_PS, DUMMY_CAND));
        assertTrue(cs.explain(EMPTY_PS, DUMMY_CAND).isEmpty());
    }

    @Test
    @DisplayName("ConstraintSet — ihlal eden kural mesajını döndürür")
    void failingConstraint_returns_message() {
        Constraint alwaysFail = new Constraint() {
            @Override
            public boolean test(PartialSchedule s, Candidate c) { return false; }
            @Override
            public String getViolationMessage() { return "TEST_ERROR"; }
        };

        ConstraintSet cs = new ConstraintSet().add(alwaysFail);
        assertFalse(cs.ok(EMPTY_PS, DUMMY_CAND));
        assertEquals(1, cs.explain(EMPTY_PS, DUMMY_CAND).size());
        assertEquals("TEST_ERROR", cs.explain(EMPTY_PS, DUMMY_CAND).get(0));
    }

    @Test
    @DisplayName("ConstraintSet — birden fazla ihlal tüm mesajları döndürür")
    void multipleConstraints_allMessages() {
        Constraint fail1 = new Constraint() {
            @Override public boolean test(PartialSchedule s, Candidate c) { return false; }
            @Override public String getViolationMessage() { return "ERROR_1"; }
        };
        Constraint fail2 = new Constraint() {
            @Override public boolean test(PartialSchedule s, Candidate c) { return false; }
            @Override public String getViolationMessage() { return "ERROR_2"; }
        };

        ConstraintSet cs = new ConstraintSet().add(fail1).add(fail2);
        var msgs = cs.explain(EMPTY_PS, DUMMY_CAND);
        assertEquals(2, msgs.size());
        assertTrue(msgs.contains("ERROR_1"));
        assertTrue(msgs.contains("ERROR_2"));
    }
}
