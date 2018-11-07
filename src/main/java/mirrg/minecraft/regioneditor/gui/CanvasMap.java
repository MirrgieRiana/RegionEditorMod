package mirrg.minecraft.regioneditor.gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CanvasMap extends Canvas
{

	private BufferedImage imageMap = null;

	private BufferedImage imageLayerMap = null;
	private Graphics2D graphicsLayerMap = null;

	private BufferedImage imageLayerOverlay = null;
	private Graphics2D graphicsLayerOverlay = null;

	private BufferedImage imageLayerBack = null;
	private Graphics2D graphicsLayerBack = null;

	private int positionX = 0;
	private int positionZ = 0;

	public CanvasMap()
	{
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e)
			{
				resizeLayer();
				updateLayerMap();
			}
		});

		setSize(1, 1);
		resizeLayer();
		updateLayerMap();
	}

	public void setMap(BufferedImage map)
	{
		this.imageMap = map;
		updateLayerMap();
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
		updateLayerMap();
	}

	//

	private void resizeLayer()
	{
		imageLayerMap = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		graphicsLayerMap = imageLayerMap.createGraphics();

		imageLayerOverlay = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		graphicsLayerOverlay = imageLayerOverlay.createGraphics();

		imageLayerBack = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		graphicsLayerBack = imageLayerBack.createGraphics();
	}

	private void updateLayerMap()
	{
		if (imageMap != null) graphicsLayerMap.drawImage(imageMap, 0 - positionX * 16, 0 - positionZ * 16, null);

		updateLayerOverlay();
	}

	private void updateLayerOverlay()
	{
		graphicsLayerOverlay.drawImage(imageLayerMap, 0, 0, null);

		int width = getWidth();
		int height = getHeight();

		int chunkWidth = (width - 1) / 16 + 1;
		int chunkHeight = (height - 1) / 16 + 1;

		for (int x = 0; x < chunkWidth; x++) {
			for (int z = 0; z < chunkHeight; z++) {
				Random random = new Random((positionX + x) * 1421435 + (positionZ + z) * 352412);

				// 背景半透明塗りつぶし
				graphicsLayerOverlay.setColor(new Color(0x44ff0000, true));
				graphicsLayerOverlay.fillRect(x * 16, z * 16, 16, 16);

				// 領地輪郭線
				graphicsLayerOverlay.setColor(new Color(0xff0000));
				graphicsLayerOverlay.drawLine(x * 16 + 1, z * 16 + 1, x * 16 + 15, z * 16 + 1);
				graphicsLayerOverlay.drawLine(x * 16 + 1, z * 16 + 1, x * 16 + 1, z * 16 + 15);
				graphicsLayerOverlay.drawLine(x * 16 + 1, z * 16 + 15, x * 16 + 15, z * 16 + 15);
				graphicsLayerOverlay.drawLine(x * 16 + 15, z * 16 + 1, x * 16 + 15, z * 16 + 15);

				// 数値
				FontRenderer.drawString(graphicsLayerOverlay, "" + random.nextInt(10) * random.nextInt(10) * random.nextInt(10) * random.nextInt(10), x * 16 + 8, z * 16 + 2);
				FontRenderer.drawString(graphicsLayerOverlay, "" + random.nextInt(10) * random.nextInt(10) * random.nextInt(10) * random.nextInt(10), x * 16 + 8, z * 16 + 8);

				// グリッド
				graphicsLayerOverlay.setColor(new Color(0x444444));
				graphicsLayerOverlay.drawRect(x * 16, z * 16, 16, 16);

			}

		}

		updateLayerBack();
	}

	private void updateLayerBack()
	{
		graphicsLayerBack.drawImage(imageLayerOverlay, 0, 0, null);

		repaint();
	}

	//

	@Override
	public void paint(Graphics g)
	{
		g.drawImage(imageLayerBack, 0, 0, null);
	}

}
