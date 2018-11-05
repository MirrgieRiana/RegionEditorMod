package mirrg.minecraft.regioneditor;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import net.minecraft.world.World;

public class FrameRegionEditor
{

	private World world;

	private JFrame frame;

	public FrameRegionEditor(World world)
	{
		this.world = world;

		frame = new JFrame("RegionEditor");

		// 内容
		{
			frame.setLayout(new BorderLayout());

			frame.add(new JLabel("スタブ"));

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
