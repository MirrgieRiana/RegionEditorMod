package mirrg.minecraft.regioneditor.gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CanvasMap extends Canvas
{

	private BufferedImage map = null;
	private int positionX = 0;
	private int positionZ = 0;

	public void setMap(BufferedImage map)
	{
		this.map = map;
		repaint();
	}

	@Override
	public void paint(Graphics g)
	{
		if (map != null) g.drawImage(map, 0 - positionX * 16, 0 - positionZ * 16, null);

		int width = getWidth();
		int height = getHeight();

		int chunkWidth = (width - 1) / 16 + 1;
		int chunkHeight = (height - 1) / 16 + 1;

		for (int x = 0; x < chunkWidth; x++) {
			for (int z = 0; z < chunkHeight; z++) {
				Random random = new Random((positionX + x) * 1421435 + (positionZ + z) * 352412);

				// 背景半透明塗りつぶし
				g.setColor(new Color(0x44ff0000, true));
				g.fillRect(x * 16, z * 16, 16, 16);

				// 領地輪郭線
				g.setColor(new Color(0xff0000));
				g.drawLine(x * 16 + 1, z * 16 + 1, x * 16 + 15, z * 16 + 1);
				g.drawLine(x * 16 + 1, z * 16 + 1, x * 16 + 1, z * 16 + 15);
				g.drawLine(x * 16 + 1, z * 16 + 15, x * 16 + 15, z * 16 + 15);
				g.drawLine(x * 16 + 15, z * 16 + 1, x * 16 + 15, z * 16 + 15);

				// 数値
				FontRenderer.drawString(g, "" + random.nextInt(10) * random.nextInt(10) * random.nextInt(10) * random.nextInt(10), x * 16 + 8, z * 16 + 2);
				FontRenderer.drawString(g, "" + random.nextInt(10) * random.nextInt(10) * random.nextInt(10) * random.nextInt(10), x * 16 + 8, z * 16 + 8);

				// グリッド
				g.setColor(new Color(0x444444));
				g.drawRect(x * 16, z * 16, 16, 16);

			}

		}

	}

	public int getPositionX()
	{
		return positionX;
	}

	public int getPositionZ()
	{
		return positionZ;
	}

	public void setPosition(int x, int z)
	{
		this.positionX = x;
		this.positionZ = z;
		repaint();
	}

}
