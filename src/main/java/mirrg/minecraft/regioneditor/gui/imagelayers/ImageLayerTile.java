package mirrg.minecraft.regioneditor.gui.imagelayers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Optional;

import mirrg.minecraft.regioneditor.data.controller.LayerController;
import mirrg.minecraft.regioneditor.data.objects.RegionEntry;
import mirrg.minecraft.regioneditor.data.objects.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.objects.RegionInfo;
import mirrg.minecraft.regioneditor.data.objects.TileCoordinate;
import mirrg.minecraft.regioneditor.data.objects.TileRectangle;
import mirrg.minecraft.regioneditor.util.gui.FontRenderer;

public class ImageLayerTile extends ImageLayer
{

	public boolean showTile = true;
	public boolean showArea = true;
	public boolean showBorder = true;
	public boolean showIdentifier = true;
	public boolean showGrid = true;

	public FontRenderer fontRenderer;

	public ImageLayerTile(FontRenderer fontRenderer)
	{
		this.fontRenderer = fontRenderer;
	}

	/**
	 * 画面全体を再描画します。
	 */
	public void update(Image imageBackground, LayerController layerController, int tileXCenter, int tileZCenter)
	{
		// 範囲を描画
		update(
			imageBackground,
			layerController,
			tileXCenter,
			tileZCenter,
			getVisibleArea(tileXCenter, tileZCenter));

	}

	public TileRectangle getVisibleArea(int tileXCenter, int tileZCenter)
	{
		// 表示矩形範囲の「半径」の算出
		// width = 255, maptipWidth = 16 -> xRadius = (254 / 2 / 16 + 1) = (127 / 16 + 1) = (7 + 1) = 8
		// width = 256, maptipWidth = 16 -> xRadius = (255 / 2 / 16 + 1) = (127 / 16 + 1) = (7 + 1) = 8
		// width = 257, maptipWidth = 16 -> xRadius = (256 / 2 / 16 + 1) = (128 / 16 + 1) = (8 + 1) = 9
		// width = 258, maptipWidth = 16 -> xRadius = (257 / 2 / 16 + 1) = (128 / 16 + 1) = (8 + 1) = 9
		int xRadius = ((width - 1) / 2 / 16 + 1);
		int zRadius = ((height - 1) / 2 / 16 + 1);

		return new TileRectangle(
			tileXCenter - xRadius,
			tileZCenter - zRadius,
			tileXCenter + xRadius,
			tileZCenter + zRadius);
	}

	public boolean isVisible(int tileXCenter, int tileZCenter, TileCoordinate tileCoordinate)
	{
		return getVisibleArea(tileXCenter, tileZCenter).contains(tileCoordinate);
	}

	/**
	 * 画面の一部分のタイルを再描画します。
	 */
	public void update(Image imageBackground, LayerController layerController, int tileXCenter, int tileZCenter, TileRectangle tileRactangle)
	{

		// 透過レイヤー貼り付け
		graphics.drawImage(
			imageBackground,
			(tileRactangle.min.x - tileXCenter) * 16 + width / 2,
			(tileRactangle.min.z - tileZCenter) * 16 + height / 2,
			(tileRactangle.max.x - tileXCenter) * 16 + width / 2 + 16,
			(tileRactangle.max.z - tileZCenter) * 16 + height / 2 + 16,
			(tileRactangle.min.x - tileXCenter) * 16 + width / 2,
			(tileRactangle.min.z - tileZCenter) * 16 + height / 2,
			(tileRactangle.max.x - tileXCenter) * 16 + width / 2 + 16,
			(tileRactangle.max.z - tileZCenter) * 16 + height / 2 + 16,
			null);

		// タイルごとに処理
		for (int tileX = tileRactangle.min.x; tileX <= tileRactangle.max.x; tileX++) {
			for (int tileZ = tileRactangle.min.z; tileZ <= tileRactangle.max.z; tileZ++) {
				TileCoordinate tileCoordinate = new TileCoordinate(tileX, tileZ);

				// タイルの描画
				if (showTile) {
					Optional<RegionIdentifier> tile = layerController.tileMapController.model.getTile(tileCoordinate);
					if (tile.isPresent()) {
						RegionInfo regionInfo = layerController.regionTableController.model.get(tile.get());

						// 1個のタイルを描画する
						drawRegionInfo(
							new RegionEntry(tile.get(), regionInfo),
							tileX - tileXCenter,
							tileZ - tileZCenter,
							!layerController.tileMapController.model.getTile(tileCoordinate.plus(-1, 0)).equals(tile),
							!layerController.tileMapController.model.getTile(tileCoordinate.plus(1, 0)).equals(tile),
							!layerController.tileMapController.model.getTile(tileCoordinate.plus(0, -1)).equals(tile),
							!layerController.tileMapController.model.getTile(tileCoordinate.plus(0, 1)).equals(tile));

					}
				}

				// グリッドの描画
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

		// 識別番号
		if (showIdentifier) {
			drawIdentifier(image, regionEntry, x, y, 16, fontRenderer);
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

	public static void drawIdentifier(BufferedImage image, RegionEntry regionEntry, int x, int y, int tileSize, FontRenderer fontRenderer)
	{
		fontRenderer.drawString(
			image,
			"" + regionEntry.regionIdentifier.countryId,
			x + tileSize / 2,
			y + tileSize / 2 - 6,
			regionEntry.regionInfo.countryColor);
		fontRenderer.drawString(
			image,
			"" + regionEntry.regionIdentifier.stateId,
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
