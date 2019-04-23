package mirrg.minecraft.regioneditor.gui.imagelayers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageLayer
{

	protected int width;
	protected int height;
	protected BufferedImage image;
	protected Graphics2D graphics;

	public ImageLayer()
	{
		resize(1, 1);
	}

	public void resize(int width, int height)
	{
		this.width = width;
		this.height = height;
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		graphics = image.createGraphics();
	}

	public BufferedImage getImage()
	{
		return image;
	}

	public Graphics2D getGraphics()
	{
		return graphics;
	}

}
