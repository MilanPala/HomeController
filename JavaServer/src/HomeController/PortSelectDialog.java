package HomeController;

import gnu.io.CommPortIdentifier;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public class PortSelectDialog extends javax.swing.JDialog
{

	private javax.swing.JButton connectButton;
	private javax.swing.JLabel jlPortName;
	private javax.swing.JComboBox portsBox;

	public static PortSelectDialog dialog;

	private final PortSelectDialogActionListener listener;

	public PortSelectDialog(PortSelectDialogActionListener listener)
	{
		this.listener = listener;
		initComponents();
	}

	private void initComponents()
	{
		jlPortName = new javax.swing.JLabel();
		jlPortName.setText("Název portu:");

		portsBox = new javax.swing.JComboBox();
		portsBox.setModel(getPortsNames());

		connectButton = new javax.swing.JButton();
		connectButton.setText("Připojit");
		connectButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				buttonActionListener(e);
			}

		});

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		JPanel panel = new JPanel();
		BoxLayout layout = new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.LINE_AXIS);
		panel.setLayout(layout);

		panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

		panel.add(jlPortName);
		panel.add(Box.createRigidArea(new Dimension(5, 0)));
		panel.add(portsBox);
		panel.add(Box.createRigidArea(new Dimension(5, 0)));
		panel.add(connectButton);

		getContentPane().add(panel);

		pack();
	}

	protected ComboBoxModel getPortsNames()
	{
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		Vector<String> portsNames = new Vector<>();

		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			portsNames.add(currPortId.getName());
		}
		return new javax.swing.DefaultComboBoxModel(portsNames);
	}

	public static void showDialog(PortSelectDialogActionListener listener)
	{
		dialog = new PortSelectDialog(listener);
		dialog.setTitle("Vyberte port Arduina");
		dialog.setVisible(true);
		dialog.setLocationRelativeTo(null);
	}

	public JComboBox getPortsBox()
	{
		return portsBox;
	}

	private void buttonActionListener(ActionEvent e)
	{
		listener.actionPerformed(dialog);
	}

}
