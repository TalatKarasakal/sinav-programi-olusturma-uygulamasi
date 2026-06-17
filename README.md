> **FORK ATFI**
>
> Bu repo [arjinozceylan/SE-302-PROJE](https://github.com/arjinozceylan/SE-302-PROJE) reposunun kişisel fork'udur.
> Orijinal ekip: Arjin Özceylan, Gözde Yılıkyılmaz, Sıla Karabağ, Talat Karasakal ve Ayşenur İşler.
> Orijinal projenin fikri, uygulama temeli, algoritma, arayüz, veritabanı, dışa aktarma ve test/dokümantasyon katkıları bu ekibe aittir.
> Bu fork'taki ek geliştirmeler `enhanced-personal-fork` branch'inde işaretlidir ve orijinal projeyi temsil etmez.

---

<div align="center">

# 📅 Exam Scheduler System

> **Course:** SE 302 – Principles of Software Engineering  
> **Department:** Computer Engineering, Izmir University of Economics  
> **Current Version:** 1.0.0  
> **Status:** Completed

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-UI-4285F4?style=for-the-badge&logo=java&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-Build-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-Database-003B57?style=for-the-badge&logo=sqlite&logoColor=white)

</div>

---

## 📖 Project Overview

**Exam Scheduler System** is a sophisticated desktop application designed to automate the complex **University Exam Timetabling Problem**. Unlike simple manual schedulers, this system uses an intelligent **Greedy Algorithm enhanced with Backtracking** to generate conflict-free schedules that respect hard constraints (room capacity, student clashes) and optimize resource usage.

The project features a **Layered Architecture** (Model-View-Controller-DAO pattern) ensuring separation of concerns, maintainability, and scalability.

---

## Bu fork'taki geliştirmeler

Bu bölüm, orijinal proje ile bu kişisel fork'taki ek işleri birbirinden ayırmak için tutulur. Aşağıdaki maddeler `enhanced-personal-fork` branch'inde yapılan ek geliştirmelerdir:

* Gradle yapılandırmasının güncellenmesi ve wrapper dosyalarının modernleştirilmesi.
* JavaFX 21 uyumluluğu, modüler çalışma yapısı ve paketleme hazırlıkları.
* `jlink` / `jpackage` desteğiyle Windows, macOS ve Linux için yerel kurulum paketi üretimi.
* GitHub Actions üzerinde Gradle CI ve release workflow düzenlemeleri.
* Çekirdek algoritma, kısıtlar, model, CSV yükleme ve veritabanı katmanı için JUnit testleri.
* JavaFX arayüzü için TestFX smoke testi.
* `scheduler.db` dosyasının kaynak kontrolden çıkarılması ve `.gitignore` kapsamının güncellenmesi.
* Release indirme bağlantılarının README'ye eklenmesi.

---

## 📸 Demo & Screenshots

### 🎥 Application In Action

