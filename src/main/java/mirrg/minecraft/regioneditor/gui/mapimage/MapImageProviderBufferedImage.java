package mirrg.minecraft.regioneditor.gui.mapimage;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class MapImageProviderBufferedImage implements IMapImageProvider
{

	private final BufferedImage image;
	private final Point mapOrigin;

	public MapImageProviderBufferedImage(BufferedImage image, Point mapOrigin)
	{
		this.image = image;
		this.mapOrigin = mapOrigin;
	}

	@Override
	public void draw(Graphics2D graphics, int srcX, int srcZ, int width, int height)
	{
		graphics.drawImage(
			image,
			0 - srcX - mapOrigin.x,
			0 - srcZ - mapOrigin.y,
			null);
	}

}
