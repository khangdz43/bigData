import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;


//GIAO DIỆN THỐNG KÊ
public class StatisticPanel extends JPanel {

    private Connection conn;
    private JTextArea area;
//    class
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
        chartPanel.setBarClickListener(this::loadDetailByKey);

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

// ================= NGUY CƠ (KẾT HỢP RISK + TYPE) =================
private void loadRiskStat() {
    area.setText("");
    Map<String, Integer> data = new LinkedHashMap<>();

    String sql =
            "SELECT " +
                    "   d.risk_level AS risk_level, " +
                    "   COUNT(*) AS total " +
                    "FROM diabetes_records r " +
                    "JOIN diagnosis d ON r.record_id = d.record_id " +
                    "GROUP BY d.risk_level " +
                    "ORDER BY d.risk_level";

    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            String riskLevel = rs.getString("risk_level");
            int total = rs.getInt("total");

            data.put(riskLevel, total);
            area.append(riskLevel + ": " + total + "\n");
        }

        chartPanel.setData("Phân loại nguy cơ bệnh nhân", data);

    } catch (Exception e) {
        area.setText("Lỗi: " + e.getMessage());
        e.printStackTrace();
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

//    load theo key
private void loadDetailByKey(String key) {
    area.setText("Chi tiết cho: " + key + "\n\n");

    String sql =
            "SELECT full_name, gender, city " +
                    "FROM patients " +
                    "WHERE city = '" + key + "' " +
                    "LIMIT 20";

    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            area.append(
                    rs.getString("full_name") + " - " +
                            rs.getString("gender") + " - " +
                            rs.getString("city") + "\n"
            );
        }

    } catch (Exception e) {
        area.setText("Lỗi: " + e.getMessage());
    }
}

}
