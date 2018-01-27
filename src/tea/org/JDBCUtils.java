package tea.org;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class JDBCUtils
{
	Connection connection = null;
	String username = "root";
	String password = "anhthe939**";
	String dbms = "mysql";
	String url;
	
	public Connection getConnection() throws SQLException
	{
		if (connection == null) {
			try {
				// force loading the driver class
				Class.forName("com.mysql.jdbc.Driver");

			} catch (ClassNotFoundException e) {
				System.err.println("Missing com.mysql.jdbc.Driver");
				System.exit(1);
			}
			// setup the connection
			url = "jdbc:" + this.dbms + "://localhost/saledb"; // + "?useSSL=false"
			return DriverManager.getConnection(url, username, password);
		} else {
			return connection;
		}
		
	}
	
	/**
	 * Display SQLException in a dialog box.
	 */
	public void displaySQLExceptionDialog(JFrame parentFrame, SQLException sqle) 
	{
		JOptionPane.showMessageDialog(parentFrame, new String[] {
				sqle.getClass().getName() + ":", sqle.getMessage() } );
	}
}
