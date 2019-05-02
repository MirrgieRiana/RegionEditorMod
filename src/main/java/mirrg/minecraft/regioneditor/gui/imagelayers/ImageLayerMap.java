package mirrg.minecraft.regioneditor.gui.imagelayers;

import java.awt.Color;
import java.util.Optional;

import mirrg.minecraft.regioneditor.data.controller.LayerController;
import mirrg.minecraft.regioneditor.gui.mapimage.IMapImageProvider;

public class ImageLayerMap extends ImageLayer
{

	public boolean showMap = true;
	public Optional<IMapImageProvider> oMapImageProvider = Optional.empty();

	public void update(LayerController layerController, int tileXCenter, int tileZCenter)
	{

		// 灰色で塗りつぶし
		graphics.setBackground(new Color(128, 128, 128));
		graphics.clearRect(0, 0, width, height);

		// 地図画像描画
		if (showMap) {
			if (oMapImageProvider.isPresent()) {
				oMapImageProvider.get().draw(
					graphics,
					tileXCenter * 16 - width / 2,
					tileZCenter * 16 - height / 2,
					width,
					height);
			}
		}

	}

}
