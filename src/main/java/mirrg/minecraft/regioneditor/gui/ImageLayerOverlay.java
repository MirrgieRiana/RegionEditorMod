package mirrg.minecraft.regioneditor.gui;

import java.awt.Image;
import java.awt.Point;
import java.util.Optional;
import java.util.function.Function;

import mirrg.minecraft.regioneditor.data.MapData;
import mirrg.minecraft.regioneditor.data.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.RegionInfo;
import mirrg.minecraft.regioneditor.data.TilePosition;

public class ImageLayerOverlay extends ImageLayer
{

	public boolean showTooltip = true;

	public void update(Image imageBackground, MapData mapData, Optional<Point> oMousePosition, Function<Point, TilePosition> function)
	{
		graphics.drawImage(imageBackground, 0, 0, null);

		if (showTooltip) {
			if (oMousePosition.isPresent()) {

				int height = graphics.getFontMetrics().getHeight();

				TilePosition tilePosition = function.apply(oMousePosition.get());

				graphics.drawString(
					tilePosition.x + ", " + tilePosition.z,
					oMousePosition.get().x + 2,
					oMousePosition.get().y - height * 2 - 2);

				Optional<RegionIdentifier> oRegionIdentifier = mapData.regionMap.get(tilePosition);
				if (oRegionIdentifier.isPresent()) {
					RegionInfo regionInfo = mapData.regionInfoTable.get(oRegionIdentifier.get());

					graphics.drawString(
						"Country: (" + oRegionIdentifier.get().countryNumber + ") " + regionInfo.countryName,
						oMousePosition.get().x + 2,
						oMousePosition.get().y - height * 1 - 2);
					graphics.drawString(
						"State: (" + oRegionIdentifier.get().stateNumber + ") " + regionInfo.stateName,
						oMousePosition.get().x + 2,
						oMousePosition.get().y - height * 0 - 2);

				}

			}
		}

	}

}
