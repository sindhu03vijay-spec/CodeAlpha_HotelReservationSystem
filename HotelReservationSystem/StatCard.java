import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * A compact "stat tile" for the dashboard — icon badge, big number, label,
 * and a colored accent stripe.
 */
public class StatCard extends Theme.RoundedCard {

    private final JLabel valueLabel;

    public StatCard(String icon, String label, String value, Color accent) {
        super(18);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(210, 118));
        setBorder(BorderFactory.createEmptyBorder(16, 18, 14, 16));

        JPanel stripe = new JPanel();
        stripe.setPreferredSize(new Dimension(5, 10));
        stripe.setBackground(accent);

        IconBadge iconBadge = new IconBadge(icon, accent);
        iconBadge.setPreferredSize(new Dimension(42, 42));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        valueLabel = new JLabel(value);
        valueLabel.setFont(Theme.FONT_BIG_NUM);
        valueLabel.setForeground(Theme.NAVY_DARK);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel captionLabel = new JLabel(label);
        captionLabel.setFont(Theme.FONT_SMALL);
        captionLabel.setForeground(Theme.MUTED_TEXT);
        captionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(Box.createVerticalGlue());
        textPanel.add(valueLabel);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(captionLabel);
        textPanel.add(Box.createVerticalGlue());

        JPanel iconRow = new JPanel(new BorderLayout());
        iconRow.setOpaque(false);
        iconRow.add(iconBadge, BorderLayout.NORTH);

        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setOpaque(false);
        centerWrap.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 0));
        centerWrap.add(textPanel, BorderLayout.CENTER);

        add(stripe, BorderLayout.WEST);
        add(iconRow, BorderLayout.EAST);
        add(centerWrap, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    /** A small circular tinted badge holding a single glyph/icon character. */
    private static class IconBadge extends JComponent {
        private final String icon;
        private final Color accent;

        IconBadge(String icon, Color accent) {
            this.icon = icon;
            this.accent = accent;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 34));
            g2.fill(new Ellipse2D.Double(0, 0, getWidth() - 1, getHeight() - 1));
            g2.setFont(Theme.FONT_HEADER.deriveFont(18f));
            g2.setColor(accent.darker());
            FontMetrics fm = g2.getFontMetrics();
            int tx = (getWidth() - fm.stringWidth(icon)) / 2;
            int ty = (getHeight() + fm.getAscent()) / 2 - 3;
            g2.drawString(icon, tx, ty);
            g2.dispose();
        }
    }
}
