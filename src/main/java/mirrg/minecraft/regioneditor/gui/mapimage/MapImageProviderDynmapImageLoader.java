package mirrg.minecraft.regioneditor.gui.mapimage;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;

public class MapImageProviderDynmapImageLoader implements IMapImageProvider
{

	private final DynmapImageLoader dynmapImageLoader;

	public MapImageProviderDynmapImageLoader(String templateUrl) throws MalformedURLException
	{
		this.dynmapImageLoader = new DynmapImageLoader(templateUrl);
	}

	@Override
	public void draw(Graphics2D graphics, int srcX, int srcZ, int width, int height)
	{
		int imageXMin = Math.floorDiv(srcX, 128);
		int imageZMin = Math.floorDiv(srcZ + 32, 128);
		int imageXMax = Math.floorDiv(srcX + width - 1, 128);
		int imageZMax = Math.floorDiv(srcZ + height - 1 + 32, 128);

		for (int imageX = imageXMin; imageX <= imageXMax; imageX++) {
			for (int imageZ = imageZMin; imageZ <= imageZMax; imageZ++) {

				BufferedImage image;
				try {
					image = dynmapImageLoader.get(imageX, imageZ);
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}

				graphics.drawImage(
					image,
					0 - srcX + imageX * 128,
					0 - srcZ + imageZ * 128 - 32,
					null);
			}
		}
	}

}
