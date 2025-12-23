import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PatientListPanel extends JPanel {

    private Connection conn;
    private JTable table;
    private DefaultTableModel model;

    // FILTER COMPONENTS
    private JComboBox<String> cbGender;
    private JComboBox<String> cbRisk;
    private JComboBox<String> cbDiabetesType;

    public PatientListPanel(Connection conn) {
        this.conn = conn;
        setLayout(new BorderLayout(10, 10));

        // ===== TITLE =====
        JLabel title = new JLabel("DANH SÁCH BỆNH NHÂN", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // ===== FILTER PANEL =====
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        cbGender = new JComboBox<>(new String[]{
                "Tất cả", "Nam", "Nữ"
        });

        cbRisk = new JComboBox<>(new String[]{
                "Tất cả", "Thấp", "Trung bình", "Cao"
        });

        cbDiabetesType = new JComboBox<>(new String[]{
                "Tất cả", "Type 1", "Type 2", "Tiền tiểu đường"
        });

        JButton btnFilter = new JButton("Lọc");
        JButton btnReload = new JButton("Tải lại");

        btnFilter.addActionListener(e -> loadData());
        btnReload.addActionListener(e -> {
            cbGender.setSelectedIndex(0);
            cbRisk.setSelectedIndex(0);
            cbDiabetesType.setSelectedIndex(0);
            loadData();
        });

        filterPanel.add(new JLabel("Giới tính:"));
        filterPanel.add(cbGender);
        filterPanel.add(new JLabel("Mức rủi ro:"));
        filterPanel.add(cbRisk);
        filterPanel.add(new JLabel("Loại tiểu đường:"));
        filterPanel.add(cbDiabetesType);
        filterPanel.add(btnFilter);
        filterPanel.add(btnReload);

        add(filterPanel, BorderLayout.SOUTH);

        // ===== TABLE =====
        model = new DefaultTableModel(
                new String[]{
                        "STT", "Họ tên", "Giới tính", "Năm sinh", "Thành phố",
                        "Glucose", "HbA1c", "Huyết áp", "BMI", "Insulin",
                        "Ngày khám", "Loại tiểu đường", "Mức rủi ro", "Ghi chú"
                }, 0
        );

        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();
    }

    // ================= LOAD DATA WITH FILTER =================
    private void loadData() {
        model.setRowCount(0);

        String gender = cbGender.getSelectedItem().toString();
        String risk = cbRisk.getSelectedItem().toString();
        String type = cbDiabetesType.getSelectedItem().toString();

        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        " p.full_name, p.gender, p.birth_year, p.city, " +
                        " r.glucose_level, r.hba1c, r.blood_pressure, r.bmi, r.insulin, r.record_date, " +
                        " d.diabetes_type, d.risk_level, d.note " +
                        "FROM patients p " +

                        "LEFT JOIN ( " +
                        "   SELECT r1.* FROM diabetes_records r1 " +
                        "   JOIN ( " +
                        "       SELECT patient_id, MAX(record_date) AS max_date " +
                        "       FROM diabetes_records GROUP BY patient_id " +
                        "   ) r2 " +
                        "   ON r1.patient_id = r2.patient_id " +
                        "   AND r1.record_date = r2.max_date " +
                        ") r ON p.patient_id = r.patient_id " +

                        "LEFT JOIN diagnosis d ON r.record_id = d.record_id " +
                        "WHERE 1=1 "
        );

        // ===== APPLY FILTERS =====
        if (!gender.equals("Tất cả")) {
            sql.append(" AND p.gender = '").append(gender).append("' ");
        }

        if (!risk.equals("Tất cả")) {
            sql.append(" AND d.risk_level = '").append(risk).append("' ");
        }

        if (!type.equals("Tất cả")) {
            sql.append(" AND d.diabetes_type = '").append(type).append("' ");
        }

        sql.append(" ORDER BY p.full_name");

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql.toString())) {

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
