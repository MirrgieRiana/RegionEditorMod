package mirrg.minecraft.regioneditor.gui;

import static mirrg.boron.swing.UtilsComponent.*;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

public class PanelMessage extends JPanel
{

	private JTextPane textPane;
	private JScrollPane scrollPane;

	public PanelMessage()
	{
		setLayout(new CardLayout());

		add(get(scrollPane = createScrollPane(textPane = get(new JTextPane(), c -> {
			c.setEditable(false);
		})), c -> {
			c.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			c.setBorder(null);
			c.setOpaque(false);
			c.setPreferredSize(new Dimension(800, 600));
		}));
	}

	public JTextPane getTextPane()
	{
		return textPane;
	}

	public void setText(String string)
	{
		textPane.setText(string);
		SwingUtilities.invokeLater(() -> {
			scrollPane.getVerticalScrollBar().setValue(0);
		});
	}

}
