# Planer

Eigene Android-App mit den Kernfunktionen von TickTick, offline-first.

**Stack:** Kotlin, Jetpack Compose (Material 3), Room, AlarmManager für Erinnerungen. minSdk 26, targetSdk 35.

## Stand (Gerüst v0.1)

- Listen anlegen, Standardliste „Eingang“
- Aufgaben mit Titel, Notizen, Priorität, Fälligkeitsdatum, Erinnerung (Benachrichtigung)
- Checklisten/Unteraufgaben, Tags
- Schnelleingabe: `Milch kaufen #einkauf !2` setzt Tag und Priorität
- Ansichten: Heute (inkl. überfällig), Listen, Kalender (Wochenleiste)

## Bauen

Android Studio öffnen und `app` starten, oder:

```
./gradlew assembleDebug
```

Die GitHub-Action in `.github/workflows/build.yml` baut bei jedem Push eine Debug-APK.

## Später

Wiederholende Aufgaben, Gewohnheiten, Pomodoro/Fokus, Widgets, Monatskalender, Suche, Sync/Backup.
