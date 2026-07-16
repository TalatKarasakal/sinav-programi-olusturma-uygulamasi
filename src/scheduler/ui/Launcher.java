package scheduler.ui;

public class Launcher {
    public static void main(String[] args) {
        // macOS Dock'ta "java" yerine uygulama adı görünsün (JavaFX/AWT
        // başlamadan ÖNCE ayarlanmalı).
        System.setProperty("apple.awt.application.name", "Sınav Programı");
        // Bu sınıf Application'dan türemediği için Java hata vermez.
        // Buradan asıl uygulamayı çağırdığımızda kütüphaneler doğru yüklenir.
        MainApp.main(args);
    }
}
