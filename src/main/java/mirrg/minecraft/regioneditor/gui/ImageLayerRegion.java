package mirrg.minecraft.regioneditor.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Optional;

import mirrg.minecraft.regioneditor.data.ChunkPosition;
import mirrg.minecraft.regioneditor.data.MapData;
import mirrg.minecraft.regioneditor.data.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.RegionInfo;

public class ImageLayerRegion
{

	private int width;
	private int height;

	private BufferedImage image;
	private Graphics2D graphics;

	public ImageLayerRegion()
	{
		resize(1, 1);
	}

	public void resize(int width, int height)
	{
		this.width = width;
		this.height = height;
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		graphics = image.createGraphics();
	}

	public BufferedImage getImage()
	{
		return image;
	}

	public void update(Image imageBackground, MapData mapData, int positionX, int positionZ)
	{
		graphics.drawImage(imageBackground, 0, 0, null);

		int chunkWidth = ((width / 2 - 1) / 16 + 1);
		int chunkHeight = ((height / 2 - 1) / 16 + 1);

		for (int xi = -chunkWidth; xi < chunkWidth; xi++) {
			for (int zi = -chunkHeight; zi < chunkHeight; zi++) {

				Optional<RegionIdentifier> oRegionIdentifier = mapData.regionMap.get(new ChunkPosition(positionX + xi, positionZ + zi));
				if (oRegionIdentifier.isPresent()) {
					RegionInfo regionInfo = mapData.regionInfoTable.get(oRegionIdentifier.get());

					// 背景半透明塗りつぶし
					graphics.setColor(new Color(
						regionInfo.getDynmapColor().getRed(),
						regionInfo.getDynmapColor().getGreen(),
						regionInfo.getDynmapColor().getBlue(),
						64));
					graphics.fillRect(xi * 16 + width / 2, zi * 16 + height / 2, 16, 16);

					// 領地輪郭線
					graphics.setColor(new Color(
						regionInfo.getDynmapColor().getRed(),
						regionInfo.getDynmapColor().getGreen(),
						regionInfo.getDynmapColor().getBlue()));
					{
						int w = 2;

						// left
						if (!mapData.regionMap.get(new ChunkPosition(positionX + xi - 1, positionZ + zi)).equals(oRegionIdentifier)) {
							graphics.fillRect(xi * 16 + 1 + 0 + width / 2, zi * 16 + 1 + 0 + height / 2, w, 16);
						}
						// right
						if (!mapData.regionMap.get(new ChunkPosition(positionX + xi + 1, positionZ + zi)).equals(oRegionIdentifier)) {
							graphics.fillRect(xi * 16 + (16 - w) + width / 2, zi * 16 + 1 + 0 + height / 2, w, 16);
						}
						// top
						if (!mapData.regionMap.get(new ChunkPosition(positionX + xi, positionZ + zi - 1)).equals(oRegionIdentifier)) {
							graphics.fillRect(xi * 16 + 1 + 0 + width / 2, zi * 16 + 1 + 0 + height / 2, 16, w);
						}
						// bottom
						if (!mapData.regionMap.get(new ChunkPosition(positionX + xi, positionZ + zi + 1)).equals(oRegionIdentifier)) {
							graphics.fillRect(xi * 16 + 1 + 0 + width / 2, zi * 16 + (16 - w) + height / 2, 16, w);
						}
					}

					// 数値
					FontRenderer.drawString(
						image,
						"" + oRegionIdentifier.get().countryNumber,
						xi * 16 + 8 + width / 2,
						zi * 16 + 2 + height / 2,
						regionInfo.countryColor);
					FontRenderer.drawString(
						image,
						"" + oRegionIdentifier.get().stateNumber,
						xi * 16 + 8 + width / 2,
						zi * 16 + 8 + height / 2,
						regionInfo.stateColor);

				}

				// グリッド
				graphics.setColor(new Color(0x444444));
				graphics.drawRect(xi * 16 + width / 2, zi * 16 + height / 2, 16, 16);

			}

		}

	}

}
