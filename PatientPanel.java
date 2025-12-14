import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.UUID;

public class PatientPanel extends JPanel {

    private Connection connection;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtName, txtBirthYear, txtCity;
    private JComboBox<String> cbGender;

    public PatientPanel(Connection connection) {
        this.connection = connection;
        setLayout(new BorderLayout());

        // ===== FORM INPUT =====
        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));

        txtName = new JTextField();
        txtBirthYear = new JTextField();
        txtCity = new JTextField();
        cbGender = new JComboBox<>(new String[]{"Nam", "Nữ"});

        form.add(new JLabel("Họ tên"));
        form.add(txtName);
        form.add(new JLabel("Giới tính"));
        form.add(cbGender);
        form.add(new JLabel("Năm sinh"));
        form.add(txtBirthYear);
        form.add(new JLabel("Thành phố"));
        form.add(txtCity);

        JButton btnAdd = new JButton("Thêm bệnh nhân");
        btnAdd.addActionListener(e -> insertPatient());

        form.add(new JLabel());
        form.add(btnAdd);

        add(form, BorderLayout.NORTH);

        // ===== TABLE =====
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Họ tên", "Giới tính", "Năm sinh", "Thành phố"}, 0);
        table = new JTable(tableModel);

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadPatients();
    }

    // ================= INSERT =================
    private void insertPatient() {
        try {
            String patientId = UUID.randomUUID().toString();
            String name = txtName.getText();
            String gender = cbGender.getSelectedItem().toString();
            int birthYear = Integer.parseInt(txtBirthYear.getText());
            String city = txtCity.getText();

            // Tạo timestamp ở Java
            String createdAt = java.time.LocalDateTime.now().toString();

            String sql = String.format(
                    "INSERT INTO patients VALUES ('%s','%s','%s',%d,'%s','%s')",
                    patientId, name, gender, birthYear, city, createdAt
            );

            Statement stmt = connection.createStatement();
            stmt.execute(sql);

            JOptionPane.showMessageDialog(this, "Thêm bệnh nhân thành công!");
            loadPatients();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }


    // ================= READ =================
    private void loadPatients() {
        tableModel.setRowCount(0);

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT patient_id, full_name, gender, birth_year, city FROM patients");

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getString(5)
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
