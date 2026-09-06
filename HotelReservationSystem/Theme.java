import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Central place for the app's color palette, fonts, and small reusable
 * "designed" Swing components (rounded buttons, gradient panels, badges).
 * Keeping all styling here means every screen shares one consistent look.
 */
public final class Theme {

    // ----- Palette: deep navy + gold, luxury-hotel feel -----
    public static final Color NAVY_DARK   = new Color(16, 27, 46);
    public static final Color NAVY        = new Color(24, 42, 71);
    public static final Color NAVY_LIGHT  = new Color(37, 63, 103);
    public static final Color GOLD        = new Color(212, 175, 55);
    public static final Color GOLD_LIGHT  = new Color(232, 200, 110);
    public static final Color CREAM       = new Color(250, 247, 240);
    public static final Color PAPER       = new Color(255, 255, 255);

    public static final Color STANDARD    = new Color(74, 144, 226);
    public static final Color DELUXE      = new Color(155, 89, 182);
    public static final Color SUITE       = new Color(212, 160, 23);

    public static final Color SUCCESS     = new Color(39, 174, 96);
    public static final Color WARNING     = new Color(243, 156, 18);
    public static final Color DANGER      = new Color(231, 76, 60);
    public static final Color MUTED_TEXT  = new Color(120, 128, 140);

    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_SUB     = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_HEADER  = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BODY_B  = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BIG_NUM = new Font("Segoe UI", Font.BOLD, 30);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 12);

    private Theme() {
    }

    public static Color categoryColor(String category) {
        switch (category) {
            case "Deluxe": return DELUXE;
            case "Suite": return SUITE;
            default: return STANDARD;
        }
    }

    /** Vertical gradient panel — used as the backdrop behind headers and cards. */
    public static class GradientPanel extends JPanel {
        private final Color top;
        private final Color bottom;

        public GradientPanel(Color top, Color bottom) {
            this.top = top;
            this.bottom = bottom;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, top, 0, getHeight(), bottom);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    /** A flat-white rounded card with a soft drop shadow, used everywhere as a container. */
    public static class RoundedCard extends JPanel {
        private final int radius;
        private Color bg = PAPER;

        public RoundedCard(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        public void setCardBackground(Color c) {
            this.bg = c;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // soft shadow
            g2.setColor(new Color(16, 27, 46, 28));
            g2.fill(new RoundRectangle2D.Double(3, 5, getWidth() - 6, getHeight() - 6, radius, radius));
            // card body
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 6, getHeight() - 8, radius, radius));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Pill-shaped colored status badge, e.g. PAID / PENDING / CANCELLED. */
    public static class Badge extends JLabel {
        private Color color;

        public Badge(String text, Color color) {
            super(text, SwingConstants.CENTER);
            this.color = color;
            setFont(FONT_SMALL.deriveFont(Font.BOLD));
            setForeground(Color.WHITE);
            setOpaque(false);
            setBorder(new EmptyBorder(3, 10, 3, 10));
        }

        public void setColor(Color color) {
            this.color = color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight()));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** A toggleable pill "chip" — used for the category filter row (All / Standard / Deluxe / Suite). */
    public static class Chip extends JToggleButton {
        private final Color accent;

        public Chip(String text, Color accent) {
            super(text);
            this.accent = accent;
            setFont(FONT_BODY_B);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(new EmptyBorder(7, 18, 7, 18));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            updateColors();
            addChangeListener(e -> updateColors());
        }

        private void updateColors() {
            setForeground(isSelected() ? Color.WHITE : NAVY);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int arc = getHeight();
            if (isSelected()) {
                g2.setColor(accent);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, arc, arc));
            } else {
                g2.setColor(new Color(0, 0, 0, 0));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, arc, arc));
                g2.setStroke(new BasicStroke(1.4f));
                g2.setColor(new Color(200, 205, 214));
                g2.draw(new RoundRectangle2D.Double(0.7, 0.7, getWidth() - 2.2, getHeight() - 2.2, arc, arc));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** A rounded, gradient-filled button with a hover glow — replaces the default flat JButton. */
    public static class PillButton extends JButton {
        private final Color base;
        private final Color hover;
        private boolean isHovering = false;
        private final boolean filled;

        public PillButton(String text, Color base, Color hover) {
            this(text, base, hover, true);
        }

        public PillButton(String text, Color base, Color hover, boolean filled) {
            super(text);
            this.base = base;
            this.hover = hover;
            this.filled = filled;
            setFont(FONT_BODY_B);
            setForeground(filled ? Color.WHITE : base);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(new EmptyBorder(10, 22, 10, 22));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovering = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovering = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int arc = getHeight();
            if (filled) {
                g2.setColor(isHovering ? hover : base);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, arc, arc));
            } else {
                g2.setColor(isHovering ? new Color(base.getRed(), base.getGreen(), base.getBlue(), 25) : new Color(0, 0, 0, 0));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, arc, arc));
                g2.setStroke(new BasicStroke(1.6f));
                g2.setColor(base);
                g2.draw(new RoundRectangle2D.Double(0.8, 0.8, getWidth() - 2.4, getHeight() - 2.4, arc, arc));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
