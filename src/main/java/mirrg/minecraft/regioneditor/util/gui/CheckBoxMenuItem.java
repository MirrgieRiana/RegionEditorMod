package mirrg.minecraft.regioneditor.util.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;

public class CheckBoxMenuItem extends JCheckBoxMenuItem
{

	public CheckBoxMenuItem(Action action)
	{
		super(action);
	}

	// コンテントペイン側との競合を無くすため
	@Override
	protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed)
	{
		return false;
	}

	@Override
	protected ItemListener createItemListener()
	{
		return new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event)
			{
				fireItemStateChanged(event);
			}
		};
	}

}
