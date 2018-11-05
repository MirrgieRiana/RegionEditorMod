package mirrg.minecraft.regioneditor.gui;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class CanvasMap extends Canvas
{

	private BufferedImage map = null;

	public void setMap(BufferedImage map)
	{
		this.map = map;
		repaint();
	}

	@Override
	public void paint(Graphics g)
	{
		if (map != null) g.drawImage(map, 0, 0, null);
	}

}
