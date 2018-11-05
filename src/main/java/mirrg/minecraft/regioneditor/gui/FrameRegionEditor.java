package mirrg.minecraft.regioneditor.gui;

import static mirrg.minecraft.regioneditor.gui.SwingUtils.*;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.WindowConstants;

public class FrameRegionEditor
{

	private JFrame frame;

	public FrameRegionEditor()
	{
		frame = new JFrame("RegionEditor");

		// 内容
		{
			frame.setLayout(new BorderLayout());

			frame.add(splitPaneHorizontal(0.7,

				borderPanelUp(

					new JButton("↑"),

					borderPanelDown(

						borderPanelLeft(

							new JButton("←"),

							borderPanelRight(

								new JLabel("地図"),

								new JButton("→")

							)

						),

						new JButton("↓")

					)

				),

				borderPanelDown(

					scrollPane(new JTable()),

					flowPanel(

						new JButton("A"),

						new JButton("B"),

						new JButton("C"),

						new JButton("D")

					)

				)

			));
		}

		frame.setSize(600, 600);
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	public void show()
	{
		frame.setVisible(true);
	}

}
