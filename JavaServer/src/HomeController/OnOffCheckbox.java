package HomeController;

import javax.swing.JCheckBox;

public class OnOffCheckbox extends JCheckBox
{
	
	private String onText = "Zapnuto";
	private String offText = "Vypnuto";
	
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
