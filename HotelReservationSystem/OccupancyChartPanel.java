import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A small hand-drawn bar chart (no external charting library) showing,
 * per room category, how many rooms are booked vs total.
 * Map key = category name, value = int[]{ booked, total }.
 */
public class OccupancyChartPanel extends Theme.RoundedCard {

    private Map<String, int[]> data = new LinkedHashMap<>();

    public OccupancyChartPanel() {
        super(18);
        setPreferredSize(new Dimension(400, 230));
        setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
    }

    public void setData(Map<String, int[]> data) {
        this.data = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int padTop = 46;
        int padBottom = 34;
        int padLeft = 26;
        int padRight = 22;

        g2.setFont(Theme.FONT_HEADER);
        g2.setColor(Theme.NAVY_DARK);
        g2.drawString("Occupancy by Category", padLeft, 26);

        int chartW = getWidth() - padLeft - padRight - 24;
        int chartH = getHeight() - padTop - padBottom;
        int n = Math.max(data.size(), 1);
        int gap = 36;
        int barWidth = Math.max(28, (chartW - gap * (n - 1)) / n);

        int x = padLeft + 8;
        int baseY = padTop + chartH;

        for (Map.Entry<String, int[]> entry : data.entrySet()) {
            String category = entry.getKey();
            int booked = entry.getValue()[0];
            int total = Math.max(entry.getValue()[1], 1);
            double ratio = (double) booked / total;

            Color accent = Theme.categoryColor(category);

            // background track (full height = total rooms)
            RoundRectangle2D track = new RoundRectangle2D.Double(x, padTop, barWidth, chartH, 10, 10);
            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30));
            g2.fill(track);

            // filled portion (booked rooms), grown from the bottom
            int filledH = (int) Math.round(chartH * ratio);
            if (filledH > 0) {
                RoundRectangle2D fill = new RoundRectangle2D.Double(x, baseY - filledH, barWidth, filledH, 10, 10);
                GradientPaint gp = new GradientPaint(x, baseY - filledH, accent.brighter(), x, baseY, accent);
                g2.setPaint(gp);
                g2.fill(fill);
            }

            // count label above bar
            g2.setFont(Theme.FONT_BODY_B);
            g2.setColor(Theme.NAVY_DARK);
            String countText = booked + "/" + total;
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(countText, x + (barWidth - fm.stringWidth(countText)) / 2, padTop - 8);

            // category label below bar
            g2.setFont(Theme.FONT_SMALL);
            g2.setColor(Theme.MUTED_TEXT);
            FontMetrics fmSmall = g2.getFontMetrics();
            g2.drawString(category, x + (barWidth - fmSmall.stringWidth(category)) / 2, baseY + 18);

            x += barWidth + gap;
        }

        g2.dispose();
    }
}
