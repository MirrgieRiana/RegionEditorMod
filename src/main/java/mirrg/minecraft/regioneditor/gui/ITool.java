package mirrg.minecraft.regioneditor.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.function.Function;

import mirrg.minecraft.regioneditor.data.TilePosition;

public interface ITool
{

	public void on();

	public void off();

	public default void draw(Graphics2D graphics, Function<Point, TilePosition> function)
	{

	}

	public default void drawTooltip(Graphics2D graphics, Function<Point, TilePosition> function)
	{

	}

}
