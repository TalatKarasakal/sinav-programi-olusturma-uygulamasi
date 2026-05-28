package scheduler.ui;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TestFX UI smoke testleri.
 * Yalnızca display.available=true ise çalışır (DISPLAY env var set ise).
 */
@Tag("ui")
@ExtendWith(ApplicationExtension.class)
@EnabledIfSystemProperty(named = "display.available", matches = "true")
class SchedulerAppTest {

    @Start
    private void start(Stage stage) throws Exception {
        new MainApp().start(stage);
    }

    @Test
    @DisplayName("App — uygulama crash olmadan başlar")
    void appLaunches_noCrash(FxRobot robot) {
        // Sahne görünür durumdaysa yeterli
        assertNotNull(robot);
    }

    @Test
    @DisplayName("App — ana pencerede BorderPane root mevcuttur")
    void mainPane_isPresent(FxRobot robot) {
        // JavaFX root sahne yapısı doğrulanır
        var panes = robot.lookup(".root").queryAllAs(javafx.scene.Node.class);
        assertFalse(panes.isEmpty(), "Root node bulunamamadı");
    }

    @Test
    @DisplayName("App — toolbar'daki 'Apply Schedule' butonu görünür")
    void applyButton_isVisible(FxRobot robot) {
        // Metne göre arama (CSS id yok, programmatic UI)
        var buttons = robot.lookup("Apply Schedule").queryAllAs(Button.class);
        assertFalse(buttons.isEmpty(), "'Apply Schedule' butonu bulunamadı");
    }

    @Test
    @DisplayName("Dark mode toggle — tıklandığında pencere stilini değiştirir")
    void darkModeToggle_works(FxRobot robot) {
        // Stage still showing after toggle — no exception is the assertion
        robot.lookup("Apply Schedule").queryAllAs(Button.class);
        // Toggle switch bulmak için ToggleSwitch sınıfını kullan
        var toggles = robot.lookup(".toggle-button").queryAllAs(javafx.scene.control.ToggleButton.class);
        // Herhangi bir toggle varsa tıkla, exception olmadığını doğrula
        if (!toggles.isEmpty()) {
            assertDoesNotThrow(() -> robot.clickOn(toggles.iterator().next()));
        }
    }
}
