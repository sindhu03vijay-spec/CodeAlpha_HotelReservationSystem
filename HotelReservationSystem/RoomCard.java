import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

/**
 * A single visual "room tile" used in the booking grid.
 * Color-coded by category, shows price and availability, and highlights
 * with a gold border when selected.
 */
public class RoomCard extends JPanel {

    private final Room room;
    private boolean selected = false;
    private final Color accent;

    public RoomCard(Room room, Consumer<RoomCard> onSelect) {
        this.room = room;
        this.accent = Theme.categoryColor(room.getCategory());
        setOpaque(false);
        setPreferredSize(new Dimension(190, 140));
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(14, 14, 14, 14));
        setCursor(room.isAvailable() ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                : Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        JLabel roomNo = new JLabel("Room " + room.getRoomNumber());
        roomNo.setFont(Theme.FONT_HEADER);
        roomNo.setForeground(Theme.NAVY_DARK);

        Theme.Badge categoryBadge = new Theme.Badge(room.getCategory().toUpperCase(), accent);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(roomNo, BorderLayout.WEST);

        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgeWrap.setOpaque(false);
        badgeWrap.add(categoryBadge);
        top.add(badgeWrap, BorderLayout.EAST);

        JLabel price = new JLabel("\u20B9" + (int) room.getPricePerNight() + " / night");
        price.setFont(Theme.FONT_BODY_B);
        price.setForeground(Theme.NAVY);

        JLabel status = new JLabel(room.isAvailable() ? "\u25CF  Available" : "\u25CF  Booked");
        status.setFont(Theme.FONT_SMALL);
        status.setForeground(room.isAvailable() ? Theme.SUCCESS : Theme.DANGER);

        JPanel middle = new JPanel();
        middle.setOpaque(false);
        middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
        middle.add(Box.createVerticalGlue());
        middle.add(price);
        middle.add(Box.createVerticalStrut(4));
        middle.add(status);
        middle.add(Box.createVerticalGlue());

        add(top, BorderLayout.NORTH);
        add(middle, BorderLayout.CENTER);

        if (room.isAvailable()) {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    onSelect.accept(RoomCard.this);
                }
            });
        }
    }

    public Room getRoom() {
        return room;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 16;
        RoundRectangle2D shape = new RoundRectangle2D.Double(2, 2, getWidth() - 4, getHeight() - 4, arc, arc);

        // shadow
        g2.setColor(new Color(16, 27, 46, 22));
        g2.fill(new RoundRectangle2D.Double(3, 5, getWidth() - 4, getHeight() - 4, arc, arc));

        // body
        if (!room.isAvailable()) {
            g2.setColor(new Color(238, 238, 240));
        } else if (selected) {
            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 26));
        } else {
            g2.setColor(Theme.PAPER);
        }
        g2.fill(shape);

        // left accent stripe
        g2.setColor(accent);
        g2.fill(new RoundRectangle2D.Double(2, 2, 7, getHeight() - 4, arc, arc));
        g2.fillRect(6, 2, 5, getHeight() - 4);

        // border
        g2.setStroke(new BasicStroke(selected ? 2.4f : 1f));
        g2.setColor(selected ? Theme.GOLD : new Color(225, 228, 235));
        g2.draw(shape);

        g2.dispose();
        super.paintComponent(g);
    }
}
