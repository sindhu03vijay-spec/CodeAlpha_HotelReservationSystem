# Aurelia Grand Hotel — Reservation System

A pure Java (Swing) hotel reservation system built for the CodeAlpha Java
Programming Internship — Task 4.

## Task conditions covered
- Room categorization: Standard / Deluxe / Suite
- Search, book, and cancel reservations
- Payment simulation (Card / UPI / Cash) with a booking-details view
- OOP design (`Room`, `Reservation`, `Hotel`) + File I/O persistence
  (`rooms.dat`, `reservations.dat` — auto-saved and reloaded)

## Extras for polish
- Dashboard: time-aware greeting, live clock, KPI cards with icon badges,
  a live occupancy donut ring, a hand-drawn bar chart by category, and a
  recent-bookings feed — all on a soft gradient backdrop
- Icon-labeled tabs, color-coded clickable room cards, badge-styled table

## How to run
```
javac *.java
java HotelReservationGUI
```
(On Windows Command Prompt, wildcards aren't expanded — either list every
.java filename explicitly, or run the same command from PowerShell instead.)

## Files
- `Room.java`, `Reservation.java`, `Hotel.java` — core OOP model + File I/O
- `Theme.java` — shared color palette, fonts, and custom-painted components
- `RoomCard.java` — clickable, color-coded room tile
- `StatCard.java` — dashboard KPI tile with icon badge
- `DonutChart.java` — hand-drawn occupancy ring
- `OccupancyChartPanel.java` — hand-drawn bar chart by category
- `HotelReservationGUI.java` — main window, 4 tabs: Dashboard, Book a Room,
  My Reservations, Room Overview

When submitting to GitHub for CodeAlpha, name the repository
`CodeAlpha_HotelReservationSystem` per the internship instructions — that
naming requirement is about the repo, not the app's own branding.
