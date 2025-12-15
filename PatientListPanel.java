import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
// GIAO DIỆN HIỂ THỊ ALL
public class PatientListPanel extends JPanel {

    private Connection conn;
    private JTable table;
    private DefaultTableModel model;

    public PatientListPanel(Connection conn) {
        this.conn = conn;
        setLayout(new BorderLayout(10, 10));

        // ===== TITLE =====
        JLabel title = new JLabel("DANH SÁCH BỆNH NHÂN", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // ===== TABLE =====
        model = new DefaultTableModel(
                new String[]{
                        "STT",
                        "Họ tên",
                        "Giới tính",
                        "Năm sinh",
                        "Thành phố",
                        "Glucose",
                        "HbA1c",
                        "Huyết áp",
                        "BMI",
                        "Insulin",
                        "Ngày khám",
                        "Loại tiểu đường",
                        "Mức rủi ro",
                        "Ghi chú"
                }, 0
        );

        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnReload = new JButton("Tải lại dữ liệu");
        btnReload.addActionListener(e -> loadData());
        add(btnReload, BorderLayout.SOUTH);

        loadData();
    }

    // ================= LOAD DATA =================
    private void loadData() {
        model.setRowCount(0);

        String sql =
                "SELECT " +
                        " p.full_name, p.gender, p.birth_year, p.city, " +
                        " r.glucose_level, r.hba1c, r.blood_pressure, r.bmi, r.insulin, r.record_date, " +
                        " d.diabetes_type, d.risk_level, d.note " +
                        "FROM patients p " +

                        // ===== LẤY RECORD MỚI NHẤT =====
                        "LEFT JOIN ( " +
                        "   SELECT r1.* " +
                        "   FROM diabetes_records r1 " +
                        "   JOIN ( " +
                        "       SELECT patient_id, MAX(record_date) AS max_date " +
                        "       FROM diabetes_records " +
                        "       GROUP BY patient_id " +
                        "   ) r2 " +
                        "   ON r1.patient_id = r2.patient_id " +
                        "   AND r1.record_date = r2.max_date " +
                        ") r ON p.patient_id = r.patient_id " +

                        // ===== JOIN DIAGNOSIS =====
                        "LEFT JOIN diagnosis d ON r.record_id = d.record_id " +

                        "ORDER BY p.full_name";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int stt = 1;
            while (rs.next()) {
                model.addRow(new Object[]{
                        stt++,
                        rs.getString("full_name"),
                        rs.getString("gender"),
                        rs.getObject("birth_year"),
                        rs.getString("city"),
                        rs.getObject("glucose_level"),
                        rs.getObject("hba1c"),
                        rs.getObject("blood_pressure"),
                        rs.getObject("bmi"),
                        rs.getObject("insulin"),
                        rs.getObject("record_date"),
                        rs.getString("diabetes_type"),
                        rs.getString("risk_level"),
                        rs.getString("note")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi tải dữ liệu:\n" + e.getMessage(),
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
