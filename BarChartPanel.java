import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class BarChartPanel extends JPanel {

    private Map<String, Integer> data;
    private String title;
    private BarClickListener listener;

    private Map<String, Rectangle> barAreas = new HashMap<>();

    public BarChartPanel() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (Map.Entry<String, Rectangle> entry : barAreas.entrySet()) {
                    if (entry.getValue().contains(e.getPoint())) {
                        if (listener != null) {
                            listener.onBarClick(entry.getKey());
                        }
                        break;
                    }
                }
            }
        });
    }

    public void setData(String title, Map<String, Integer> data) {
        this.title = title;
        this.data = data;
        repaint();
    }

    public void setBarClickListener(BarClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data == null || data.isEmpty()) return;

        barAreas.clear();

        Graphics2D g2 = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();

        int padding = 50;
        int barWidth = (width - padding * 2) / data.size();
        int max = data.values().stream().max(Integer::compare).orElse(1);

        // Title
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString(title, padding, 30);

        int x = padding;
        int baseY = height - padding;

        g2.setFont(new Font("Arial", Font.PLAIN, 12));

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            int barHeight = (int) ((entry.getValue() * 1.0 / max) * (height - 120));

            Rectangle rect = new Rectangle(
                    x,
                    baseY - barHeight,
                    barWidth - 10,
                    barHeight
            );

            barAreas.put(entry.getKey(), rect);

            g2.setColor(new Color(100, 149, 237));
            g2.fill(rect);

            g2.setColor(Color.BLACK);
            g2.draw(rect);

            g2.drawString(entry.getKey(), x, baseY + 15);
            g2.drawString(entry.getValue().toString(), x, baseY - barHeight - 5);

            x += barWidth;
        }
    }
}
