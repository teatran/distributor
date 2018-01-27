package tea.org;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class ProductsTableModel extends DefaultTableModel
{
	static final String[] headers = new String[] 
			{"Tên sản phẩm", "Nhà cung cấp", "Số lượng"};
	
	public ProductsTableModel(JDBCUtils jdbcUtils) throws SQLException
	{
		super(headers, 0);
		
		assert this.getColumnCount() == 3;
		Connection connection = jdbcUtils.getConnection();	
		// populate model
		try (Statement stat = connection.createStatement()) {
			String query = "select * from products";
			try (ResultSet rs = stat.executeQuery(query)) {
				while (rs.next()) {
					this.addRow(new Object[] {
							rs.getString(1), rs.getString(2), rs.getString(3)
					});
				}
			}
		}
		
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
