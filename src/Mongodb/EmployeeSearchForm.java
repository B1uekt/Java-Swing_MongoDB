package Mongodb;
import javax.swing.*;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.mongodb.*;
import org.bson.types.ObjectId;
import java.util.regex.Pattern;

public class EmployeeSearchForm extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchTextField;
    private JComboBox<String> searchComboBox;
    
    String[] columnNames = {"", "_id", "Manv", "Tennv", "Ngsinh", "Gioitinh", "Diachi", "SoDT", "Email", "Chucvu", "Luong"};
    
    
    public EmployeeSearchForm() {
        setTitle("Employee Search Form");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = createTopPanel();
        JPanel middlePanel = createMiddlePanel();
        JPanel bottomPanel = createBottomPanel();

        add(topPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH); 
        
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton addButton = new JButton("Thêm");
        JButton deleteButton = new JButton("Xóa");
        JButton updateButton = new JButton("Cập nhật");

        panel.add(addButton);
        panel.add(deleteButton);
        panel.add(updateButton);

        addButton.addActionListener(e -> onAddButtonClicked());
        deleteButton.addActionListener(e -> onDeleteButtonClicked());
        updateButton.addActionListener(e -> onUpdateButtonClicked());

        return panel;
    }

    private JPanel createMiddlePanel() {
        JPanel panel = new JPanel(new FlowLayout());

        String[] searchOptions = {"","Mã nhân viên", "Họ tên", "Địa chỉ", "Số điện thoại"};
        searchComboBox = new JComboBox<>(searchOptions);
        searchTextField = new JTextField(20);
        JButton searchButton = new JButton("Tìm kiếm");

        panel.add(new JLabel("Tìm kiếm theo: "));
        panel.add(searchComboBox);
        panel.add(searchTextField);
        panel.add(searchButton);

        searchButton.addActionListener(e -> onSearchButtonClicked());

        return panel;

    }
    
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        table.setCellSelectionEnabled(true);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        connectAndLoadData();

        return panel;
    }
    private void LoadData(DBCursor cursor) {
    	try {
        	while (cursor.hasNext()) {
        	    DBObject document = cursor.next();
        	    
        	    //Object idValue = document.get("_id");
        	    //System.out.println("_id: " + idValue);

        	    Object[] rowData = new Object[columnNames.length];
        	    for (int i = 1; i < columnNames.length; i++) {
        	        String columnName = columnNames[i];

        	        if ("ngsinh".equals(columnName)) {
        	            Object ngsinhValue = document.get("Ngsinh");

        	            //System.out.println("Ngsinh: " + ngsinhValue);
        	            rowData[i] = ngsinhValue;
        	        } else {

        	            rowData[i] = document.get(columnName);
        	            //System.out.println(columnName + ": " + rowData[i]);
        	        }
        	    }

        	    tableModel.addRow(rowData);
        	    
        	}

        } finally {
            cursor.close();
        }
    }
    private void connectAndLoadData() {
        try {
            MongoDBManager mongoDBManager = new MongoDBManager("EX11");
            DBCollection coll = mongoDBManager.getCollection("nhanvien");

            DBCursor cursor = coll.find();
            LoadData(cursor);
            cursor.close();
            mongoDBManager.closeConnection();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
   
    
    
    private void onAddButtonClicked() {
    	DialogForm d = new DialogForm();
    	tableModel.setRowCount(0);
    	connectAndLoadData();
    }

    private int deleteData(String index) {
    	try {
            MongoDBManager mongoDBManager = new MongoDBManager("EX11");
            DBCollection coll = mongoDBManager.getCollection("nhanvien");

            BasicDBObject query = new BasicDBObject();
            System.out.println(index);
            query.put("_id", new ObjectId(index));
            return coll.remove(query).getN();
    	}
    	catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }
    private void onDeleteButtonClicked() {
    	int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hàng để xóa", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Object idValue = table.getValueAt(selectedRow, table.getColumn("_id").getModelIndex());
        //System.out.println(idValue);
        int deleteSuccess = deleteData(idValue.toString());
        if (deleteSuccess>0) {
        	tableModel.removeRow(selectedRow);
        	SwingUtilities.invokeLater(() -> tableModel.fireTableDataChanged());
            JOptionPane.showMessageDialog(this, "Xóa dữ liệu thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Xóa dữ liệu thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int updateData(String index, String updatedValue, String selectedColumn) {
        try {
            MongoDBManager mongoDBManager = new MongoDBManager("EX11");
            DBCollection coll = mongoDBManager.getCollection("nhanvien");

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(index));

            BasicDBObject updateObject = new BasicDBObject("$set", new BasicDBObject(selectedColumn, updatedValue));

            WriteResult result = coll.update(query, updateObject);

            return result.getN();
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }


    private void onUpdateButtonClicked() {
    	int selectedRow = table.getSelectedRow();
    	int selectedColumn = table.getSelectedColumn();
    	if (selectedRow != -1 && selectedColumn != -1) {
    		
    	    Object cellValue = table.getValueAt(selectedRow, selectedColumn);
    	    if(selectedColumn==1) {
    	    	JOptionPane.showMessageDialog(this, "Không thể chỉnh sửa cột id", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
    	    }
    	    if (cellValue != null) {
    	        //System.out.println("Giá trị của ô được chọn: " + cellValue);
    	    	
    	        Object idValue = table.getValueAt(selectedRow, table.getColumn("_id").getModelIndex());
    	        System.out.println(columnNames[selectedColumn]);
    	        int updateSuccess = updateData(idValue.toString(), cellValue.toString(),columnNames[selectedColumn] );
    	        
    	        if (updateSuccess>0) {
    	        	SwingUtilities.invokeLater(() -> tableModel.fireTableDataChanged());
    	            JOptionPane.showMessageDialog(this, "Cập nhật dữ liệu thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    	        } else {
    	            JOptionPane.showMessageDialog(this, "Cập nhật dữ liệu thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
    	        }
    	        
    	    } else {
    	    	JOptionPane.showMessageDialog(this, "Ô được chọn có giá trị null", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
    	    }
    	} else {
    		JOptionPane.showMessageDialog(this, "Vui lòng chọn một hàng để cập nhật", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
    	}
    }
    private void searchData(String selectedOption,String inputValue) {
    	try {
            MongoDBManager mongoDBManager = new MongoDBManager("EX11");
            DBCollection coll = mongoDBManager.getCollection("nhanvien");
            
            BasicDBObject searchQuery = new BasicDBObject();
            String condition = new String();
            switch (selectedOption) {
	            case "Mã nhân viên":
	            	condition = "Manv";
	                break;
	            case "Họ tên":
	            	condition = "Tennv";
	                break;
	            case "Địa chỉ":
	            	condition = "Diachi";
	                break;
	            case "Số điện thoại":
	            	condition = "SoDT";
	                break;
            }
            searchQuery.put(condition, Pattern.compile(inputValue, Pattern.CASE_INSENSITIVE));
            DBCursor cursor = coll.find(searchQuery);
            tableModel.setRowCount(0);
            LoadData(cursor);
            mongoDBManager.closeConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void onSearchButtonClicked() {
    	String selectedOption = (String) searchComboBox.getSelectedItem();
        String inputValue = searchTextField.getText();
        if (selectedOption.isEmpty() || inputValue.isEmpty()) {
        	JOptionPane.showMessageDialog(this, "Vui lòng điều đủ thông tin cần tìm", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        searchData(selectedOption,inputValue);
        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EmployeeSearchForm form = new EmployeeSearchForm();
            form.setVisible(true);
        });
    }
}
