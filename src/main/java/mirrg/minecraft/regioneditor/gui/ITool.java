package mirrg.minecraft.regioneditor.gui;

import java.awt.Graphics2D;

public interface ITool
{

	public void on();

	public void off();

	public default void draw(Graphics2D graphics)
	{

	}

	public default void drawTooltip(Graphics2D graphics)
	{

	}

}
