package mirrg.minecraft.regioneditor.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

public class SwingUtils
{

	public static <T> T get(T t, Consumer<T> consumer)
	{
		consumer.accept(t);
		return t;
	}

	public static JSplitPane splitPaneHorizontal(double resizeWeight, Component left, Component right)
	{
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, left, right);
		splitPane.setResizeWeight(resizeWeight);
		return splitPane;
	}

	public static JSplitPane splitPaneVertical(double resizeWeight, Component up, Component down)
	{
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, up, down);
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

	public static JScrollPane scrollPane(Component component, int width, int height)
	{
		JScrollPane scrollPane = new JScrollPane(component);
		scrollPane.setPreferredSize(new Dimension(width, height));
		return scrollPane;
	}

	public static JPanel flowPanel(Component... components)
	{
		JPanel panel = new JPanel(new FlowLayout());
		for (Component component : components) {
			panel.add(component);
		}
		return panel;
	}

	public static JPanel marginPanel(int border, Component component)
	{
		JPanel panel = new JPanel(new CardLayout());
		panel.setBorder(new EmptyBorder(border, border, border, border));
		panel.add(component);
		return panel;
	}

	public static JPanel cardPanel(Component component)
	{
		JPanel panel = new JPanel(new CardLayout());
		panel.add(component);
		return panel;
	}

	public static JButton button(String text, ActionListener listener)
	{
		JButton button = new JButton(text);
		button.addActionListener(listener);
		return button;
	}

	public static JButton button(String text, Action action)
	{
		JButton button = new JButton();
		button.setAction(action);
		button.setText(text);
		return button;
	}

}
