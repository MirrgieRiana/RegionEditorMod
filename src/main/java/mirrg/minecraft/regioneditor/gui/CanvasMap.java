package mirrg.minecraft.regioneditor.gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.text.ParseException;
import java.util.Optional;
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

	private RegionMap regionMap = new RegionMap();
	// TODO
	{
		try {

			Random random = new Random();
			for (int i = 0; i < 10000; i++) {
				RegionInfo regionInfo;

				if (random.nextInt(10) == 0) {
					regionInfo = RegionInfo.decode("4432,#FF0000,レイミセロ国,1,#823413,首都");

				} else if (random.nextInt(10) == 0) {
					regionInfo = RegionInfo.decode("4432,#FF0000,レイミセロ国,5673,#198467,九州");
				} else if (random.nextInt(10) == 0) {
					regionInfo = RegionInfo.decode("17,#D89726,宇宙航空研究開発機構,1,#ff0000,金星探査機「あかつき28」墜落跡地");
				} else {
					regionInfo = new RegionInfo(
						random.nextInt(10) * random.nextInt(10) * random.nextInt(10) * random.nextInt(10),
						new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)),
						"" + random.nextInt(10000),
						random.nextInt(10) * random.nextInt(10) * random.nextInt(10) * random.nextInt(10),
						new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)),
						"" + random.nextInt(10000));
				}

				regionMap.setRegionInfo(new ChunkPosition(random.nextInt(1000), random.nextInt(1000)),
					regionInfo);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

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
		graphicsLayerMap.setBackground(new Color(128, 128, 128));
		graphicsLayerMap.clearRect(0, 0, getWidth(), getHeight());
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

				Optional<RegionInfo> oRegionInfo = regionMap.getRegionInfo(new ChunkPosition(positionX + x, positionZ + z));
				if (oRegionInfo.isPresent()) {
					RegionInfo regionInfo = oRegionInfo.get();

					// 背景半透明塗りつぶし
					graphicsLayerOverlay.setColor(new Color(
						regionInfo.getDynmapColor().getRed(),
						regionInfo.getDynmapColor().getGreen(),
						regionInfo.getDynmapColor().getBlue(),
						64));
					graphicsLayerOverlay.fillRect(x * 16, z * 16, 16, 16);

					// 領地輪郭線
					graphicsLayerOverlay.setColor(new Color(
						regionInfo.getDynmapColor().getRed(),
						regionInfo.getDynmapColor().getGreen(),
						regionInfo.getDynmapColor().getBlue()));
					graphicsLayerOverlay.drawLine(x * 16 + 1, z * 16 + 1, x * 16 + 15, z * 16 + 1);
					graphicsLayerOverlay.drawLine(x * 16 + 1, z * 16 + 1, x * 16 + 1, z * 16 + 15);
					graphicsLayerOverlay.drawLine(x * 16 + 1, z * 16 + 15, x * 16 + 15, z * 16 + 15);
					graphicsLayerOverlay.drawLine(x * 16 + 15, z * 16 + 1, x * 16 + 15, z * 16 + 15);

					// 数値
					FontRenderer.drawString(imageLayerOverlay, "" + regionInfo.countryNumber, x * 16 + 8, z * 16 + 2, regionInfo.countryColor);
					FontRenderer.drawString(imageLayerOverlay, "" + regionInfo.stateNumber, x * 16 + 8, z * 16 + 8, regionInfo.stateColor);

				}

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
