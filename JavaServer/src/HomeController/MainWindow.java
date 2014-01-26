package HomeController;

import gnu.io.SerialPort;
import java.awt.Dimension;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class MainWindow extends javax.swing.JFrame implements ArduinoDataEventListener
{

	private ArduinoController arduino;
	private ArduinoServer server;

	private ValueBox lightBox;
	private ValueBox temperatureBox;
	private OnOffBox relayABox;
	private OnOffBox relayBBox;

	private Timer timer;
	private LogTask logTask;

	public MainWindow(SerialPort port, int serverPortNumber)
	{
		initComponents();

		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				if (arduino != null) {
					arduino.close();
				}
			}
		});

		timer = new Timer();
		logTask = new LogTask();
		timer.schedule(logTask, 1000 * 5, 1000 * 60 * 5);

		this.arduino = new ArduinoController(port);
		this.arduino.addArduinoDataEventListener(this);

		try {
			this.server = new ArduinoServer(arduino, serverPortNumber);
		} catch (IOException ex) {
			Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
			JOptionPane.showMessageDialog(this,
					"Nepodařilo se spustit webový server.",
					"Chyba spuštění serveru",
					JOptionPane.ERROR_MESSAGE);
		}
		this.arduino.addArduinoDataEventListener(this.server);
		this.arduino.write("u:1");
	}

	private void initComponents()
	{
		lightBox = new ValueBox("Osvětlení", new ValueBox.ValueConvertor()
		{

			@Override
			public String convert(State state)
			{
				return String.format("%d %%", state.getLight());
			}
		});

		temperatureBox = new ValueBox("Teplota", new ValueBox.ValueConvertor()
		{

			@Override
			public String convert(State state)
			{
				return String.format("%5.2f °C", state.getTemperature());
			}
		});

		relayABox = new OnOffBox("Relé A", "Zapnuto", "Vypnuto", "Zapnout", "Vypnout", new OnOffBox.ValueConvertor()
		{

			@Override
			public Boolean setValue(State state)
			{
				return state.isRelayA();
			}
		});

		relayABox.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				relayAButtonActionPerformed(evt);
			}
		});

		relayBBox = new OnOffBox("Relé B", "Zapnuto", "Vypnuto", "Zapnout", "Vypnout", new OnOffBox.ValueConvertor()
		{

			@Override
			public Boolean setValue(State state)
			{
				return state.isRelayB();
			}
		});

		relayBBox.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				relayBButtonActionPerformed(evt);
			}
		});

		JPanel innerPanel = new JPanel();
		BoxLayout layout = new javax.swing.BoxLayout(innerPanel, javax.swing.BoxLayout.Y_AXIS);
		innerPanel.setLayout(layout);
		innerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		innerPanel.add(lightBox);
		innerPanel.add(temperatureBox);
		innerPanel.add(relayABox);
		innerPanel.add(relayBBox);

		add(innerPanel);

		pack();
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(320, 320));
		setTitle("Ovládání domácnosti");
	}

	private void relayAButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		JToggleButton button = (JToggleButton) evt.getSource();
		this.arduino.write("r:" + (button.isSelected() ? "1" : "0"));
	}

	private void relayBButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		JToggleButton button = (JToggleButton) evt.getSource();
		this.arduino.write("s:" + (button.isSelected() ? "1" : "0"));
	}

	@Override
	public void dataReady(State data)
	{
		this.lightBox.setValue(data);
		this.temperatureBox.setValue(data);
		this.relayABox.setValue(data);
		this.relayBBox.setValue(data);
		this.logTask.dataReady(data);
	}

	class LogTask extends TimerTask implements ArduinoDataEventListener
	{

		private State state = null;

		@Override
		public void run()
		{
			if (state == null) {
				return;
			}

			FileWriter fileWriter = null;
			boolean newFile;
			try {
				File file = new File("homecontroller.log");
				newFile = !file.exists();
				fileWriter = new FileWriter(file, true);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				if (newFile) {
					fileWriter.write("Datum;Osvětlení;Teplota\n");
				}
				fileWriter.write(dateFormat.format(cal.getTime()) + ";" + String.format("%d", state.getLight()) + ";" + String.format("%5.2f", state.getTemperature()) + "\n");
			} catch (IOException ex) {
				Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
			} finally {
				try {
					if (fileWriter != null) {
						fileWriter.close();
					}
				} catch (IOException ex) {
					Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}

		@Override
		public void dataReady(State data)
		{
			this.state = data;
		}

	}

}
