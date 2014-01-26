package HomeController;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArduinoController implements SerialPortEventListener
{

	SerialPort serialPort;

	private BufferedReader input;

	private OutputStream output;

	private static final int DATA_RATE = 9600;

	private final List<ArduinoDataEventListener> listeners = new ArrayList<>();

	private final State state;

	public ArduinoController(SerialPort port)
	{
		this.serialPort = port;
		this.initialize();
		this.state = new State();
	}

	private void initialize()
	{
		try {
			serialPort.setSerialPortParams(
					DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (UnsupportedCommOperationException | IOException | TooManyListenersException ex) {
			Logger.getLogger(ArduinoController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public synchronized void close()
	{
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	@Override
	public synchronized void serialEvent(SerialPortEvent oEvent)
	{
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				if (input.ready()) {
					String inputLine = input.readLine();
					this.state.parseInput(inputLine);
					for (ArduinoDataEventListener listener : this.listeners) {
						if (listener != null) {
							listener.dataReady(state);
						}
					}
				}
			} catch (IOException ex) {
				Logger.getLogger(ArduinoController.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	public void addArduinoDataEventListener(ArduinoDataEventListener l)
	{
		this.listeners.add(l);
	}

	public void write(String s)
	{
		try (PrintStream ps = new PrintStream(output)) {
			ps.print(s);
			ps.flush();
		}
	}

}
