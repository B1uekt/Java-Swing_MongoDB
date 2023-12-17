package Mongodb;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.*;
import com.mongodb.*;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import java.util.Date;
public class DialogForm extends JFrame{
	private JTextField textFieldEmployeeID;
	private JTextField textFieldEmployeeName;
	private JTextField textFieldAddress;
	private JTextField textFieldPhoneNumber;
	private JTextField textFieldEmail;
	private JTextField textFieldPosition;
	private JTextField textFieldSalary;
	private JComboBox<String> genderComboBox;
    private JDateChooser dateChooser;
    private String[] genderOptions = {"","Nam", "Nữ"};;
	public DialogForm()  {
		JDialog dialog = new JDialog(this, "Thêm Nhân Viên", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JLabel labelEmployeeID = new JLabel("Mã Nhân Viên:");
        textFieldEmployeeID = new JTextField();

        JLabel labelDOB = new JLabel("Ngày Sinh:");
        dateChooser = new JDateChooser();

        
        
        genderComboBox = new JComboBox<>(genderOptions);
        JLabel labelGender = new JLabel("Giới Tính:");

        JLabel labelEmployeeName = new JLabel("Tên Nhân Viên:");
        textFieldEmployeeName = new JTextField();

        JLabel labelAddress = new JLabel("Địa Chỉ:");
        textFieldAddress = new JTextField();

        JLabel labelPhoneNumber = new JLabel("Số ĐT:");
        textFieldPhoneNumber = new JTextField();

        JLabel labelEmail = new JLabel("Email:");
        textFieldEmail = new JTextField();

        JLabel labelPosition = new JLabel("Chức Vụ:");
        textFieldPosition = new JTextField();

        JLabel labelSalary = new JLabel("Lương:");
        textFieldSalary = new JTextField("0");

        JButton saveButton = new JButton("Lưu");
        JButton addButtoninDiaglog = new JButton("Thêm");
        JButton closeButton = new JButton("Đóng");
        
        saveButton.addActionListener(e -> onAddButtoninDialogClicked());
        addButtoninDiaglog.addActionListener(e -> onSaveButtonClicked());
        closeButton.addActionListener(e -> onCloseButtonClicked());
        

        JPanel panel1 = new JPanel(new GridLayout(1, 2));
        JPanel panelEast = new JPanel(new GridLayout(1, 3));
        JPanel leftPanel = new JPanel(new GridLayout(4, 2));
        JPanel rightPanel = new JPanel(new GridLayout(5, 2));

        leftPanel.add(labelEmployeeID);
        leftPanel.add(textFieldEmployeeID);
        
        leftPanel.add(labelDOB);
        leftPanel.add(dateChooser);
        
        leftPanel.add(labelGender);
        leftPanel.add(genderComboBox);
        
        leftPanel.add(labelEmployeeName);
        leftPanel.add(textFieldEmployeeName);
        
        rightPanel.add(labelAddress);
        rightPanel.add(textFieldAddress);
        rightPanel.add(labelPhoneNumber);
        rightPanel.add(textFieldPhoneNumber);
        rightPanel.add(labelEmail);
        rightPanel.add(textFieldEmail);
        rightPanel.add(labelPosition);
        rightPanel.add(textFieldPosition);
        rightPanel.add(labelSalary);
        rightPanel.add(textFieldSalary);

        panel1.add(leftPanel);
        panel1.add(rightPanel);

        panelEast.add(saveButton);
        panelEast.add(addButtoninDiaglog);
        panelEast.add(closeButton);
        dialog.add(panel1, BorderLayout.NORTH);
        dialog.add(panelEast, BorderLayout.EAST);
        dialog.setSize(800, 180);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
	}
	private String DateConversion(Date date) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return sdf.format(date);
    }
	
	private void onAddButtoninDialogClicked() {
	    if (textFieldEmployeeID.getText().isEmpty() || textFieldEmployeeName.getText().isEmpty() ||
	            textFieldAddress.getText().isEmpty() || textFieldPhoneNumber.getText().isEmpty() ||
	            textFieldEmail.getText().isEmpty() || textFieldPosition.getText().isEmpty() ||
	            textFieldSalary.getText().isEmpty() || String.valueOf(genderComboBox.getSelectedItem()).isEmpty() ||
	            dateChooser.getDate() == null) {
	        JOptionPane.showMessageDialog(DialogForm.this, "Vui lòng điền đầy đủ thông tin", "Lỗi", JOptionPane.ERROR_MESSAGE);
	        return;
	    }
	    try {
	        double salary = Double.parseDouble(textFieldSalary.getText());
	        String employeeID = textFieldEmployeeID.getText();
	        String employeeName = textFieldEmployeeName.getText();
	        String address = textFieldAddress.getText();
	        String phoneNumber = textFieldPhoneNumber.getText();
	        String email = textFieldEmail.getText();
	        String position = textFieldPosition.getText();
	        String selectedGender = (String) genderComboBox.getSelectedItem();
	        Date selectedDate = dateChooser.getDate();
	        String birthday = DateConversion(selectedDate);
	        try {
	            MongoDBManager mongoDBManager = new MongoDBManager("EX11");
	            DBCollection coll = mongoDBManager.getCollection("nhanvien");

	            BasicDBObject newEmployee = new BasicDBObject();
	            newEmployee.append("Manv", employeeID);
	            newEmployee.append("Tennv", employeeName);
	            newEmployee.append("Ngsinh", birthday);
	            newEmployee.append("Gioitinh",selectedGender);
	            newEmployee.append("Diachi", address);
	            newEmployee.append("SoDT", phoneNumber);
	            newEmployee.append("Email",email);
	            newEmployee.append("Chucvu", position);
	            newEmployee.append("Luong", salary);

	            coll.insert(newEmployee);
	            mongoDBManager.closeConnection();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	    } catch (NumberFormatException ex) {
	        JOptionPane.showMessageDialog(DialogForm.this, "Lương phải là một số", "Lỗi", JOptionPane.ERROR_MESSAGE);
	    }
	}
    
	private void onSaveButtonClicked() {
		textFieldEmployeeID.setText("");
		textFieldEmployeeName.setText("");
		textFieldAddress.setText("");
		textFieldPhoneNumber.setText("");
		textFieldEmail.setText("");
		textFieldPosition.setText("");
		textFieldSalary.setText("");
		genderComboBox.setSelectedItem(genderOptions[0]);
		dateChooser.setDate(null);

	}
	
	private void onCloseButtonClicked() {
		this.dispose();
	}
}
