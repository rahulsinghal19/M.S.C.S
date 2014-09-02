/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Rahul
 */
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.*;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class OracleJDBC {

    public static DefaultTableModel buildTableModel(ResultSet rs)
            throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();

// names of columns
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

// data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);

    }

    public static void main(String[] argv) {
//        Connection conn = null;

        System.out.println("-------- Oracle JDBC Connection Testing ------");

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

        } catch (ClassNotFoundException e) {

            System.out.println("Where is your Oracle JDBC Driver?");
            e.printStackTrace();
            return;

        }



        System.out.println("Oracle JDBC Driver Registered!");

        Connection connection = null;

        try {

//			connection = DriverManager.getConnection(
//					"jdbc:oracle:thin:@localhost:1521:mkyong", "rxs132730",
//					"password");
            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@csoracle.utdallas.edu:1521:student", "rxs132730",
                    "password");

        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;

        }

        if (connection != null) {
            System.out.println("You made it, take control your database now!");
            try {
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM EMPLOYEE_MASTER");

                while (rs.next()) {
                    System.out.println(rs.getString(1));
                }
                //jTable.setModel(buildTableModel(rs));
                JTable table = new JTable(buildTableModel(rs));
                //JTable table.setModel(buildTableModel(rs));
                JOptionPane.showMessageDialog(null, new JScrollPane(table));

            } catch (SQLException ex) {
                Logger.getLogger(OracleJDBC.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Failed to make connection!");
        }
    }
}
