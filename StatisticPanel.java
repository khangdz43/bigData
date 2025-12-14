import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class StatisticPanel extends JPanel {

    private Connection conn;
    private JTextArea area;

    public StatisticPanel(Connection conn) {
        this.conn = conn;
        setLayout(new BorderLayout(10,10));

        area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 13));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnType = new JButton("Thống kê theo loại tiểu đường");
        JButton btnRisk = new JButton("Thống kê theo mức độ nguy cơ");
        JButton btnDetail = new JButton("Danh sách chẩn đoán chi tiết");

        btnType.addActionListener(e -> statByType());
        btnRisk.addActionListener(e -> statByRisk());
        btnDetail.addActionListener(e -> statDetail());

        top.add(btnType);
        top.add(btnRisk);
        top.add(btnDetail);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(area), BorderLayout.CENTER);
    }

    // 1️⃣ Thống kê theo loại tiểu đường
    private void statByType() {
        area.setText("");
        try {
            String sql = "SELECT diabetes_type, COUNT(*) FROM diagnosis GROUP BY diabetes_type";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            area.append("📊 THỐNG KÊ THEO LOẠI TIỂU ĐƯỜNG\n");
            area.append("---------------------------------\n");

            while (rs.next()) {
                area.append(
                        rs.getString(1) + " : " + rs.getInt(2) + " ca\n"
                );
            }
        } catch (Exception e) {
            area.setText("Lỗi thống kê");
        }
    }

    // 2️⃣ Thống kê theo mức độ nguy cơ
    private void statByRisk() {
        area.setText("");
        try {
            String sql = "SELECT risk_level, COUNT(*) FROM diagnosis GROUP BY risk_level";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            area.append("⚠️ THỐNG KÊ THEO MỨC ĐỘ NGUY CƠ\n");
            area.append("---------------------------------\n");

            while (rs.next()) {
                area.append(
                        rs.getString(1) + " : " + rs.getInt(2) + " ca\n"
                );
            }
        } catch (Exception e) {
            area.setText("Lỗi thống kê");
        }
    }

    // 3️⃣ Danh sách chẩn đoán chi tiết
    private void statDetail() {
        area.setText("");
        try {
            String sql =
                    "SELECT p.full_name, d.diabetes_type, d.risk_level " +
                            "FROM patients p " +
                            "JOIN diabetes_records r ON p.patient_id = r.patient_id " +
                            "JOIN diagnosis d ON r.record_id = d.record_id";

            ResultSet rs = conn.createStatement().executeQuery(sql);

            area.append("🩺 DANH SÁCH CHẨN ĐOÁN CHI TIẾT\n");
            area.append("---------------------------------\n");

            while (rs.next()) {
                area.append(
                        "👤 " + rs.getString(1) +
                                " | Loại: " + rs.getString(2) +
                                " | Nguy cơ: " + rs.getString(3) + "\n"
                );
            }
        } catch (Exception e) {
            area.setText("Lỗi thống kê");
        }
    }
}
