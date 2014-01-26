package HomeController;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class OnOffBox extends JPanel
{

	private String title;
	private String onLabel;
	private String offLabel;
	private String onActionLabel;
	private String offActionLabel;

	private JLabel labelLabel;

	private OnOffButton button;
	private OnOffCheckbox state;

	private OnOffBox.ValueConvertor valueConvertor;

	private List<Timer> timers;

	public OnOffBox(String title, String onLabel, String offLabel, String onActionLabel, String offActionLabel, OnOffBox.ValueConvertor valueConvertor)
	{
		this.title = title;
		this.onLabel = onLabel;
		this.offLabel = offLabel;
		this.onActionLabel = onActionLabel;
		this.offActionLabel = offActionLabel;
		this.valueConvertor = valueConvertor;
		timers = new ArrayList<>();

		initComponents();
	}

	private void initComponents()
	{
		JPanel innerPanel = new JPanel();

		setBorder(javax.swing.BorderFactory.createTitledBorder(this.title));
		setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

		innerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

		this.labelLabel = new JLabel(this.title);
		innerPanel.add(this.labelLabel);

		this.state = new OnOffCheckbox();
		this.state.setOnText(this.onLabel);
		this.state.setOffText(this.offLabel);
		this.state.setEnabled(false);
		innerPanel.add(this.state);

		this.button = new OnOffButton();
		this.button.setOffText(this.onActionLabel);
		this.button.setOnText(this.offActionLabel);
		innerPanel.add(this.button);

		add(innerPanel);
	}

	public void setValue(State state)
	{
		Boolean value = this.valueConvertor.setValue(state);
		this.button.setSelected(value);
		this.state.setSelected(value);
	}

	public void addActionListener(ActionListener listener)
	{
		this.button.addActionListener(listener);
	}

	public interface ValueConvertor
	{

		public Boolean setValue(State state);

	}

}
