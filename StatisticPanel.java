import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatisticPanel extends JPanel {

    private Connection conn;
    private JTextArea area;
    private BarChartPanel chartPanel;

    public StatisticPanel(Connection conn) {
        this.conn = conn;
        setLayout(new BorderLayout(10, 10));

        // ===== BUTTON BAR =====
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton btnGender = new JButton("Giới tính");
        JButton btnRisk = new JButton("Nguy cơ");
        JButton btnCity = new JButton("Thành phố");

        btnGender.addActionListener(e -> loadGenderStat());
        btnRisk.addActionListener(e -> loadRiskStat());
        btnCity.addActionListener(e -> loadCityStat());

        buttonPanel.add(btnGender);
        buttonPanel.add(btnRisk);
        buttonPanel.add(btnCity);

        add(buttonPanel, BorderLayout.NORTH);

        // ===== CHART =====
        chartPanel = new BarChartPanel();
        chartPanel.setPreferredSize(new Dimension(600, 300));
        add(chartPanel, BorderLayout.CENTER);

        // ===== TEXT =====
        area = new JTextArea(5, 20);
        area.setEditable(false);
        add(new JScrollPane(area), BorderLayout.SOUTH);
    }

    // ================= GIỚI TÍNH =================
    private void loadGenderStat() {
        area.setText("");
        Map<String, Integer> data = new LinkedHashMap<>();

        String sql = "SELECT gender, COUNT(*) FROM patients GROUP BY gender";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                data.put(rs.getString(1), rs.getInt(2));
                area.append(rs.getString(1) + ": " + rs.getInt(2) + "\n");
            }

            chartPanel.setData("Phân bố giới tính", data);

        } catch (Exception e) {
            area.setText("Lỗi: " + e.getMessage());
        }
    }

    // ================= NGUY CƠ =================
    private void loadRiskStat() {
        area.setText("");
        Map<String, Integer> data = new LinkedHashMap<>();

        String sql =
                "SELECT CASE " +
                        " WHEN glucose_level < 100 THEN 'Bình thường' " +
                        " WHEN glucose_level BETWEEN 100 AND 125 THEN 'Tiền tiểu đường' " +
                        " ELSE 'Tiểu đường' END AS risk, COUNT(*) " +
                        "FROM diabetes_records " +
                        "GROUP BY CASE " +
                        " WHEN glucose_level < 100 THEN 'Bình thường' " +
                        " WHEN glucose_level BETWEEN 100 AND 125 THEN 'Tiền tiểu đường' " +
                        " ELSE 'Tiểu đường' END";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                data.put(rs.getString(1), rs.getInt(2));
                area.append(rs.getString(1) + ": " + rs.getInt(2) + "\n");
            }

            chartPanel.setData("Phân loại nguy cơ tiểu đường", data);

        } catch (Exception e) {
            area.setText("Lỗi: " + e.getMessage());
        }
    }

    // ================= THÀNH PHỐ =================
    private void loadCityStat() {
        area.setText("");
        Map<String, Integer> data = new LinkedHashMap<>();

        String sql =
                "SELECT city, COUNT(*) AS total FROM patients GROUP BY city ORDER BY total DESC";

        try (Statement stmt = conn.createStatement();

             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                data.put(rs.getString(1), rs.getInt(2));
                area.append(rs.getString(1) + ": " + rs.getInt(2) + "\n");
            }

            chartPanel.setData("Số bệnh nhân theo thành phố", data);

        } catch (Exception e) {
            area.setText("Lỗi: " + e.getMessage());
        }
    }
}
