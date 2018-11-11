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
import java.util.Base64;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;
import mirrg.minecraft.regioneditor.data.ChunkPosition;
import mirrg.minecraft.regioneditor.data.MapData;
import mirrg.minecraft.regioneditor.data.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.RegionInfo;
import mirrg.minecraft.regioneditor.data.RegionInfoTable;
import net.minecraft.util.Tuple;

public class CanvasMap extends Canvas
{

	private ICanvasMapListener listener;

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

	private Optional<RegionIdentifier> oRegionIdentifierCurrent = Optional.empty();

	private MapData mapData = new MapData();

	// TODO
	public void init()
	{
		try {

			Random random = new Random();

			for (int i = 0; i < 5; i++) {
				addRegionInfo(new RegionInfo(
					new RegionIdentifier(
						"" + (random.nextInt(10) * random.nextInt(10) * random.nextInt(10) * random.nextInt(10)),
						"" + (random.nextInt(10) * random.nextInt(10) * random.nextInt(10) * random.nextInt(10))),
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

				regionIdentifier = new ArrayList<>(mapData.regionInfoTable.keySet()).get(random.nextInt(mapData.regionInfoTable.size()));

				int x = random.nextInt(1000);
				int z = random.nextInt(1000);
				int s = random.nextInt(5);
				for (int xi = -s; xi <= s; xi++) {
					for (int zi = -s; zi <= s; zi++) {
						mapData.regionMap.set(new ChunkPosition(x + xi, z + zi), Optional.of(regionIdentifier));
					}
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void addRegionInfo(RegionInfo regionInfo)
	{
		mapData.regionInfoTable.put(regionInfo.regionIdentifier, regionInfo);
		listener.onRegionInfoTableChange(mapData.regionInfoTable);
	}

	private Optional<Point> oMousePosition = Optional.empty();
	private boolean[] mouseButtons = new boolean[8];

	public CanvasMap(ICanvasMapListener listener)
	{
		this.listener = listener;

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
					setRegionIdentifierCurrent(mapData.regionMap.get(chunkPosition));
				} else if (e.getButton() == MouseEvent.BUTTON1) {
					mapData.regionMap.set(chunkPosition, oRegionIdentifierCurrent);
					updateLayerOverlay();
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					mapData.regionMap.set(chunkPosition, Optional.empty());
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
				if (mouseButtons[MouseEvent.BUTTON1]) {
					mapData.regionMap.set(chunkPosition, oRegionIdentifierCurrent);
					updateLayerOverlay();
				} else if (mouseButtons[MouseEvent.BUTTON3]) {
					mapData.regionMap.set(chunkPosition, Optional.empty());
					updateLayerOverlay();
				}
			}
		});

		setSize(1, 1);
		resizeLayer();
		updateLayerMap();
	}

	public void setRegionIdentifierCurrent(Optional<RegionIdentifier> oRegionIdentifierCurrent)
	{
		this.oRegionIdentifierCurrent = oRegionIdentifierCurrent;
		listener.onRegionIdentifierCurrentChange(oRegionIdentifierCurrent);
		updateLayerBack();
	}

	public static interface ICanvasMapListener
	{

		public void onRegionInfoTableChange(RegionInfoTable regionInfoTable);

		public void onRegionIdentifierCurrentChange(Optional<RegionIdentifier> oRegionIdentifierCurrent);

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
		mapData.regionInfoTable.clear();
		mapData.regionMap.clear();

		Consumer<String>[] commandConsumer = new Consumer[1];
		commandConsumer[0] = string2 -> {
			try {
				string2 = string2.replaceAll("[\\r\\n\\t ]", "");
				String[] commands = string2.split(";");

				for (String command : commands) {
					String kind = command.substring(0, 1);
					String args = command.substring(1);

					if (kind.equals("#")) {
						// Comment Out
					} else if (kind.equals("I")) {
						// Info
						RegionInfo regionInfo = RegionInfo.decode(args);
						mapData.regionInfoTable.put(regionInfo.regionIdentifier, regionInfo);
					} else if (kind.equals("M")) {
						// Map
						String[] s = args.split(",");
						String countryNumber = s[0];
						String stateNumber = s[1];
						int x = Integer.parseInt(s[2], 10);
						int z = Integer.parseInt(s[3], 10);
						int length = Integer.parseInt(s[4], 10);
						RegionIdentifier regionIdentifier = new RegionIdentifier(countryNumber, stateNumber);
						for (int xi = 0; xi < length; xi++) {
							mapData.regionMap.set(new ChunkPosition(x + xi, z), Optional.of(regionIdentifier));
						}
					} else if (kind.equals("C")) {
						commandConsumer[0].accept(decompress(args));
					}

				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
		commandConsumer[0].accept(string);

		listener.onRegionInfoTableChange(mapData.regionInfoTable);

		updateLayerOverlay();
	}

	public String toExpression()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("#infos");
		sb.append(";\n");

		for (Entry<RegionIdentifier, RegionInfo> entry : mapData.regionInfoTable.entrySet()) {
			sb.append("I");
			sb.append(entry.getValue().encode());
			sb.append(";\n");
		}

		sb.append("#map");
		sb.append(";\n");

		{
			sb.append("C");
			sb.append("\n");
			try {
				sb.append(compress(getMapExpression()));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			sb.append(";\n");
		}

		return sb.toString();
	}

	private String getMapExpression()
	{
		StringBuilder sb = new StringBuilder();

		ChunkPosition chunkPositionLast = null;
		RegionIdentifier regionIdentifierLast = null;
		int length = 0;

		for (ChunkPosition chunkPosition : mapData.regionMap.getKeys()) {
			RegionIdentifier regionIdentifier = mapData.regionMap.get(chunkPosition).get();

			if (chunkPositionLast != null) {
				// 1個前の領地がある場合

				if (chunkPositionLast.x + 1 == chunkPosition.x
					&& chunkPositionLast.z == chunkPosition.z
					&& regionIdentifierLast.equals(regionIdentifier)) {
					// 1個前の領地のすぐ右で同じ領地情報の場合

					// この領地を飛ばす
					chunkPositionLast = chunkPosition;
					length++;

				} else {
					// そうでない場合

					// 前の領地を出力する
					sb.append("M");
					sb.append(String.format("%s,%s,%s,%s,%s",
						regionIdentifierLast.countryNumber,
						regionIdentifierLast.stateNumber,
						chunkPositionLast.x - length + 1,
						chunkPositionLast.z,
						length));
					sb.append(";\n");

					// この領地を飛ばす
					chunkPositionLast = chunkPosition;
					regionIdentifierLast = regionIdentifier;
					length = 1;

				}

			} else {
				// 1個前の領地がない場合

				// この領地を飛ばす
				chunkPositionLast = chunkPosition;
				regionIdentifierLast = regionIdentifier;
				length = 1;

			}

		}

		if (chunkPositionLast != null) {
			// 1個前の領地がある場合

			// 前の領地を出力する
			sb.append("M");
			sb.append(String.format("%s,%s,%s,%s,%s",
				regionIdentifierLast.countryNumber,
				regionIdentifierLast.stateNumber,
				chunkPositionLast.x - length + 1,
				chunkPositionLast.z,
				length));
			sb.append(";\n");

		}

		return sb.toString();
	}

	private String compress(String string) throws Exception
	{
		Deflater deflater = new Deflater();
		deflater.setInput(string.getBytes("utf-8"));
		deflater.finish();
		ArrayList<Tuple<Integer, byte[]>> buffers = new ArrayList<>();
		while (true) {
			byte[] buffer = new byte[1024];
			int length = deflater.deflate(buffer);
			if (length > 0) {
				buffers.add(new Tuple<>(length, buffer));
			} else {
				break;
			}
		}
		deflater.end();

		int length = buffers.stream()
			.mapToInt(t -> t.getFirst())
			.sum();
		byte[] buffer2 = new byte[length];
		{
			int start = 0;
			for (Tuple<Integer, byte[]> buffer : buffers) {
				System.arraycopy(buffer.getSecond(), 0, buffer2, start, buffer.getFirst());
				start += buffer.getFirst();
			}
		}

		String out = new String(Base64.getEncoder().encode(buffer2), "utf-8");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < out.length(); i += 100) {
			sb.append(out.substring(i, Math.min(i + 100, out.length())));
			sb.append("\n");
		}
		return sb.toString();
	}

	private String decompress(String string) throws Exception
	{
		byte[] buffer1 = Base64.getDecoder().decode(string.replaceAll("[\\r\\n\\t ]", ""));

		Inflater inflater = new Inflater();
		inflater.setInput(buffer1, 0, buffer1.length);
		ArrayList<Tuple<Integer, byte[]>> buffers = new ArrayList<>();
		while (true) {
			byte[] buffer = new byte[1024];
			int length = inflater.inflate(buffer);
			if (length > 0) {
				buffers.add(new Tuple<>(length, buffer));
			} else {
				break;
			}
		}
		inflater.end();

		int length = buffers.stream()
			.mapToInt(t -> t.getFirst())
			.sum();
		byte[] buffer2 = new byte[length];
		{
			int start = 0;
			for (Tuple<Integer, byte[]> buffer : buffers) {
				System.arraycopy(buffer.getSecond(), 0, buffer2, start, buffer.getFirst());
				start += buffer.getFirst();
			}
		}

		return new String(buffer2, "utf-8");
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
		if (imageMap != null) graphicsLayerMap.drawImage(
			imageMap,
			0 - positionX * 16 - mapOrigin.x + getWidth() / 2,
			0 - positionZ * 16 - mapOrigin.y + getHeight() / 2,
			null);

		updateLayerOverlay();
	}

	private void updateLayerOverlay()
	{
		graphicsLayerOverlay.drawImage(imageLayerMap, 0, 0, null);

		int width = getWidth();
		int height = getHeight();

		int chunkWidth = ((width / 2 - 1) / 16 + 1);
		int chunkHeight = ((height / 2 - 1) / 16 + 1);

		for (int xi = -chunkWidth; xi < chunkWidth; xi++) {
			for (int zi = -chunkHeight; zi < chunkHeight; zi++) {

				Optional<RegionIdentifier> oRegionIdentifier = mapData.regionMap.get(new ChunkPosition(positionX + xi, positionZ + zi));
				if (oRegionIdentifier.isPresent()) {
					RegionInfo regionInfo = mapData.regionInfoTable.get(oRegionIdentifier.get());

					// 背景半透明塗りつぶし
					graphicsLayerOverlay.setColor(new Color(
						regionInfo.getDynmapColor().getRed(),
						regionInfo.getDynmapColor().getGreen(),
						regionInfo.getDynmapColor().getBlue(),
						64));
					graphicsLayerOverlay.fillRect(xi * 16 + getWidth() / 2, zi * 16 + getHeight() / 2, 16, 16);

					// 領地輪郭線
					graphicsLayerOverlay.setColor(new Color(
						regionInfo.getDynmapColor().getRed(),
						regionInfo.getDynmapColor().getGreen(),
						regionInfo.getDynmapColor().getBlue()));
					{
						int w = 2;

						// left
						if (!mapData.regionMap.get(new ChunkPosition(positionX + xi - 1, positionZ + zi)).equals(oRegionIdentifier)) {
							graphicsLayerOverlay.fillRect(xi * 16 + 1 + 0 + getWidth() / 2, zi * 16 + 1 + 0 + getHeight() / 2, w, 16);
						}
						// right
						if (!mapData.regionMap.get(new ChunkPosition(positionX + xi + 1, positionZ + zi)).equals(oRegionIdentifier)) {
							graphicsLayerOverlay.fillRect(xi * 16 + (16 - w) + getWidth() / 2, zi * 16 + 1 + 0 + getHeight() / 2, w, 16);
						}
						// top
						if (!mapData.regionMap.get(new ChunkPosition(positionX + xi, positionZ + zi - 1)).equals(oRegionIdentifier)) {
							graphicsLayerOverlay.fillRect(xi * 16 + 1 + 0 + getWidth() / 2, zi * 16 + 1 + 0 + getHeight() / 2, 16, w);
						}
						// bottom
						if (!mapData.regionMap.get(new ChunkPosition(positionX + xi, positionZ + zi + 1)).equals(oRegionIdentifier)) {
							graphicsLayerOverlay.fillRect(xi * 16 + 1 + 0 + getWidth() / 2, zi * 16 + (16 - w) + getHeight() / 2, 16, w);
						}
					}

					// 数値
					FontRenderer.drawString(
						imageLayerOverlay,
						"" + oRegionIdentifier.get().countryNumber,
						xi * 16 + 8 + getWidth() / 2,
						zi * 16 + 2 + getHeight() / 2,
						regionInfo.countryColor);
					FontRenderer.drawString(
						imageLayerOverlay,
						"" + oRegionIdentifier.get().stateNumber,
						xi * 16 + 8 + getWidth() / 2,
						zi * 16 + 8 + getHeight() / 2,
						regionInfo.stateColor);

				}

				// グリッド
				graphicsLayerOverlay.setColor(new Color(0x444444));
				graphicsLayerOverlay.drawRect(xi * 16 + getWidth() / 2, zi * 16 + getHeight() / 2, 16, 16);

			}

		}

		updateLayerBack();
	}

	private void updateLayerBack()
	{
		graphicsLayerBack.drawImage(imageLayerOverlay, 0, 0, null);

		if (oMousePosition.isPresent()) {

			int height = graphicsLayerBack.getFontMetrics().getHeight();

			ChunkPosition chunkPosition = getChunkPosition(oMousePosition.get());

			graphicsLayerBack.drawString(
				chunkPosition.x + ", " + chunkPosition.z,
				oMousePosition.get().x + 2,
				oMousePosition.get().y - height * 2 - 2);

			Optional<RegionIdentifier> oRegionIdentifier = mapData.regionMap.get(chunkPosition);
			if (oRegionIdentifier.isPresent()) {
				RegionInfo regionInfo = mapData.regionInfoTable.get(oRegionIdentifier.get());

				graphicsLayerBack.drawString(
					"Country: (" + regionInfo.regionIdentifier.countryNumber + ") " + regionInfo.countryName,
					oMousePosition.get().x + 2,
					oMousePosition.get().y - height * 1 - 2);
				graphicsLayerBack.drawString(
					"State: (" + regionInfo.regionIdentifier.stateNumber + ") " + regionInfo.stateName,
					oMousePosition.get().x + 2,
					oMousePosition.get().y - height * 0 - 2);

			}

		}

		repaint();
	}

	private ChunkPosition getChunkPosition(Point point)
	{
		return new ChunkPosition(
			positionX + (int) Math.floor(((double) point.x - getWidth() / 2) / 16),
			positionZ + (int) Math.floor(((double) point.y - getHeight() / 2) / 16));
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
