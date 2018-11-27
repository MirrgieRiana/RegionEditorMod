package mirrg.minecraft.regioneditor.gui;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;

import mirrg.minecraft.regioneditor.data.RegionMapModel;

public class ImageLayerMap extends ImageLayer
{

	public boolean showMap = true;

	public void update(Image imageBackground, RegionMapModel regionMapModel, int positionX, int positionZ, Point mapOrigin)
	{
		graphics.setBackground(new Color(128, 128, 128));
		graphics.clearRect(0, 0, width, height);
		if (showMap) {
			if (imageBackground != null) {
				graphics.drawImage(
					imageBackground,
					0 - positionX * 16 - mapOrigin.x + width / 2,
					0 - positionZ * 16 - mapOrigin.y + height / 2,
					null);
			}
		}
	}

}
