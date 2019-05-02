package mirrg.minecraft.regioneditor.gui.imagelayers;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import mirrg.minecraft.regioneditor.data.controller.LayerController;
import mirrg.minecraft.regioneditor.gui.mapimage.IMapImageProvider;

public class ImageLayerMap extends ImageLayer
{

	public boolean showMap = true;

	public void update(Image imageBackground, LayerController layerController, int tileXCenter, int tileZCenter, Point mapOrigin)
	{

		// 灰色で塗りつぶし
		graphics.setBackground(new Color(128, 128, 128));
		graphics.clearRect(0, 0, width, height);

		// 地図画像描画
		if (showMap) {
			if (imageBackground != null) {
				graphics.drawImage(
					imageBackground,
					0 - tileXCenter * 16 - mapOrigin.x + width / 2,
					0 - tileZCenter * 16 - mapOrigin.y + height / 2,
					null);
			} else {
				drawFromDynmapImageLoader(
					tileXCenter * 16 - width / 2,
					tileZCenter * 16 - height / 2,
					width,
					height);
			}
		}

	}

	private DynmapImageLoader dynmapImageLoader = new DynmapImageLoader("http://mimi2.f5.si:17026/tiles/world/flat/${x1}_${z1}/zz_${x2}_${z2}.png?1556325681500");

	private void drawFromDynmapImageLoader(int srcX, int srcZ, int width, int height)
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
