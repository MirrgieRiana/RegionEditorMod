package mirrg.minecraft.regioneditor.gui;

import java.awt.Image;
import java.awt.Point;
import java.util.Optional;
import java.util.function.Function;

import mirrg.minecraft.regioneditor.data.ChunkPosition;
import mirrg.minecraft.regioneditor.data.MapData;
import mirrg.minecraft.regioneditor.data.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.RegionInfo;

public class ImageLayerMouse extends ImageLayer
{

	public void update(Image imageBackground, MapData mapData, int positionX, int positionZ, Optional<Point> oMousePosition, Function<Point, ChunkPosition> function)
	{
		graphics.drawImage(imageBackground, 0, 0, null);

		if (oMousePosition.isPresent()) {

			int height = graphics.getFontMetrics().getHeight();

			ChunkPosition chunkPosition = function.apply(oMousePosition.get());

			graphics.drawString(
				chunkPosition.x + ", " + chunkPosition.z,
				oMousePosition.get().x + 2,
				oMousePosition.get().y - height * 2 - 2);

			Optional<RegionIdentifier> oRegionIdentifier = mapData.regionMap.get(chunkPosition);
			if (oRegionIdentifier.isPresent()) {
				RegionInfo regionInfo = mapData.regionInfoTable.get(oRegionIdentifier.get());

				graphics.drawString(
					"Country: (" + regionInfo.regionIdentifier.countryNumber + ") " + regionInfo.countryName,
					oMousePosition.get().x + 2,
					oMousePosition.get().y - height * 1 - 2);
				graphics.drawString(
					"State: (" + regionInfo.regionIdentifier.stateNumber + ") " + regionInfo.stateName,
					oMousePosition.get().x + 2,
					oMousePosition.get().y - height * 0 - 2);

			}

		}

	}

}
