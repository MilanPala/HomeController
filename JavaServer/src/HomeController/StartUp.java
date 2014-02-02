package HomeController;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class StartUp
{

	/**
	 * Milliseconds to block while waiting for port open
	 */
	private static final int TIME_OUT = 2000;

	private static int serverPort = 8080;

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[])
	{
		/*Logger log = Logger.getLogger(StartUp.class.getPackage().getName());
		 log.setLevel(Level.ALL);
		 ConsoleHandler handler = new ConsoleHandler();
		 handler.setLevel(Level.ALL);
		 handler.setFormatter(new SimpleFormatter());
		 log.addHandler(handler);*/

		if (args.length > 0) {
			serverPort = Integer.parseInt(args[0]);
		}

		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Windows".equals(info.getName())) { // největší prioritu má Microsoft Windows vzhled
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
				if ("GTK+".equals(info.getName())) { // poté defaultní téma GNOME
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}

		java.awt.EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				PortSelectDialog.showDialog(new PortSelectDialogActionListener()
				{

					@Override
					public void actionPerformed(PortSelectDialog dialog)
					{

						try {
							CommPortIdentifier portId = null;
							String portName = (String) dialog.getPortsBox().getSelectedItem();
							portId = CommPortIdentifier.getPortIdentifier(portName);

							// open serial port, and use class name for the appName.
							SerialPort port = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

							dialog.dispose();
							new MainWindow(port, dialog.getServerPort()).setVisible(true);
						} catch (NoSuchPortException ex) {
							Logger.getLogger(PortSelectDialog.class.getName()).log(Level.SEVERE, null, ex);
						} catch (PortInUseException ex) {
							JOptionPane.showMessageDialog(dialog,
									"Port již využívá jiná aplikace.",
									"Chyba otevření portu",
									JOptionPane.ERROR_MESSAGE);
						}

					}
				}, serverPort);
			}
		}
		);
	}

}
