package mirrg.minecraft.regioneditor.gui;

import java.awt.Image;
import java.awt.Point;
import java.util.Optional;
import java.util.function.Function;

import mirrg.minecraft.regioneditor.data.MapData;
import mirrg.minecraft.regioneditor.data.TilePosition;

public class ImageLayerOverlay extends ImageLayer
{

	public boolean showTooltip = true;

	public void update(Image imageBackground, MapData mapData, Optional<ITool> oTool, Function<Point, TilePosition> function)
	{
		graphics.drawImage(imageBackground, 0, 0, null);

		if (oTool.isPresent()) oTool.get().draw(graphics, function);

		if (showTooltip) {
			if (oTool.isPresent()) oTool.get().drawTooltip(graphics, function);
		}

	}

}
