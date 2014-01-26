package HomeController;

import gnu.io.SerialPort;

public interface ComPortListener
{

	public void comPortConnected(SerialPort port);
}
