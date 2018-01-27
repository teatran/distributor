package tea.org;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class SuppliersTableModel extends DefaultTableModel
{
	static final String[] headers = new String[] 
			{"Tên nhà cung cấp"};
	
	public SuppliersTableModel(JDBCUtils jdbcUtils) throws SQLException
	{
		super(headers, 0);
		
		assert this.getColumnCount() == 1;
		Connection connection = jdbcUtils.getConnection();	
		// populate model
		try (Statement stat = connection.createStatement()) {
			String query = "select * from suppliers";
			try (ResultSet rs = stat.executeQuery(query)) {
				while (rs.next()) {
					this.addRow(new Object[] {
							rs.getString(1)
					});
				}
			}
		}
		connection.close();
	}
	// --------------------------------------------------
	// Methods from interface TableModel
	// --------------------------------------------------

	/**
	 * Not allow user to edit any cell from TableModel
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}
	
	/**
	 * Unit test
	 */
	public static void main(String[] args)
	{
		// test model
	}
}
