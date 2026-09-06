import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Core business logic: manages the list of rooms and reservations,
 * and handles saving/loading data to disk using File I/O (object serialization).
 */
public class Hotel implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String ROOMS_FILE = "rooms.dat";
    private static final String RESERVATIONS_FILE = "reservations.dat";

    private List<Room> rooms;
    private List<Reservation> reservations;

    public Hotel() {
        rooms = new ArrayList<>();
        reservations = new ArrayList<>();
    }

    // ---------- Setup ----------

    /** Creates a default set of rooms if no saved data exists yet. */
    public void initializeDefaultRooms() {
        int roomNo = 101;
        for (int i = 0; i < 5; i++) rooms.add(new Room(roomNo++, "Standard", 2000));
        for (int i = 0; i < 3; i++) rooms.add(new Room(roomNo++, "Deluxe", 3500));
        for (int i = 0; i < 2; i++) rooms.add(new Room(roomNo++, "Suite", 6000));
    }

    // ---------- Room operations ----------

    public List<Room> getAllRooms() {
        return rooms;
    }

    public List<Room> getAvailableRooms() {
        List<Room> available = new ArrayList<>();
        for (Room r : rooms) {
            if (r.isAvailable()) available.add(r);
        }
        return available;
    }

    public List<Room> getAvailableRoomsByCategory(String category) {
        List<Room> available = new ArrayList<>();
        for (Room r : rooms) {
            if (r.isAvailable() && r.getCategory().equalsIgnoreCase(category)) {
                available.add(r);
            }
        }
        return available;
    }

    public Room findRoomByNumber(int roomNumber) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomNumber) return r;
        }
        return null;
    }

    // ---------- Reservation operations ----------

    public Reservation bookRoom(String guestName, String guestPhone, Room room,
                                 LocalDate checkIn, LocalDate checkOut) {
        if (!room.isAvailable()) {
            throw new IllegalStateException("Room " + room.getRoomNumber() + " is not available.");
        }
        Reservation reservation = new Reservation(guestName, guestPhone, room, checkIn, checkOut);
        room.setAvailable(false);
        reservations.add(reservation);
        return reservation;
    }

    public boolean cancelReservation(int reservationId) {
        for (Reservation res : reservations) {
            if (res.getReservationId() == reservationId && res.getStatus().equals("CONFIRMED")) {
                res.setStatus("CANCELLED");
                Room room = findRoomByNumber(res.getRoomNumber());
                if (room != null) room.setAvailable(true);
                return true;
            }
        }
        return false;
    }

    public boolean processPayment(int reservationId) {
        for (Reservation res : reservations) {
            if (res.getReservationId() == reservationId && res.getStatus().equals("CONFIRMED")) {
                res.setPaymentDone(true);
                return true;
            }
        }
        return false;
    }

    public List<Reservation> getAllReservations() {
        return reservations;
    }

    public Reservation findReservationById(int id) {
        for (Reservation res : reservations) {
            if (res.getReservationId() == id) return res;
        }
        return null;
    }

    // ---------- Stats (used by the dashboard) ----------

    public int getTotalRoomCount() {
        return rooms.size();
    }

    public int getAvailableRoomCount() {
        return getAvailableRooms().size();
    }

    public int getBookedRoomCount() {
        return rooms.size() - getAvailableRoomCount();
    }

    public double getTotalRevenueCollected() {
        double total = 0;
        for (Reservation r : reservations) {
            if (r.isPaymentDone() && r.getStatus().equals("CONFIRMED")) {
                total += r.getTotalAmount();
            }
        }
        return total;
    }

    /** category -> {bookedCount, totalCount}, in a stable Standard/Deluxe/Suite order. */
    public Map<String, int[]> getOccupancyByCategory() {
        Map<String, int[]> stats = new LinkedHashMap<>();
        stats.put("Standard", new int[]{0, 0});
        stats.put("Deluxe", new int[]{0, 0});
        stats.put("Suite", new int[]{0, 0});

        for (Room r : rooms) {
            int[] counts = stats.get(r.getCategory());
            if (counts == null) {
                counts = new int[]{0, 0};
                stats.put(r.getCategory(), counts);
            }
            counts[1]++;
            if (!r.isAvailable()) counts[0]++;
        }
        return stats;
    }

    // ---------- File I/O (persistence) ----------

    @SuppressWarnings("unchecked")
    public void loadData() {
        File roomsFile = new File(ROOMS_FILE);
        File reservationsFile = new File(RESERVATIONS_FILE);

        if (roomsFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(roomsFile))) {
                rooms = (List<Room>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Could not load rooms.dat: " + e.getMessage());
                initializeDefaultRooms();
            }
        } else {
            initializeDefaultRooms();
        }

        if (reservationsFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(reservationsFile))) {
                reservations = (List<Reservation>) ois.readObject();
                int maxId = 0;
                for (Reservation r : reservations) {
                    if (r.getReservationId() > maxId) maxId = r.getReservationId();
                }
                Reservation.syncCounter(maxId);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Could not load reservations.dat: " + e.getMessage());
                reservations = new ArrayList<>();
            }
        }
    }

    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ROOMS_FILE))) {
            oos.writeObject(rooms);
        } catch (IOException e) {
            System.err.println("Could not save rooms.dat: " + e.getMessage());
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RESERVATIONS_FILE))) {
            oos.writeObject(reservations);
        } catch (IOException e) {
            System.err.println("Could not save reservations.dat: " + e.getMessage());
        }
    }
}
