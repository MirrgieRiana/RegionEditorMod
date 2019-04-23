package mirrg.minecraft.regioneditor.util.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class BitMapFont
{

	public BufferedImage image;
	public int width;
	public int height;
	public int offsetX;

	public BitMapFont(URL resource, int width, int height, int offsetX) throws IOException
	{
		image = ImageIO.read(resource);
		this.width = width;
		this.height = height;
		this.offsetX = offsetX;
	}

}
