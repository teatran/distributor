package tea.org;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import javax.swing.text.Document;
import javax.swing.table.TableColumn;

@SuppressWarnings("serial")
public class ProductsFrame extends JFrame {
	JDBCUtils jdbcUtils;
	JTable table;
	ProductsTableModel productsModel;
	TableRowSorter<ProductsTableModel> sorter;

	MyToolBar bar;

	JLabel label_SEARCH_PRODUCT;
	JLabel label_PRODUCT_COUNT;

	public JTextField textfield_FILTER_PRODUCT;

	JButton buttonEditRow;
	JButton buttonDeleteRow;
	JButton buttonAddRow;

	public ProductsFrame(JDBCUtils jdbcUtilsArg) throws SQLException {
		super("Kho hàng"); // set window title

		this.jdbcUtils = jdbcUtilsArg;

		// When user close window, exit application
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});

		// Initialize model
		try {
			productsModel = new ProductsTableModel(jdbcUtils);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Initialize table
		table = new JTable(productsModel);

		// Set row selection mode, one row at a time
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Create sorter
		sorter = new TableRowSorter<>(productsModel);
		table.setRowSorter(sorter);

		// Set column width for PRODUCT_NAME
		TableColumn columnProductName = table.getColumnModel().getColumn(0);
		columnProductName.setPreferredWidth(200);

		// Set row height for all rows
		table.setRowHeight(25);

		// --------------------------------------------
		// Layout controls
		// ---------------------------------------------

		URL imageSearchURL = getClass().getResource("/resources/search.png");
		label_SEARCH_PRODUCT = new JLabel();
		label_SEARCH_PRODUCT.setIcon(new ImageIcon(imageSearchURL));

		label_PRODUCT_COUNT = new JLabel("Tổng số: " + productsModel.getRowCount() + " sản phẩm");

		textfield_FILTER_PRODUCT = new JTextField(11);
		textfield_FILTER_PRODUCT.setToolTipText("Tìm sản phẩm");

		URL imageAddURL = getClass().getResource("/resources/add.png");
		buttonAddRow = new JButton("Thêm sản phẩm");
		buttonAddRow.setIcon(new ImageIcon(imageAddURL));

		URL imageEditURL = getClass().getResource("/resources/edit.png");
		buttonEditRow = new JButton("Thay đổi sản phẩm");
		buttonEditRow.setIcon(new ImageIcon(imageEditURL));

		URL imageDeleteURL = getClass().getResource("/resources/delete.png");
		buttonDeleteRow = new JButton("Xóa sản phẩm");
		buttonDeleteRow.setIcon(new ImageIcon(imageDeleteURL));

		// Setup the toolbar
		bar = new MyToolBar();
		add(bar, BorderLayout.NORTH);

		// Place a panel within the container contentPane,
		// use GridBagLayout as the layout
		JPanel panel = new JPanel();
		panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		panel.setLayout(new GridBagLayout());
		add(panel, BorderLayout.CENTER);
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 0.15;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.insets = new Insets(0, 30, 0, 0);
		panel.add(label_SEARCH_PRODUCT, c);

		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.85;
		c.weighty = 0;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		c.insets = new Insets(0, -150, 0, 10);
		panel.add(textfield_FILTER_PRODUCT, c);

		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		c.insets = new Insets(0, 0, 0, 0);
		panel.add(new JScrollPane(table), c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 0.3;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.insets = new Insets(10, 20, 10, 20);
		panel.add(buttonAddRow, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 0.3;
		c.weighty = 0;
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		c.insets = new Insets(10, 40, 10, 40);
		panel.add(buttonEditRow, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_END;
		c.weightx = 0.3;
		c.weighty = 0;
		c.gridx = 2;
		c.gridy = 2;
		c.gridwidth = 1;
		c.insets = new Insets(10, 20, 10, 20);
		panel.add(buttonDeleteRow, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_END;
		c.weightx = 0.3;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		panel.add(label_PRODUCT_COUNT, c);

		// ---------- End of layout ----------

		// ------------------------------------
		// Setup listeners

		// Add filter trigger key
		textfield_FILTER_PRODUCT.getDocument().addDocumentListener(new SearchFilterListener());

		// Add listeners for the buttons in the application

		buttonEditRow.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int[] selected = table.getSelectedRows();
				for (int i : selected) {
					// Convert table-index to model-index
					int j = table.convertRowIndexToModel(i);
					// Show the EditRowDialog
					EditProductDialog dialog = new EditProductDialog(ProductsFrame.this, productsModel, j);
					dialog.setVisible(true);
				}
			}
		});

		buttonAddRow.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Show InsertRowDialog
				InsertProductDialog dialog = new InsertProductDialog(ProductsFrame.this, productsModel);
				dialog.setVisible(true);

			}
		});

		buttonDeleteRow.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int[] selected = table.getSelectedRows();
				for (int i : selected) {
					// Convert table-index to model-index
					int j = table.convertRowIndexToModel(i);
					// Show the confirm Dialog
					int result = JOptionPane.showConfirmDialog(ProductsFrame.this, "Bạn thật sự muốn xóa sản phẩm này?",
							"Xóa sản phẩm", JOptionPane.OK_CANCEL_OPTION);
					if (result == 0) {
						try (Connection connection = jdbcUtils.getConnection()) {
							// Delete a row in database
							try (Statement stat = connection.createStatement()) {
								String queryDelete = "delete from products where product_name = '"
										+ productsModel.getValueAt(j, 0) + "'";
								stat.executeUpdate(queryDelete);
								// Delete a row in model
								productsModel.removeRow(j);
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
						} catch (SQLException e2) {
							e2.printStackTrace();
						}

					}
				}
			}
		});

	} // End of constructor

	/**
	 * Setup search filter
	 */
	protected class SearchFilterListener implements DocumentListener {
		protected void changeFilter(DocumentEvent event) {
			Document document = event.getDocument();
			try {
				String filterRegex = "(?i).*" + document.getText(0, document.getLength()) + ".*";
				filterRegex = filterRegex.replace(" ", ".*");
				sorter.setRowFilter(RowFilter.regexFilter(filterRegex));

			} catch (Exception ex) {
				ex.printStackTrace();
				System.err.println(ex);
			}
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			changeFilter(e);

		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			changeFilter(e);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			changeFilter(e);
		}

	}

	/**
	 * Set default focus
	 */
	public void setDefaultFocus() {
		textfield_FILTER_PRODUCT.requestFocusInWindow();
	}

	public static void main(String[] args) {
		try {
			// iterate the list to determine if Nimbus is available
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			System.err.println("Nimbus is not available");
		}

		java.awt.EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					ProductsFrame frame = new ProductsFrame(new JDBCUtils());
					frame.setSize(720, 700);
					frame.setLocation(150, 0);
					// Set application icon
					URL iconURL = getClass().getResource("/resources/icon.png");
					frame.setIconImage(new ImageIcon(iconURL).getImage());
					frame.setVisible(true);
					frame.setDefaultFocus();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		});

	}

}
