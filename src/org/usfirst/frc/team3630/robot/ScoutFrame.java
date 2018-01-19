import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ScoutFrame extends JFrame {
	
	static final long serialVersionUID = 1;
	
	JFrame frame = new JFrame("Scouting 2018 code");
	JTextField textField1;
	JTextField textField2;
	JTextField textField3;
	JTextField textField4;
	JTextField textField5;
	JTextField textField6;
	JLabel label1;
	JLabel label2;
	JLabel label3;
	JLabel label4;
	JLabel label5;
	JLabel label6;
	JButton subButton;
	
	final int width = 65;
	final int length = 30;
	
	public ScoutFrame() {
		getContentPane().setLayout(new FlowLayout());
		textField1 = new JTextField();
		textField2 = new JTextField();
		textField3 = new JTextField();
		textField4 = new JTextField();
		textField5 = new JTextField();
		textField6 = new JTextField();
		label1 = new JLabel("Team #:");
		label2 = new JLabel("Can place boxes in vault:");
		label3 = new JLabel("Can place boxes on switch:");
		label4 = new JLabel("Match #:");
		label5 = new JLabel("Time to place boxes in vault:");
		label6 = new JLabel("Time to place boxes on switch:");
		subButton = new JButton("Submit Text");
		
		setVisible(true);
		
		add(label1);
		add(textField1);
		add(label2);
		add(textField2);
		add(label3);
		add(textField3);
		add(label4);
		add(textField4);
		add(label5);
		add(textField5);
		add(label6);
		add(textField6);
		add(subButton);
		
		frame.setSize(1440, 900);
		
		textField1.setPreferredSize(new Dimension(width, length));
		textField2.setPreferredSize(new Dimension(width, length));
		textField3.setPreferredSize(new Dimension(width, length));
		textField4.setPreferredSize(new Dimension(width, length));
		textField5.setPreferredSize(new Dimension(width, length));
		textField6.setPreferredSize(new Dimension(width, length));
	}
}