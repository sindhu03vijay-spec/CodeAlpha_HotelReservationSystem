import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

/**
 * A hand-drawn circular "donut" progress ring showing overall occupancy
 * percentage — a quick, eye-catching way to see how full the hotel is
 * without reading a table.
 */
public class DonutChart extends Theme.RoundedCard {

    private int percent = 0;
    private String caption = "Occupied Today";

    public DonutChart() {
        super(18);
        setPreferredSize(new Dimension(260, 230));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
    }

    public void setPercent(int percent) {
        this.percent = Math.max(0, Math.min(100, percent));
        repaint();
    }

    public void setCaption(String caption) {
        this.caption = caption;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setFont(Theme.FONT_HEADER);
        g2.setColor(Theme.NAVY_DARK);
        g2.drawString("Overall Occupancy", 20, 26);

        int size = Math.min(getWidth() - 40, getHeight() - 80);
        int cx = (getWidth() - size) / 2;
        int cy = 46;
        int thickness = Math.max(14, size / 9);

        Ellipse2D.Double track = new Ellipse2D.Double(cx, cy, size, size);
        g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(212, 175, 55, 35));
        g2.draw(track);

        double angle = 360.0 * percent / 100.0;
        Arc2D.Double arc = new Arc2D.Double(cx, cy, size, size, 90, -angle, Arc2D.OPEN);
        g2.setColor(Theme.GOLD);
        g2.draw(arc);

        String pctText = percent + "%";
        g2.setFont(Theme.FONT_BIG_NUM);
        g2.setColor(Theme.NAVY_DARK);
        FontMetrics fm = g2.getFontMetrics();
        int tx = cx + (size - fm.stringWidth(pctText)) / 2;
        int ty = cy + size / 2 + fm.getAscent() / 3;
        g2.drawString(pctText, tx, ty);

        g2.setFont(Theme.FONT_SMALL);
        g2.setColor(Theme.MUTED_TEXT);
        FontMetrics fmSmall = g2.getFontMetrics();
        g2.drawString(caption, cx + (size - fmSmall.stringWidth(caption)) / 2, cy + size + 22);

        g2.dispose();
    }
}
