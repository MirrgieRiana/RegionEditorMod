package mirrg.minecraft.regioneditor.gui;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class CanvasMap extends Canvas
{

	private BufferedImage map = null;
	private int positionX = 0;
	private int positionZ = 0;

	public void setMap(BufferedImage map)
	{
		this.map = map;
		repaint();
	}

	@Override
	public void paint(Graphics g)
	{
		if (map != null) g.drawImage(map, 0 - positionX * 16, 0 - positionZ * 16, null);
	}

	public int getPositionX()
	{
		return positionX;
	}

	public int getPositionZ()
	{
		return positionZ;
	}

	public void setPosition(int x, int z)
	{
		this.positionX = x;
		this.positionZ = z;
		repaint();
	}

}
