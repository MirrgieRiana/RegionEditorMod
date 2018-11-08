package mirrg.minecraft.regioneditor.gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;

public class CanvasMap extends Canvas
{

	private BufferedImage imageMap = null;
	private Point mapOrigin = null;

	private BufferedImage imageLayerMap = null;
	private Graphics2D graphicsLayerMap = null;

	private BufferedImage imageLayerOverlay = null;
	private Graphics2D graphicsLayerOverlay = null;

	private BufferedImage imageLayerBack = null;
	private Graphics2D graphicsLayerBack = null;

	private int positionX = 0;
	private int positionZ = 0;

	private Optional<RegionIdentifier> oRegionIdentifier = Optional.empty();

	private Map<RegionIdentifier, RegionInfo> regionInfoTable = new TreeMap<>();
	private RegionMap regionMap = new RegionMap();
	// TODO
	{
		try {

			Random random = new Random();

			for (int i = 0; i < 5; i++) {
				addRegionInfo(new RegionInfo(
					new RegionIdentifier(
						random.nextInt(10) * random.nextInt(10) * random.nextInt(10) * random.nextInt(10),
						random.nextInt(10) * random.nextInt(10) * random.nextInt(10) * random.nextInt(10)),
					"" + random.nextInt(10000),
					new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)),
					"" + random.nextInt(10000),
					new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256))));
			}
			addRegionInfo(RegionInfo.decode("4432:1,レイミセロ国:#FF0000:首都:#823413"));
			addRegionInfo(RegionInfo.decode("4432:5673,レイミセロ国:#FF0000:九州:#198467"));
			addRegionInfo(RegionInfo.decode("17:1,宇宙航空研究開発機構:#D89726:金星探査機「あかつき28」墜落跡地:#ff0000"));

			for (int i = 0; i < 10000; i++) {
				RegionIdentifier regionIdentifier;

				regionIdentifier = new ArrayList<>(regionInfoTable.keySet()).get(random.nextInt(regionInfoTable.size()));

				int x = random.nextInt(1000);
				int z = random.nextInt(1000);
				int s = random.nextInt(5);
				for (int xi = -s; xi <= s; xi++) {
					for (int zi = -s; zi <= s; zi++) {
						regionMap.set(new ChunkPosition(x + xi, z + zi), Optional.of(regionIdentifier));
					}
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void addRegionInfo(RegionInfo regionInfo)
	{
		regionInfoTable.put(regionInfo.regionIdentifier, regionInfo);
	}

	public RegionInfo getRegionInfo(RegionIdentifier regionIdentifier)
	{
		return regionInfoTable.get(regionIdentifier);
	}

	private Optional<Point> oMousePosition = Optional.empty();
	private boolean[] mouseButtons = new boolean[8];

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
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e)
			{
				oMousePosition = Optional.of(e.getPoint());
				mouseButtons[Math.min(e.getButton(), mouseButtons.length - 1)] = true;
				updateLayerBack();

				ChunkPosition chunkPosition = getChunkPosition(e.getPoint());
				if (e.getButton() == MouseEvent.BUTTON2) {
					oRegionIdentifier = regionMap.get(chunkPosition);
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					regionMap.set(chunkPosition, oRegionIdentifier);
					updateLayerOverlay();
				} else if (e.getButton() == MouseEvent.BUTTON1) {
					regionMap.set(chunkPosition, Optional.empty());
					updateLayerOverlay();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				oMousePosition = Optional.of(e.getPoint());
				mouseButtons[Math.min(e.getButton(), mouseButtons.length - 1)] = false;
				updateLayerBack();
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				oMousePosition = Optional.of(e.getPoint());
				updateLayerBack();
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				oMousePosition = Optional.empty();
				updateLayerBack();
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e)
			{
				oMousePosition = Optional.of(e.getPoint());
				updateLayerBack();
			}

			@Override
			public void mouseDragged(MouseEvent e)
			{
				oMousePosition = Optional.of(e.getPoint());
				updateLayerBack();

				ChunkPosition chunkPosition = getChunkPosition(e.getPoint());
				if (mouseButtons[MouseEvent.BUTTON3]) {
					regionMap.set(chunkPosition, oRegionIdentifier);
					updateLayerOverlay();
				} else if (mouseButtons[MouseEvent.BUTTON1]) {
					regionMap.set(chunkPosition, Optional.empty());
					updateLayerOverlay();
				}
			}
		});

		setSize(1, 1);
		resizeLayer();
		updateLayerMap();
	}

	public void setMap(BufferedImage imageMap, Point mapOrigin)
	{
		this.imageMap = imageMap;
		this.mapOrigin = mapOrigin;
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

	public void fromExpression(String string)
	{
		try {
			string = string.replaceAll("[\r\n\t ]", "");
			String[] commands = string.split(";");

			regionInfoTable.clear();
			regionMap.clear();

			for (String command : commands) {
				String kind = command.substring(0, 1);
				String args = command.substring(1);

				if (kind.equals("#")) {
					// Comment Out
				} else if (kind.equals("I")) {
					// Info
					RegionInfo regionInfo = RegionInfo.decode(args);
					regionInfoTable.put(regionInfo.regionIdentifier, regionInfo);
				} else if (kind.equals("M")) {
					// Map
					String[] s = args.split(",");
					int countryNumber = Integer.parseInt(s[0], 10);
					int stateNumber = Integer.parseInt(s[1], 10);
					int x = Integer.parseInt(s[2], 10);
					int z = Integer.parseInt(s[3], 10);
					int length = Integer.parseInt(s[4], 10);
					RegionIdentifier regionIdentifier = new RegionIdentifier(countryNumber, stateNumber);
					for (int xi = 0; xi < length; xi++) {
						regionMap.set(new ChunkPosition(x + xi, z), Optional.of(regionIdentifier));
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		updateLayerOverlay();
	}

	public String toExpression()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("#infos");
		sb.append(";\n");

		for (Entry<RegionIdentifier, RegionInfo> entry : regionInfoTable.entrySet()) {
			sb.append("I");
			sb.append(entry.getValue().encode());
			sb.append(";\n");
		}

		sb.append("#map");
		sb.append(";\n");

		// TODO 横並びの領地は1行にまとめる
		// TODO Zip Base64
		for (ChunkPosition chunkPosition : regionMap.getKeys()) {
			RegionIdentifier regionIdentifier = regionMap.get(chunkPosition).get();
			sb.append("M");
			sb.append(String.format("%s,%s,%s,%s,%s",
				regionIdentifier.countryNumber,
				regionIdentifier.stateNumber,
				chunkPosition.x,
				chunkPosition.z,
				1));
			sb.append(";\n");
		}

		return sb.toString();
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
		if (imageMap != null) graphicsLayerMap.drawImage(imageMap, 0 - positionX * 16 - mapOrigin.x, 0 - positionZ * 16 - mapOrigin.y, null);

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

				Optional<RegionIdentifier> oRegionIdentifier = regionMap.get(new ChunkPosition(positionX + x, positionZ + z));
				if (oRegionIdentifier.isPresent()) {
					RegionInfo regionInfo = getRegionInfo(oRegionIdentifier.get());

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
					{
						int w = 2;

						// left
						if (!regionMap.get(new ChunkPosition(positionX + x - 1, positionZ + z)).equals(oRegionIdentifier)) {
							graphicsLayerOverlay.fillRect(x * 16 + 1 + 0, z * 16 + 1 + 0, w, 16);
						}
						// right
						if (!regionMap.get(new ChunkPosition(positionX + x + 1, positionZ + z)).equals(oRegionIdentifier)) {
							graphicsLayerOverlay.fillRect(x * 16 + (16 - w), z * 16 + 1 + 0, w, 16);
						}
						// top
						if (!regionMap.get(new ChunkPosition(positionX + x, positionZ + z - 1)).equals(oRegionIdentifier)) {
							graphicsLayerOverlay.fillRect(x * 16 + 1 + 0, z * 16 + 1 + 0, 16, w);
						}
						// bottom
						if (!regionMap.get(new ChunkPosition(positionX + x, positionZ + z + 1)).equals(oRegionIdentifier)) {
							graphicsLayerOverlay.fillRect(x * 16 + 1 + 0, z * 16 + (16 - w), 16, w);
						}
					}

					// 数値
					FontRenderer.drawString(imageLayerOverlay, "" + regionInfo.regionIdentifier.countryNumber, x * 16 + 8, z * 16 + 2, regionInfo.countryColor);
					FontRenderer.drawString(imageLayerOverlay, "" + regionInfo.regionIdentifier.stateNumber, x * 16 + 8, z * 16 + 8, regionInfo.stateColor);

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

		if (oMousePosition.isPresent()) {
			Optional<RegionIdentifier> oRegionIdentifier = regionMap.get(getChunkPosition(oMousePosition.get()));
			if (oRegionIdentifier.isPresent()) {
				RegionInfo regionInfo = getRegionInfo(oRegionIdentifier.get());

				int height = graphicsLayerBack.getFontMetrics().getHeight();

				graphicsLayerBack.drawString(regionInfo.regionIdentifier.countryNumber + ":" + regionInfo.countryName, oMousePosition.get().x + 2, oMousePosition.get().y - height - 2);
				graphicsLayerBack.drawString(regionInfo.regionIdentifier.stateNumber + ":" + regionInfo.stateName, oMousePosition.get().x + 2, oMousePosition.get().y - 2);

			}
		}

		repaint();
	}

	private ChunkPosition getChunkPosition(Point point)
	{
		return new ChunkPosition(positionX + point.x / 16, positionZ + point.y / 16);
	}

	//

	@Override
	public void update(Graphics g)
	{
		paint(g);
	}

	@Override
	public void paint(Graphics g)
	{
		g.drawImage(imageLayerBack, 0, 0, null);
	}

}
