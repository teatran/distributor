package tea.org;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;

@SuppressWarnings("serial")
public class MyToolBar extends JToolBar
{
	boolean priceVisible = false;
	
	public MyToolBar() 
	{
		URL customerURL = getClass().getResource("/resources/customer.png");
		Action openCustomer = new OpenAction("openCustomer", 
				new ImageIcon(customerURL), "Khách hàng");
		add(openCustomer);
		
		addSeparator();
		
		URL invoiceURL = getClass().getResource("/resources/invoice.png");
		Action openInvoice = new OpenAction("openInvoice", 
				new ImageIcon(invoiceURL), "Đơn hàng");
		add(openInvoice);
		
		addSeparator();
		
		URL priceURL = getClass().getResource("/resources/price.png");
		Action openPrice = new OpenAction("openPrice", 
				new ImageIcon(priceURL), "Giá cả");
		add(openPrice);
		
	}
	
	class OpenAction extends AbstractAction
	{
		public OpenAction(String name, Icon icon, String description)
		{
			putValue(Action.NAME, name);
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.SHORT_DESCRIPTION, description);
		}

		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			java.awt.EventQueue.invokeLater(new Runnable()
			{
				
				@Override
				public void run()
				{
					
				}
			});
		}
			
		
	}

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
