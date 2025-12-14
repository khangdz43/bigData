import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.UUID;

public class RecordPanel extends JPanel {

    private Connection conn;
    private DefaultTableModel model;

    private JTextField txtPatientId, txtGlucose, txtHba1c,
            txtBp, txtBmi, txtInsulin, txtDate;

    public RecordPanel(Connection conn) {
        this.conn = conn;
        setLayout(new BorderLayout(10,10));

        JPanel form = new JPanel(new GridLayout(2,7,10,10));

        txtPatientId = new JTextField();
        txtGlucose = new JTextField();
        txtHba1c = new JTextField();
        txtBp = new JTextField();
        txtBmi = new JTextField();
        txtInsulin = new JTextField();
        txtDate = new JTextField("2025-01-01");

        form.add(new JLabel("Patient ID"));
        form.add(new JLabel("Glucose"));
        form.add(new JLabel("HbA1c"));
        form.add(new JLabel("Blood Pressure"));
        form.add(new JLabel("BMI"));
        form.add(new JLabel("Insulin"));
        form.add(new JLabel("Record Date"));

        form.add(txtPatientId);
        form.add(txtGlucose);
        form.add(txtHba1c);
        form.add(txtBp);
        form.add(txtBmi);
        form.add(txtInsulin);
        form.add(txtDate);

        JButton btnAdd = new JButton("Thêm chỉ số");
        btnAdd.addActionListener(e -> insertRecord());

        model = new DefaultTableModel(
                new String[]{"Record ID","Patient ID","Glucose","HbA1c","BP","BMI","Insulin","Date"},0);
        JTable table = new JTable(model);

        add(form, BorderLayout.NORTH);
        add(btnAdd, BorderLayout.CENTER);
        add(new JScrollPane(table), BorderLayout.SOUTH);

        loadData();
    }

    private void insertRecord() {
        try {
            String sql = "INSERT INTO TABLE diabetes_records VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, txtPatientId.getText());
            ps.setDouble(3, Double.parseDouble(txtGlucose.getText()));
            ps.setDouble(4, Double.parseDouble(txtHba1c.getText()));
            ps.setInt(5, Integer.parseInt(txtBp.getText()));
            ps.setDouble(6, Double.parseDouble(txtBmi.getText()));
            ps.setDouble(7, Double.parseDouble(txtInsulin.getText()));
            ps.setString(8, txtDate.getText());

            ps.executeUpdate();
            loadData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Lỗi thêm chỉ số");
            System.out.println("sss" + e);
        }
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT * FROM diabetes_records");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString(1),
                        rs.getString(2),
                        rs.getDouble(3),
                        rs.getDouble(4),
                        rs.getInt(5),
                        rs.getDouble(6),
                        rs.getDouble(7),
                        rs.getDate(8)
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
