package mirrg.minecraft.regioneditor.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Optional;

import mirrg.minecraft.regioneditor.data.controller.PossessionMapModel;
import mirrg.minecraft.regioneditor.data.model.RegionEntry;
import mirrg.minecraft.regioneditor.data.model.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.model.RegionInfo;
import mirrg.minecraft.regioneditor.data.model.TileIndex;

public class ImageLayerTile extends ImageLayer
{

	public boolean showTile = true;
	public boolean showArea = true;
	public boolean showBorder = true;
	public boolean showIdentifier = true;
	public boolean showGrid = true;

	public void update(Image imageBackground, PossessionMapModel possessionMapModel, int tileXCenter, int tileZCenter)
	{
		// width = 255, maptipWidth = 16 -> xRadius = (254 / 2 / 16 + 1) = (127 / 16 + 1) = (7 + 1) = 8
		// width = 256, maptipWidth = 16 -> xRadius = (255 / 2 / 16 + 1) = (127 / 16 + 1) = (7 + 1) = 8
		// width = 257, maptipWidth = 16 -> xRadius = (256 / 2 / 16 + 1) = (128 / 16 + 1) = (8 + 1) = 9
		// width = 258, maptipWidth = 16 -> xRadius = (257 / 2 / 16 + 1) = (128 / 16 + 1) = (8 + 1) = 9
		int xRadius = ((width - 1) / 2 / 16 + 1);
		int zRadius = ((height - 1) / 2 / 16 + 1);

		update(
			imageBackground,
			possessionMapModel,
			tileXCenter,
			tileZCenter,
			new TileIndex(
				tileXCenter - xRadius,
				tileZCenter - zRadius),
			new TileIndex(
				tileXCenter + xRadius,
				tileZCenter + zRadius));
	}

	/**
	 * @param tileIndexEnd
	 *            このチャンクまでが描画範囲に含まれる。
	 */
	public void update(Image imageBackground, PossessionMapModel possessionMapModel, int tileXCenter, int tileZCenter, TileIndex tileIndexStart, TileIndex tileIndexEnd)
	{
		graphics.drawImage(
			imageBackground,
			(tileIndexStart.x - tileXCenter) * 16 + width / 2,
			(tileIndexStart.z - tileZCenter) * 16 + height / 2,
			(tileIndexEnd.x - tileXCenter) * 16 + width / 2 + 16,
			(tileIndexEnd.z - tileZCenter) * 16 + height / 2 + 16,
			(tileIndexStart.x - tileXCenter) * 16 + width / 2,
			(tileIndexStart.z - tileZCenter) * 16 + height / 2,
			(tileIndexEnd.x - tileXCenter) * 16 + width / 2 + 16,
			(tileIndexEnd.z - tileZCenter) * 16 + height / 2 + 16,
			null);

		for (int tileX = tileIndexStart.x; tileX <= tileIndexEnd.x; tileX++) {
			for (int tileZ = tileIndexStart.z; tileZ <= tileIndexEnd.z; tileZ++) {
				TileIndex tileIndex = new TileIndex(tileX, tileZ);

				if (showTile) {
					Optional<RegionIdentifier> oRegionIdentifier = possessionMapModel.tileMapModel.getDataReader().get(tileIndex);
					if (oRegionIdentifier.isPresent()) {
						RegionInfo regionInfo = possessionMapModel.regionTableModel.getDataReader().get(oRegionIdentifier.get());

						drawRegionInfo(
							new RegionEntry(oRegionIdentifier.get(), regionInfo),
							tileX - tileXCenter,
							tileZ - tileZCenter,
							!possessionMapModel.tileMapModel.getDataReader().get(tileIndex.plus(-1, 0)).equals(oRegionIdentifier),
							!possessionMapModel.tileMapModel.getDataReader().get(tileIndex.plus(1, 0)).equals(oRegionIdentifier),
							!possessionMapModel.tileMapModel.getDataReader().get(tileIndex.plus(0, -1)).equals(oRegionIdentifier),
							!possessionMapModel.tileMapModel.getDataReader().get(tileIndex.plus(0, 1)).equals(oRegionIdentifier));

					}
				}

				// グリッド
				if (showGrid) {
					drawGrid(graphics, (tileX - tileXCenter) * 16 + width / 2, (tileZ - tileZCenter) * 16 + height / 2, 16);
				}

			}

		}

	}

	private void drawRegionInfo(
		RegionEntry regionEntry,
		int tileX,
		int tileZ,
		boolean borderLeft,
		boolean borderRight,
		boolean borderUp,
		boolean borderDown)
	{
		int x = tileX * 16 + width / 2;
		int y = tileZ * 16 + height / 2;

		// 背景半透明塗りつぶし
		if (showArea) {
			drawArea(graphics, regionEntry, x, y, 16);
		}

		// 領地輪郭線
		if (showBorder) {
			drawBorder(graphics, regionEntry, x, y, 16, borderLeft, borderRight, borderUp, borderDown);
		}

		// 数値
		if (showIdentifier) {
			drawIdentifier(image, regionEntry, x, y, 16);
		}

	}

	public static void drawArea(Graphics2D graphics, RegionEntry regionEntry, int x, int y, int tileSize)
	{
		graphics.setColor(new Color(
			regionEntry.regionInfo.stateColor.getRed(),
			regionEntry.regionInfo.stateColor.getGreen(),
			regionEntry.regionInfo.stateColor.getBlue(),
			64));
		graphics.fillRect(x, y, tileSize, tileSize);
	}

	public static void drawBorder(
		Graphics2D graphics,
		RegionEntry regionEntry,
		int x,
		int y,
		int tileSize,
		boolean borderLeft,
		boolean borderRight,
		boolean borderUp,
		boolean borderDown)
	{
		graphics.setColor(new Color(
			regionEntry.regionInfo.countryColor.getRed(),
			regionEntry.regionInfo.countryColor.getGreen(),
			regionEntry.regionInfo.countryColor.getBlue()));
		int w = 2;
		if (borderLeft) graphics.fillRect(x + 1 + 0, y + 1 + 0, w, tileSize - 1);
		if (borderRight) graphics.fillRect(x + (tileSize - w), y + 1 + 0, w, tileSize - 1);
		if (borderUp) graphics.fillRect(x + 1 + 0, y + 1 + 0, tileSize - 1, w);
		if (borderDown) graphics.fillRect(x + 1 + 0, y + (tileSize - w), tileSize - 1, w);
	}

	public static void drawIdentifier(BufferedImage image, RegionEntry regionEntry, int x, int y, int tileSize)
	{
		FontRenderer.drawString(
			image,
			"" + regionEntry.regionIdentifier.countryNumber,
			x + tileSize / 2,
			y + tileSize / 2 - 6,
			regionEntry.regionInfo.countryColor);
		FontRenderer.drawString(
			image,
			"" + regionEntry.regionIdentifier.stateNumber,
			x + tileSize / 2,
			y + tileSize / 2,
			regionEntry.regionInfo.stateColor);
	}

	public static void drawGrid(Graphics2D graphics, int x, int y, int tileSize)
	{
		graphics.setColor(new Color(0x444444));
		graphics.drawRect(x, y, tileSize, tileSize);
	}

}
