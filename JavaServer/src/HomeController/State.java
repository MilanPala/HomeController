package HomeController;

public class State
{

	private int light = 0;

	private boolean relayA = false;

	private boolean relayB = false;

	private float temperature = 0;

	public State()
	{
	}

	public void parseInput(String state)
	{
		char type = state.charAt(0);
		if (type != ':') {
			return;
		}
		type = state.charAt(1);
		String value = state.substring(3);

		if (type == 'l') {
			this.light = Integer.decode(value);
		}
		if (type == 'r') {
			this.relayA = Integer.decode(value) == 1;
		}
		if (type == 's') {
			this.relayB = Integer.decode(value) == 1;
		}
		if (type == 't') {
			this.temperature = Float.valueOf(value);
		}
	}

	public int getLight()
	{
		return light;
	}

	public boolean isRelayA()
	{
		return relayA;
	}

	public boolean isRelayB()
	{
		return relayB;
	}

	public float getTemperature()
	{
		return temperature;
	}

	public String toString()
	{
		String s = new String();
		s += "light: " + light + " %; ";
		s += "relayA: " + relayA + "; ";
		s += "relayB: " + relayB + "; ";
		s += "temp: " + temperature + "; ";
		return s;
	}

}
