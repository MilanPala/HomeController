package HomeController;

public class Timer
{

	private boolean toggle = false;

	private int hours;

	private int minutes;

	public boolean isToggle()
	{
		return toggle;
	}

	public int getHours()
	{
		return hours;
	}

	public int getMinutes()
	{
		return minutes;
	}

	public void setToggle(boolean toggle)
	{
		this.toggle = toggle;
	}

	public void setHours(int hours)
	{
		this.hours = hours;
	}

	public void setMinutes(int minutes)
	{
		this.minutes = minutes;
	}

}
