package mirrg.minecraft.regioneditor.gui;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class CanvasMap extends Canvas
{

	private BufferedImage map = null;
	private int x = 0;
	private int z = 0;

	public void setMap(BufferedImage map)
	{
		this.map = map;
		repaint();
	}

	@Override
	public void paint(Graphics g)
	{
		if (map != null) g.drawImage(map, 0 - x * 16, 0 - z * 16, null);
	}

	public int getPositionX()
	{
		return x;
	}

	public int getPositionZ()
	{
		return z;
	}

	public void setPosition(int x, int z)
	{
		this.x = x;
		this.z = z;
		repaint();
	}

}
