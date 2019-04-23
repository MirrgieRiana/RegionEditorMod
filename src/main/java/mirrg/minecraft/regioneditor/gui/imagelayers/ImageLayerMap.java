package mirrg.minecraft.regioneditor.gui.imagelayers;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;

import mirrg.minecraft.regioneditor.data.controller.LayerController;

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
			}
		}

	}

}
