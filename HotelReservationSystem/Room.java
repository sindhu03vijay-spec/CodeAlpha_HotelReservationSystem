import java.io.Serializable;

/**
 * Represents a hotel room.
 * Implements Serializable so Room objects can be written to file via ObjectOutputStream.
 */
public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    private int roomNumber;
    private String category;      // Standard, Deluxe, Suite
    private double pricePerNight;
    private boolean available;

    public Room(int roomNumber, String category, double pricePerNight) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.pricePerNight = pricePerNight;
        this.available = true;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getCategory() {
        return category;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Room #" + roomNumber + " [" + category + "] - Rs." + pricePerNight + "/night - "
                + (available ? "Available" : "Booked");
    }
}
