package mirrg.minecraft.regioneditor.gui;

import java.awt.Image;
import java.util.Optional;

import mirrg.minecraft.regioneditor.data.controller.LayerController;

public class ImageLayerOverlay extends ImageLayer
{

	public boolean showTooltip = true;

	public void update(Image imageBackground, LayerController layerController, Optional<ITool> oTool)
	{
		graphics.drawImage(imageBackground, 0, 0, null);

		if (oTool.isPresent()) oTool.get().draw(graphics);

		if (showTooltip) {
			if (oTool.isPresent()) oTool.get().drawTooltip(graphics);
		}

	}

}
