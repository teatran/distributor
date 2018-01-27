package tea.org;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import javax.swing.text.Document;

@SuppressWarnings("serial")
public class EditProductDialog extends JDialog
{
	JLabel label_PRODUCT_NAME = new JLabel("Tên sản phẩm");
	JLabel label_SUPPLIER_NAME = new JLabel("Nhà sản xuất");
	JLabel label_QUANTITY = new JLabel("Số lượng");
	JLabel label_SEARCH_PRODUCT;

	JTextField textField_PRODUCT_NAME = new JTextField(11);
	JTextField textField_SUPPLIER_NAME = new JTextField(11);
	JTextField textField_QUANTITY = new JTextField(11);

	String selectedSupplier = null;
	String filterRegex;

	JTable suppliersTable;
	SuppliersTableModel suppliersModel;
	TableRowSorter<SuppliersTableModel> sorter;
	
	String oldProductName;

	public EditProductDialog(JFrame owner, final ProductsTableModel productsModel, 
			final int rowIndex)
	{
		super(owner, "Thay đổi sản phẩm", true);
		setModal(true);
		setSize(450, 350);
		setLocation(owner.getLocation().x + 100, owner.getLocation().y + 100);
		
		oldProductName = productsModel.getValueAt(rowIndex, 0).toString(); 
		textField_PRODUCT_NAME.setText(oldProductName);
		textField_SUPPLIER_NAME.setText(
				productsModel.getValueAt(rowIndex, 1).toString());
		textField_QUANTITY.setText(
				productsModel.getValueAt(rowIndex, 2).toString());
		
		URL imageSearchURL = getClass().getResource("/resources/search.png");
		label_SEARCH_PRODUCT = new JLabel();
		label_SEARCH_PRODUCT.setIcon(new ImageIcon(imageSearchURL));
		
		// Initialize model
		try {
			suppliersModel = new SuppliersTableModel(new JDBCUtils());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Initialize table
		suppliersTable = new JTable(suppliersModel);
		JScrollPane tableScroll = new JScrollPane(suppliersTable);
		tableScroll.setPreferredSize(new Dimension(300, 127));
		suppliersTable.setTableHeader(null);

		// Set row selection mode, one row at a time
		suppliersTable.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);

		// Add filter trigger key
		final SearchFilterListener searchListener = new SearchFilterListener();
		textField_SUPPLIER_NAME.getDocument().addDocumentListener(searchListener);
		
		textField_SUPPLIER_NAME.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e) 
			{
				if (e.isControlDown() && e.getKeyChar() != 'f' && e.getKeyCode() == 70) {
					suppliersTable.getSelectionModel().clearSelection();
					textField_SUPPLIER_NAME.getDocument().addDocumentListener(searchListener);
					textField_SUPPLIER_NAME.setText("");
				}
		    }
		});
		
		// Create sorter
		sorter = new TableRowSorter<>(suppliersModel);
		suppliersTable.setRowSorter(sorter);
		
		// filter the table
		sorter.setRowFilter(
				RowFilter.regexFilter(textField_SUPPLIER_NAME.getText().trim()));

		// Set row height for all rows
		suppliersTable.setRowHeight(25);
		
		JButton button_OK = new JButton("OK");
		
		// --------------------------------------
		//  Setup listeners 
		
		// Add listener for row-selection event
		suppliersTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{

			@Override
			public void valueChanged(ListSelectionEvent event) {
				int indexSelect = suppliersTable.getSelectedRow();
				if (indexSelect != -1) {
					selectedSupplier =
							suppliersTable.getValueAt(indexSelect, 0).toString();
					textField_SUPPLIER_NAME.getDocument().removeDocumentListener(searchListener);
					textField_SUPPLIER_NAME.setText(selectedSupplier);
					
				}
				
			}
		});
		
		button_OK.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				String newProductName = textField_PRODUCT_NAME.getText().trim();
				String newSupplierName = textField_SUPPLIER_NAME.getText().trim();
				String newQuantiy = textField_QUANTITY.getText().trim();
				
				// Edit the selected row in the model
				productsModel.setValueAt(newProductName, rowIndex, 0);
				if (selectedSupplier != null) { 
					// if another supplier is selected
					productsModel.setValueAt(selectedSupplier, rowIndex, 1);
					newSupplierName = selectedSupplier;
				}
				productsModel.setValueAt(newQuantiy, rowIndex, 2);
				
				// Edit the row in database
				try (Connection connection = new JDBCUtils().getConnection()) {
					try (Statement stat = connection.createStatement()) {
						stat.executeUpdate("update PRODUCTS set PRODUCT_NAME='" +
								newProductName + "', SUPPLIER_NAME='" + newSupplierName +
								"', QUANTITY='" + newQuantiy + "' where PRODUCT_NAME = '" + 
								oldProductName + "'"); 			
					}
					catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				catch (SQLException e2) {
					e2.printStackTrace();
				}
				EditProductDialog.this.setVisible(false);
			}
		});
		
		
		// ----------------------------------------
		// Layout
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
				
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 0.3;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 5, 10, 5);
		contentPane.add(label_PRODUCT_NAME, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_END;
		c.weightx = 0.7;
		c.weighty = 0;
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		c.insets = new Insets(10, 5, 10, 5);
		contentPane.add(textField_PRODUCT_NAME, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.25;
		c.weighty = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.insets = new Insets(0, 5, 0, 5);
		contentPane.add(label_SUPPLIER_NAME, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 0.15;
		c.weighty = 0;
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		c.insets = new Insets(0, 0, 0, 0);
		contentPane.add(label_SEARCH_PRODUCT, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_END;
		c.weightx = 0.75;
		c.weighty = 0;
		c.gridx = 2;
		c.gridy = 2;
		c.gridwidth = 1;
		c.insets = new Insets(0, -100, 0, 60);
		contentPane.add(textField_SUPPLIER_NAME, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 2;
		c.insets = new Insets(0, 5, 0, 0);
		contentPane.add(tableScroll, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 0.25;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 1;
		c.insets = new Insets(10, 5, 5, 5);
		contentPane.add(label_QUANTITY, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_END;
		c.weightx = 0.75;
		c.weighty = 0;
		c.gridx = 1;
		c.gridy = 4;
		c.gridwidth = 2;
		contentPane.add(textField_QUANTITY, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 0.5;
		c.weighty = 0;
		c.gridx = 1;
		c.gridy = 5;
		c.gridwidth = 1;
		contentPane.add(button_OK, c);
		
		// Set "button_OK" as default for ENTER key
		getRootPane().setDefaultButton(button_OK);

	} // End of constructor
	
	/**
	 * Setup search filter
	 */
	protected class SearchFilterListener implements DocumentListener
	{
		protected void changeFilter(final DocumentEvent event)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				
				@Override
				public void run()
				{
					Document document = event.getDocument();
					try {
						filterRegex = "(?i).*"
								+ document.getText(0, document.getLength())
								+ ".*";
						filterRegex = filterRegex.replace(" ", ".*");
						sorter.setRowFilter(RowFilter.regexFilter(filterRegex));

					} catch (Exception ex) {
						ex.printStackTrace();
						System.err.println(ex);
					}
					
				}
			});
			
		}

		@Override
		public void changedUpdate(DocumentEvent e)
		{
			changeFilter(e);
			
		}

		@Override
		public void insertUpdate(DocumentEvent e)
		{
			changeFilter(e);
		}

		@Override
		public void removeUpdate(DocumentEvent e)
		{
			changeFilter(e);
		}
		
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}