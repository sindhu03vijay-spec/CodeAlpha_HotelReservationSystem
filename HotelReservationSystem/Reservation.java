import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a single hotel reservation made by a guest for a room.
 */
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int reservationCounter = 1000;

    private int reservationId;
    private String guestName;
    private String guestPhone;
    private int roomNumber;
    private String roomCategory;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private double totalAmount;
    private boolean paymentDone;
    private String status; // CONFIRMED, CANCELLED

    public Reservation(String guestName, String guestPhone, Room room,
                        LocalDate checkIn, LocalDate checkOut) {
        this.reservationId = ++reservationCounter;
        this.guestName = guestName;
        this.guestPhone = guestPhone;
        this.roomNumber = room.getRoomNumber();
        this.roomCategory = room.getCategory();
        this.checkIn = checkIn;
        this.checkOut = checkOut;

        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) nights = 1;
        this.totalAmount = nights * room.getPricePerNight();
        this.paymentDone = false;
        this.status = "CONFIRMED";
    }

    public int getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getRoomCategory() {
        return roomCategory;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public boolean isPaymentDone() {
        return paymentDone;
    }

    public void setPaymentDone(boolean paymentDone) {
        this.paymentDone = paymentDone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /** Keeps the static ID counter ahead of any IDs loaded from file. */
    public static void syncCounter(int lastUsedId) {
        if (lastUsedId > reservationCounter) {
            reservationCounter = lastUsedId;
        }
    }

    @Override
    public String toString() {
        return "Reservation #" + reservationId + " | " + guestName + " | Room " + roomNumber
                + " (" + roomCategory + ") | " + checkIn + " to " + checkOut
                + " | Rs." + totalAmount + " | Payment: " + (paymentDone ? "PAID" : "PENDING")
                + " | " + status;
    }
}
