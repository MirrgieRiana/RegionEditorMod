package mirrg.minecraft.regioneditor.gui;

import java.awt.Color;
import java.awt.Image;
import java.util.Optional;

import mirrg.minecraft.regioneditor.data.ChunkPosition;
import mirrg.minecraft.regioneditor.data.MapData;
import mirrg.minecraft.regioneditor.data.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.RegionInfo;

public class ImageLayerRegion extends ImageLayer
{

	public void update(Image imageBackground, MapData mapData, int positionX, int positionZ)
	{
		// width = 255, maptipWidth = 16 -> xRadius = (254 / 2 / 16 + 1) = (127 / 16 + 1) = (7 + 1) = 8
		// width = 256, maptipWidth = 16 -> xRadius = (255 / 2 / 16 + 1) = (127 / 16 + 1) = (7 + 1) = 8
		// width = 257, maptipWidth = 16 -> xRadius = (256 / 2 / 16 + 1) = (128 / 16 + 1) = (8 + 1) = 9
		// width = 258, maptipWidth = 16 -> xRadius = (257 / 2 / 16 + 1) = (128 / 16 + 1) = (8 + 1) = 9
		int xRadius = ((width - 1) / 2 / 16 + 1);
		int zRadius = ((height - 1) / 2 / 16 + 1);

		update(
			imageBackground,
			mapData,
			positionX,
			positionZ,
			new ChunkPosition(
				positionX - xRadius,
				positionZ - zRadius),
			new ChunkPosition(
				positionX + xRadius,
				positionZ + zRadius));
	}

	/**
	 * @param chunkPositionEnd
	 *            このチャンクまでが描画範囲に含まれる。
	 */
	public void update(Image imageBackground, MapData mapData, int positionX, int positionZ, ChunkPosition chunkPositionStart, ChunkPosition chunkPositionEnd)
	{
		graphics.drawImage(
			imageBackground,
			(chunkPositionStart.x - positionX) * 16 + width / 2,
			(chunkPositionStart.z - positionZ) * 16 + height / 2,
			(chunkPositionEnd.x - positionX) * 16 + width / 2 + 16,
			(chunkPositionEnd.z - positionZ) * 16 + height / 2 + 16,
			(chunkPositionStart.x - positionX) * 16 + width / 2,
			(chunkPositionStart.z - positionZ) * 16 + height / 2,
			(chunkPositionEnd.x - positionX) * 16 + width / 2 + 16,
			(chunkPositionEnd.z - positionZ) * 16 + height / 2 + 16,
			null);

		for (int x = chunkPositionStart.x; x <= chunkPositionEnd.x; x++) {
			for (int z = chunkPositionStart.z; z <= chunkPositionEnd.z; z++) {

				Optional<RegionIdentifier> oRegionIdentifier = mapData.regionMap.get(new ChunkPosition(x, z));
				if (oRegionIdentifier.isPresent()) {
					RegionInfo regionInfo = mapData.regionInfoTable.get(oRegionIdentifier.get());

					// 背景半透明塗りつぶし
					graphics.setColor(new Color(
						regionInfo.getDynmapColor().getRed(),
						regionInfo.getDynmapColor().getGreen(),
						regionInfo.getDynmapColor().getBlue(),
						64));
					graphics.fillRect((x - positionX) * 16 + width / 2, (z - positionZ) * 16 + height / 2, 16, 16);

					// 領地輪郭線
					graphics.setColor(new Color(
						regionInfo.getDynmapColor().getRed(),
						regionInfo.getDynmapColor().getGreen(),
						regionInfo.getDynmapColor().getBlue()));
					{
						int w = 2;

						// left
						if (!mapData.regionMap.get(new ChunkPosition(x - 1, z)).equals(oRegionIdentifier)) {
							graphics.fillRect((x - positionX) * 16 + 1 + 0 + width / 2, (z - positionZ) * 16 + 1 + 0 + height / 2, w, 16);
						}
						// right
						if (!mapData.regionMap.get(new ChunkPosition(x + 1, z)).equals(oRegionIdentifier)) {
							graphics.fillRect((x - positionX) * 16 + (16 - w) + width / 2, (z - positionZ) * 16 + 1 + 0 + height / 2, w, 16);
						}
						// top
						if (!mapData.regionMap.get(new ChunkPosition(x, z - 1)).equals(oRegionIdentifier)) {
							graphics.fillRect((x - positionX) * 16 + 1 + 0 + width / 2, (z - positionZ) * 16 + 1 + 0 + height / 2, 16, w);
						}
						// bottom
						if (!mapData.regionMap.get(new ChunkPosition(x, z + 1)).equals(oRegionIdentifier)) {
							graphics.fillRect((x - positionX) * 16 + 1 + 0 + width / 2, (z - positionZ) * 16 + (16 - w) + height / 2, 16, w);
						}
					}

					// 数値
					FontRenderer.drawString(
						image,
						"" + oRegionIdentifier.get().countryNumber,
						(x - positionX) * 16 + 8 + width / 2,
						(z - positionZ) * 16 + 2 + height / 2,
						regionInfo.countryColor);
					FontRenderer.drawString(
						image,
						"" + oRegionIdentifier.get().stateNumber,
						(x - positionX) * 16 + 8 + width / 2,
						(z - positionZ) * 16 + 8 + height / 2,
						regionInfo.stateColor);

				}

				// グリッド
				graphics.setColor(new Color(0x444444));
				graphics.drawRect((x - positionX) * 16 + width / 2, (z - positionZ) * 16 + height / 2, 16, 16);

			}

		}

	}

}
