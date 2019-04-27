package mirrg.minecraft.regioneditor.util.gui;

import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MenuItem extends JMenuItem
{

	public MenuItem(Action action)
	{
		super(action);
	}

	// コンテントペイン側との競合を無くすため
	@Override
	protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed)
	{
		return false;
	}

}
