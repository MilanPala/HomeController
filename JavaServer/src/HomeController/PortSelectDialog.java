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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PortSelectDialog extends javax.swing.JDialog
{

	private javax.swing.JButton connectButton;
	private javax.swing.JLabel jlPortName;
	private javax.swing.JComboBox portsBox;
	private JTextField serverPortInput;

	public static PortSelectDialog dialog;

	private final PortSelectDialogActionListener listener;

	public PortSelectDialog(PortSelectDialogActionListener listener, int serverPort)
	{
		this.listener = listener;
		initComponents(serverPort);
	}

	private void initComponents(int serverPort)
	{
		jlPortName = new javax.swing.JLabel();
		jlPortName.setText("Port Arduina:");

		portsBox = new javax.swing.JComboBox();
		portsBox.setModel(getPortsNames());

		JLabel serverPortLabel = new javax.swing.JLabel();
		serverPortLabel.setText("Port webového serveru:");

		serverPortInput = new JTextField(String.valueOf(serverPort));
		serverPortInput.setColumns(5);

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
		panel.add(serverPortLabel);
		panel.add(Box.createRigidArea(new Dimension(5, 0)));
		panel.add(serverPortInput);
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

	public static void showDialog(PortSelectDialogActionListener listener, int serverPort)
	{
		dialog = new PortSelectDialog(listener, serverPort);
		dialog.setTitle("Vyberte port Arduina");
		dialog.setVisible(true);
		dialog.setLocationRelativeTo(null);
	}

	public JComboBox getPortsBox()
	{
		return portsBox;
	}

	public int getServerPort()
	{
		return Integer.parseInt(serverPortInput.getText());
	}

	private void buttonActionListener(ActionEvent e)
	{
		listener.actionPerformed(dialog);
	}

}
