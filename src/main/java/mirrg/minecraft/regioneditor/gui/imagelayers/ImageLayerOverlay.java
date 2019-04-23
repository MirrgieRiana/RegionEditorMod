package mirrg.minecraft.regioneditor.gui.imagelayers;

import java.awt.Image;
import java.util.Optional;

import mirrg.minecraft.regioneditor.data.controller.LayerController;
import mirrg.minecraft.regioneditor.gui.tool.ITool;

public class ImageLayerOverlay extends ImageLayer
{

	public boolean showTooltip = true;

	public void update(Image imageBackground, LayerController layerController, Optional<ITool> oTool)
	{

		// 透過レイヤー描画
		graphics.drawImage(imageBackground, 0, 0, null);

		// ツール依存のポインタを描画
		if (oTool.isPresent()) oTool.get().draw(graphics);

		// ツール依存のツールチップを描画
		if (showTooltip) {
			if (oTool.isPresent()) oTool.get().drawTooltip(graphics);
		}

	}

}