![App Demo](https://github.com/arjinozceylan/SE-302-PROJE/blob/main/screenshots/Kay%C4%B1t%202025-12-21%20185859.gif)

### 🖥️ User Interface Gallery

| **Main Dashboard (Dark Mode)** | **Advanced Filtering** |
|:---:|:---:|
| ![Main View](https://github.com/arjinozceylan/SE-302-PROJE/blob/main/screenshots/WhatsApp%20Image%202025-12-21%20at%2018.15.04.jpeg) | ![Filter View](https://github.com/arjinozceylan/SE-302-PROJE/blob/main/screenshots/WhatsApp%20Image%202025-12-21%20at%2018.15.00.jpeg) |

| **Conflict Analysis & Logs** | **Excel Export** |
|:---:|:---:|
| ![Conflict Graph](https://github.com/arjinozceylan/SE-302-PROJE/blob/main/screenshots/WhatsApp%20Image%202025-12-21%20at%2019.22.33.jpeg) | ![Export Feature](https://github.com/arjinozceylan/SE-302-PROJE/blob/main/screenshots/WhatsApp%20Image%202025-12-21%20at%2018.14.57.jpeg) |

---

## ✨ Key Features & Technical Highlights

### 🧠 Advanced Scheduling Algorithm
* **Conflict Analysis & Prioritization:** Instead of a simple weighted graph, the system constructs a **Conflict Graph** to map incompatibilities. It prioritizes courses based on their **Conflict Degree** (number of conflicting courses) and **Enrollment Size**, ensuring the most difficult exams are placed first to prevent deadlocks.
* **Smart Backtracking:** If the scheduler hits a dead-end, it intelligently backtracks, removing conflicting placements and retrying alternative paths to find a valid solution.
* **Deterministic Seeding:** Uses a fixed seed (`42L`) for random operations, ensuring that the same input always produces the exact same schedule (Reproducibility).

### ⚙️ Robust Constraint Engine
The system enforces rules using a flexible `Constraint` interface:
* **No Student Clash:** A student cannot attend two exams simultaneously.
* **Minimum Gap Check:** Enforces a configurable break (e.g., 60 mins) between exams for the same student.
* **Daily Exam Limits:** Prevents scheduling more than X exams (default: 2) per student per day.
* **Room Capacity & Balancing:** Automatically generates optimal room combinations (Single, Double, or Triple room sets) to fit enrollment counts.

### 🎨 Modern User Interface (JavaFX)
* **MasterList Pattern:** Implements a design pattern in the search module to filter data views without losing the original dataset.
* **Theme Support:** Native **Dark Mode** and **Light Mode** switching.
* **Drag & Drop Import:** Seamlessly import CSV data files.
* **Responsive Tables:** Dynamic column resizing for optimal readability on different screen sizes.

### 💾 Data Persistence & Export
* **SQLite Integration:** Automatically initializes the local database schema (`scheduler.db`) via `DBManager` and stores generated schedules.
* **Excel-Compatible Export:** Exports schedules and student lists to CSV format with **BOM support** (correctly displays special characters in Excel).

---

## 🏗️ Project Architecture

The codebase is organized into modular packages to ensure separation of concerns:

```bash
scheduler/
├── assign/       # Student seating logic (Randomized but deterministic distribution)
├── config/       # System configuration constants (Grid size, timeouts, seeds)
├── constraints/  # Rule engine interface and implementations (Hard/Soft rules)
├── core/         # Core algorithms (Conflict Graph, Backtracking Scheduler)
├── dao/          # Database Access Layer (SQLite Connection & Queries)
├── io/           # Input/Output operations (CSV Parsing & Sanitization)
├── model/        # Immutable Domain entities (Student, Course, Timeslot)
└── ui/           # JavaFX controllers, views, and theme logic
```
---

## 📥 İndir / Download

| Platform | İndir |
|----------|-------|
| Windows | [.msi](https://github.com/TalatKarasakal/sinav-programi-olusturma-uygulamasi/releases/latest/download/ExamScheduler.msi) |
| macOS | [.dmg](https://github.com/TalatKarasakal/sinav-programi-olusturma-uygulamasi/releases/latest/download/ExamScheduler.dmg) |
| Linux (Debian/Ubuntu) | [.deb](https://github.com/TalatKarasakal/sinav-programi-olusturma-uygulamasi/releases/latest/download/examscheduler.deb) |

## 🚀 How to Run

### Prerequisites
* **Java Development Kit (JDK):** Version 17 or higher.
* **Gradle:** Version 8.0 or higher (Wrapper included).

### Option 1: Run as Executable (Recommended)
1.  Download the latest release (`ExamScheduler.exe` or `.jar`).
2.  Ensure input CSV files are ready (Sample data is provided).
3.  Double-click the executable to launch.

### Option 2: Developer Mode (IntelliJ IDEA)
1.  Clone this repository.
2.  Open the project in **IntelliJ IDEA**.
3.  Let Gradle sync the dependencies (JavaFX, SQLite JDBC).
4.  Run the `Launcher.java` class located in `src/main/java/scheduler/ui/`.

---

## 📥 Input & 📤 Output

### Input Data (CSV)
The system requires four CSV files to operate:
* `Students List` (id)
* `Courses List` (id, duration)
* `Classrooms List` (id, capacity)
* `Enrollments` (student_id, course_id)

### Output Data
* **On Screen:** Interactive schedule tables (Student view, Exam view, Day view).
* **File Export:** `Student_Schedule.csv` or `Full_Schedule.csv` (Formatted for Excel).
* **Database:** Local `scheduler.db` file preserving the state.

---

## 👥 Contributors

This project was collaboratively developed by:

| Team Member | Role & Responsibility |
| :--- | :--- |
| **Arjin Özceylan** | Core Algorithm & Architecture |
| **Gözde Yılıkyılmaz** | Database Logic & Export Features |
| **Sıla Karabağ** | Algorithm Optimization, UI Development & Search Functionality |
| **Talat Karasakal** | UX Design & Input Handling |
| **Ayşenur İşler** | Documentation & Testing Strategy |

---

## 📝 License

Orijinal [arjinozceylan/SE-302-PROJE](https://github.com/arjinozceylan/SE-302-PROJE) reposunda bir `LICENSE` dosyası bulunmamaktadır. Bu nedenle bu fork'a ayrıca bir lisans eklenmemiştir.

Bu proje **Izmir University of Economics** bünyesindeki **SE 302** dersi kapsamında eğitim amaçlı hazırlanmıştır. Kullanım, yeniden dağıtım veya lisanslama için orijinal proje sahiplerinden izin alınması gerekebilir.
