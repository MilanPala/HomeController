package HomeController;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class ValueBox extends JPanel
{

	private String labelText;

	private JLabel labelLabel;
	private JLabel valueLabel;

	private ValueConvertor valueConvertor;

	public ValueBox(String label, ValueConvertor valueConvertor)
	{
		this.labelText = label;
		this.valueConvertor = valueConvertor;

		initComponents();
	}

	private void initComponents()
	{
		JPanel innerPanel = new JPanel();

		setBorder(javax.swing.BorderFactory.createTitledBorder(this.labelText));
		setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

		innerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

		this.labelLabel = new JLabel(this.labelText + ": ");
		innerPanel.add(this.labelLabel);

		this.valueLabel = new JLabel("---");
		innerPanel.add(this.valueLabel);

		add(innerPanel);
	}

	public void setValue(State state)
	{
		String valueText = this.valueConvertor.convert(state);
		this.valueLabel.setText(valueText);
	}

	public interface ValueConvertor
	{

		public String convert(State state);

	}

}
