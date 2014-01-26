package HomeController;

import javax.swing.JToggleButton;

public class OnOffButton extends JToggleButton
{
	
	private String onText = "Vypnout";
	private String offText = "Zapnout";
	
	public void setOnText(String text)
	{
		this.onText = text;
	}
	
	public void setOffText(String text)
	{
		this.offText = text;
	}
	
	@Override
	public void setSelected(boolean b)
	{
		super.setSelected(b);
		
		if (b) {
			this.setText(onText);
		} else {
			this.setText(offText);
		}
	}
	
}
