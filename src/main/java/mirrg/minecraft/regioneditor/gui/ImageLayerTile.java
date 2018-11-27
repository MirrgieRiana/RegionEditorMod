package mirrg.minecraft.regioneditor.gui;

import java.awt.Color;
import java.awt.Image;
import java.util.Optional;

import mirrg.minecraft.regioneditor.data.MapData;
import mirrg.minecraft.regioneditor.data.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.RegionInfo;
import mirrg.minecraft.regioneditor.data.TileIndex;

public class ImageLayerTile extends ImageLayer
{

	public boolean showTile = true;
	public boolean showArea = true;
	public boolean showBorder = true;
	public boolean showIdentifier = true;
	public boolean showGrid = true;

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
			new TileIndex(
				positionX - xRadius,
				positionZ - zRadius),
			new TileIndex(
				positionX + xRadius,
				positionZ + zRadius));
	}

	/**
	 * @param tileIndexEnd
	 *            このチャンクまでが描画範囲に含まれる。
	 */
	public void update(Image imageBackground, MapData mapData, int positionX, int positionZ, TileIndex tileIndexStart, TileIndex tileIndexEnd)
	{
		graphics.drawImage(
			imageBackground,
			(tileIndexStart.x - positionX) * 16 + width / 2,
			(tileIndexStart.z - positionZ) * 16 + height / 2,
			(tileIndexEnd.x - positionX) * 16 + width / 2 + 16,
			(tileIndexEnd.z - positionZ) * 16 + height / 2 + 16,
			(tileIndexStart.x - positionX) * 16 + width / 2,
			(tileIndexStart.z - positionZ) * 16 + height / 2,
			(tileIndexEnd.x - positionX) * 16 + width / 2 + 16,
			(tileIndexEnd.z - positionZ) * 16 + height / 2 + 16,
			null);

		for (int x = tileIndexStart.x; x <= tileIndexEnd.x; x++) {
			for (int z = tileIndexStart.z; z <= tileIndexEnd.z; z++) {
				TileIndex tileIndex = new TileIndex(x, z);

				if (showTile) {
					Optional<RegionIdentifier> oRegionIdentifier = mapData.tileMap.get(tileIndex);
					if (oRegionIdentifier.isPresent()) {
						RegionInfo regionInfo = mapData.regionInfoTable.get(oRegionIdentifier.get());

						drawRegionInfo(
							oRegionIdentifier.get(),
							regionInfo,
							x - positionX,
							z - positionZ,
							!mapData.tileMap.get(tileIndex.plus(-1, 0)).equals(oRegionIdentifier),
							!mapData.tileMap.get(tileIndex.plus(1, 0)).equals(oRegionIdentifier),
							!mapData.tileMap.get(tileIndex.plus(0, -1)).equals(oRegionIdentifier),
							!mapData.tileMap.get(tileIndex.plus(0, 1)).equals(oRegionIdentifier));

					}
				}

				// グリッド
				if (showGrid) {
					drawGrid(
						(x - positionX) * 16 + width / 2,
						(z - positionZ) * 16 + height / 2);
				}

			}

		}

	}

	private void drawRegionInfo(
		RegionIdentifier regionIdentifier,
		RegionInfo regionInfo,
		int x,
		int y,
		boolean borderLeft,
		boolean borderRight,
		boolean borderUp,
		boolean borderDown)
	{

		// 背景半透明塗りつぶし
		if (showArea) {
			graphics.setColor(new Color(
				regionInfo.stateColor.getRed(),
				regionInfo.stateColor.getGreen(),
				regionInfo.stateColor.getBlue(),
				64));
			graphics.fillRect(x * 16 + width / 2, y * 16 + height / 2, 16, 16);
		}

		// 領地輪郭線
		if (showBorder) {
			graphics.setColor(new Color(
				regionInfo.countryColor.getRed(),
				regionInfo.countryColor.getGreen(),
				regionInfo.countryColor.getBlue()));
			{
				int w = 2;

				// left
				if (borderLeft) graphics.fillRect(x * 16 + 1 + 0 + width / 2, y * 16 + 1 + 0 + height / 2, w, 15);

				// right
				if (borderRight) graphics.fillRect(x * 16 + (16 - w) + width / 2, y * 16 + 1 + 0 + height / 2, w, 15);

				// top
				if (borderUp) graphics.fillRect(x * 16 + 1 + 0 + width / 2, y * 16 + 1 + 0 + height / 2, 15, w);

				// bottom
				if (borderDown) graphics.fillRect(x * 16 + 1 + 0 + width / 2, y * 16 + (16 - w) + height / 2, 15, w);

			}
		}

		// 数値
		if (showIdentifier) {
			FontRenderer.drawString(
				image,
				"" + regionIdentifier.countryNumber,
				x * 16 + 8 + width / 2,
				y * 16 + 2 + height / 2,
				regionInfo.countryColor);
			FontRenderer.drawString(
				image,
				"" + regionIdentifier.stateNumber,
				x * 16 + 8 + width / 2,
				y * 16 + 8 + height / 2,
				regionInfo.stateColor);
		}

	}

	private void drawGrid(int x, int y)
	{
		graphics.setColor(new Color(0x444444));
		graphics.drawRect(x, y, 16, 16);
	}

}
