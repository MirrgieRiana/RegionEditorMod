package mirrg.minecraft.regioneditor.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

public class SwingUtils
{

	public static JSplitPane splitPaneHorizontal(double resizeWeight, Component left, Component right)
	{
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, left, right);
		splitPane.setResizeWeight(resizeWeight);
		return splitPane;
	}

	public static JPanel borderPanelLeft(Component left, Component right)
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(left, BorderLayout.WEST);
		panel.add(right, BorderLayout.CENTER);
		return panel;
	}

	public static JPanel borderPanelRight(Component left, Component right)
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(left, BorderLayout.CENTER);
		panel.add(right, BorderLayout.EAST);
		return panel;
	}

	public static JPanel borderPanelUp(Component up, Component down)
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(up, BorderLayout.NORTH);
		panel.add(down, BorderLayout.CENTER);
		return panel;
	}

	public static JPanel borderPanelDown(Component up, Component down)
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(up, BorderLayout.CENTER);
		panel.add(down, BorderLayout.SOUTH);
		return panel;
	}

	public static JScrollPane scrollPane(Component component)
	{
		return new JScrollPane(component);
	}

	public static JPanel flowPanel(Component... components)
	{
		JPanel panel = new JPanel(new FlowLayout());
		for (Component component : components) {
			panel.add(component);
		}
		return panel;
	}

}
