package mirrg.minecraft.regioneditor.gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import mirrg.boron.util.struct.Tuple;
import mirrg.minecraft.regioneditor.data.ChunkPosition;
import mirrg.minecraft.regioneditor.data.MapData;
import mirrg.minecraft.regioneditor.data.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.RegionInfo;
import mirrg.minecraft.regioneditor.data.RegionInfoTable;
import mirrg.minecraft.regioneditor.data.RegionMap;;

public class CanvasMap extends Canvas
{

	private ICanvasMapListener listener;

	private BufferedImage imageMap = null;
	private Point mapOrigin = null;

	private ImageLayerMap imageLayerMap = new ImageLayerMap();
	private ImageLayerRegion imageLayerRegion = new ImageLayerRegion();
	private ImageLayerMouse imageLayerMouse = new ImageLayerMouse();

	private int positionX = 0;
	private int positionZ = 0;

	private Optional<RegionIdentifier> oRegionIdentifierCurrent = Optional.empty();

	public MapData mapData = new MapData();

	// TODO
	public void init()
	{
		try {

			Random random = new Random();

			for (int i = 0; i < 5; i++) {
				addRegionInfo(
					new RegionIdentifier(
						"" + (random.nextInt(10) * random.nextInt(10) * random.nextInt(10) * random.nextInt(10)),
						"" + (random.nextInt(10) * random.nextInt(10) * random.nextInt(10) * random.nextInt(10))),
					new RegionInfo(
						"" + random.nextInt(10000),
						new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)),
						"" + random.nextInt(10000),
						new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256))));
			}
			addRegionInfo(
				RegionIdentifier.decode(new Gson().fromJson("[\"4432\",\"1\"]", JsonElement.class)),
				RegionInfo.decode(new Gson().fromJson("[\"レイミセロ国\",\"#FF0000\",\"首都\",\"#823413\"]", JsonElement.class)));
			addRegionInfo(
				RegionIdentifier.decode(new Gson().fromJson("[\"4432\",\"5673\"]", JsonElement.class)),
				RegionInfo.decode(new Gson().fromJson("[\"レイミセロ国\",\"#FF0000\",\"九州\",\"#198467\"]", JsonElement.class)));
			addRegionInfo(
				RegionIdentifier.decode(new Gson().fromJson("[\"17\",\"1\"]", JsonElement.class)),
				RegionInfo.decode(new Gson().fromJson("[\"宇宙航空研究開発機構\",\"#D89726\",\"金星探査機「あかつき28」墜落跡地\",\"#ff0000\"]", JsonElement.class)));

			/*
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
			*/

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void addRegionInfo(RegionIdentifier regionIdentifier, RegionInfo regionInfo)
	{
		mapData.regionInfoTable.put(regionIdentifier, regionInfo);
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
					if (!mapData.regionMap.get(chunkPosition).equals(oRegionIdentifierCurrent)) {
						mapData.regionMap.set(chunkPosition, oRegionIdentifierCurrent);
						updateLayerRegion(chunkPosition);
					}
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					if (!mapData.regionMap.get(chunkPosition).equals(Optional.empty())) {
						mapData.regionMap.set(chunkPosition, Optional.empty());
						updateLayerRegion(chunkPosition);
					}
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
					if (!mapData.regionMap.get(chunkPosition).equals(oRegionIdentifierCurrent)) {
						mapData.regionMap.set(chunkPosition, oRegionIdentifierCurrent);
						updateLayerRegion(chunkPosition);
					}
				} else if (mouseButtons[MouseEvent.BUTTON3]) {
					if (!mapData.regionMap.get(chunkPosition).equals(Optional.empty())) {
						mapData.regionMap.set(chunkPosition, Optional.empty());
						updateLayerRegion(chunkPosition);
					}
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

	public void setMapData(MapData mapData)
	{
		this.mapData = mapData;
		listener.onRegionInfoTableChange(this.mapData.regionInfoTable);
		updateLayerRegion();
	}

	public void setExpression(String string) throws Exception
	{
		setMapData(fromExpression(string));
	}

	public String getExpression() throws Exception
	{
		return toExpression(mapData);
	}

	private static MapData fromExpression(String string) throws Exception
	{
		MapData mapData = new MapData();

		{
			JsonObject json = fromJson(string).getAsJsonObject();

			{
				JsonArray infos = json.get("infos").getAsJsonArray();

				for (JsonElement info : infos) {
					if (info.isJsonArray()) {
						JsonArray entry = info.getAsJsonArray();
						mapData.regionInfoTable.put(
							RegionIdentifier.decode(entry.get(0)),
							RegionInfo.decode(entry.get(1)));
					}
				}

			}

			{
				JsonArray map = json.get("map").getAsJsonArray();

				// 結合
				StringBuilder sb = new StringBuilder();
				for (JsonElement line : map) {
					sb.append(line.getAsString());
				}
				String stringZipEncodeBase64 = sb.toString();

				// 解凍
				String regionMapExpression = decompress(stringZipEncodeBase64, "UTF-8");

				// 地図データに入れる
				setRegionMapExpression(mapData.regionMap, regionMapExpression);

			}

		}

		return mapData;
	}

	private static String toExpression(MapData mapData) throws Exception
	{
		Map<String, String> replaceTable = new HashMap<>();
		int replaceIndex = 0;

		JsonObject json = new JsonObject();
		{
			JsonArray infos = new JsonArray();

			for (Entry<RegionIdentifier, RegionInfo> entry : mapData.regionInfoTable.entrySet()) {

				// RegionEntryのJson表現の生成
				String string;
				{
					JsonArray array = new JsonArray();
					array.add(entry.getKey().encode());
					array.add(entry.getValue().encode());
					string = array.toString();
				}

				// 代替の文字列の配置
				// RegionEntry分のJsonを1行にするため
				replaceTable.put("\"=5A46d2Wsf=" + replaceIndex + "=5A46d2Wsf=\"", string);
				infos.add("=5A46d2Wsf=" + replaceIndex + "=5A46d2Wsf=");
				replaceIndex++;

			}

			json.add("infos", infos);
		}
		{
			JsonArray map = new JsonArray();

			// 地図データの文字列表現の取得
			String regionMapExpression = getRegionMapExpression(mapData.regionMap);

			// 圧縮
			List<String> list = compress(regionMapExpression, "UTF-8");

			// Jsonに1行ずつ追加
			for (String line : list) {
				map.add(line);
			}

			json.add("map", map);
		}

		String string = toJson(json);
		string = replace(string, replaceTable);
		return string;
	}

	private static String getRegionMapExpression(RegionMap regionMap)
	{
		StringBuilder sb = new StringBuilder();

		ChunkPosition chunkPositionLast = null;
		RegionIdentifier regionIdentifierLast = null;
		int length = 0;

		for (ChunkPosition chunkPosition : regionMap.getKeys()) {
			RegionIdentifier regionIdentifier = regionMap.get(chunkPosition).get();

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

	private static void setRegionMapExpression(RegionMap regionMap, String regionMapExpression)
	{

		// 空白文字無視
		regionMapExpression = regionMapExpression.replaceAll("[\\r\\n\\t ]", "");

		// 区切ってコマンド列にする
		String[] commands = regionMapExpression.isEmpty() ? new String[0] : regionMapExpression.split(";");

		// コマンド列の処理
		for (String command : commands) {

			// コマンド表現を引数ごとに区切る
			String[] s = command.split(",");

			// 引数をコマンドにする
			String countryNumber = s[0];
			String stateNumber = s[1];
			RegionIdentifier regionIdentifier = new RegionIdentifier(countryNumber, stateNumber);
			int x = Integer.parseInt(s[2], 10);
			int z = Integer.parseInt(s[3], 10);
			int length = Integer.parseInt(s[4], 10);

			// 配置実行
			for (int xi = 0; xi < length; xi++) {
				regionMap.set(new ChunkPosition(x + xi, z), Optional.of(regionIdentifier));
			}

		}

	}

	private static JsonElement fromJson(String string) throws Exception
	{
		JsonReader jsonReader = new JsonReader(new StringReader(string));
		jsonReader.setLenient(true);
		return Streams.parse(jsonReader);
	}

	private static String toJson(JsonElement json) throws Exception
	{
		StringWriter stringWriter = new StringWriter();
		JsonWriter jsonWriter = new JsonWriter(stringWriter);
		jsonWriter.setLenient(true);
		jsonWriter.setIndent(" ");
		Streams.write(json, jsonWriter);
		return stringWriter.toString();
	}

	private static String replace(String string, Map<String, String> map)
	{
		for (Entry<String, String> entry : map.entrySet()) {
			string = string.replaceAll(entry.getKey(), entry.getValue());
		}
		return string;
	}

	private static List<String> compress(String string, String charset) throws Exception
	{

		// バイト列化
		byte[] bytes = string.getBytes(charset);

		// Zip圧縮
		byte[] bytesZip = zip(bytes);

		// Base64エンコード
		String stringZipEncodeBase64 = encodeBase64(bytesZip);

		// 100文字ごとに切る
		ArrayList<String> list = slice(stringZipEncodeBase64, 100);

		return list;
	}

	private static String decompress(String string, String charset) throws Exception
	{

		// Base64デコード
		byte[] bytesDecodeBase64 = decodeBase64(string);

		// Zip解凍
		byte[] bytesDecodeBase64Unzip = unzip(bytesDecodeBase64);

		// 文字列化
		String stringDecodeBase64Unzip = new String(bytesDecodeBase64Unzip, charset);

		return stringDecodeBase64Unzip;
	}

	private static String encodeBase64(byte[] bytes) throws Exception
	{

		// Base64変換
		String stringEncodeBase64 = new String(Base64.getEncoder().encode(bytes), "utf-8");

		return stringEncodeBase64;
	}

	private static byte[] decodeBase64(String string) throws Exception
	{

		// 空白除去
		string = string.replaceAll("[\\r\\n\\t ]", "");

		// Base64逆変換
		byte[] bytesDecodeBase64 = Base64.getDecoder().decode(string);

		return bytesDecodeBase64;
	}

	private static byte[] zip(byte[] bytes) throws Exception
	{

		// Zip圧縮
		ArrayList<Tuple<Integer, byte[]>> bytesListZip = new ArrayList<>();
		{
			Deflater deflater = new Deflater();
			deflater.setInput(bytes);
			deflater.finish();
			while (true) {
				byte[] buffer = new byte[1024];
				int length = deflater.deflate(buffer);
				if (length > 0) {
					bytesListZip.add(new Tuple<>(length, buffer));
				} else {
					break;
				}
			}
			deflater.end();
		}

		// 結合
		byte[] bytesZip = concatenate(bytesListZip);

		return bytesZip;
	}

	private static byte[] unzip(byte[] bytes) throws Exception
	{

		// Zip解凍
		ArrayList<Tuple<Integer, byte[]>> bytesListUnzip = new ArrayList<>();
		{
			Inflater inflater = new Inflater();
			inflater.setInput(bytes, 0, bytes.length);
			while (true) {
				byte[] buffer = new byte[1024];
				int length = inflater.inflate(buffer);
				if (length > 0) {
					bytesListUnzip.add(new Tuple<>(length, buffer));
				} else {
					break;
				}
			}
			inflater.end();
		}

		// 結合
		byte[] bytesUnzip = concatenate(bytesListUnzip);

		return bytesUnzip;
	}

	private static ArrayList<String> slice(String string, int length)
	{
		ArrayList<String> lines = new ArrayList();
		for (int i = 0; i < string.length(); i += length) {
			lines.add(string.substring(i, Math.min(i + length, string.length())));
		}
		return lines;
	}

	private static byte[] concatenate(ArrayList<Tuple<Integer, byte[]>> bytesList)
	{
		byte[] bytes = new byte[bytesList.stream()
			.mapToInt(t -> t.x)
			.sum()];
		{
			int start = 0;
			for (Tuple<Integer, byte[]> buffer : bytesList) {
				System.arraycopy(buffer.y, 0, bytes, start, buffer.x);
				start += buffer.x;
			}
		}
		return bytes;
	}

	//

	private void resizeLayer()
	{
		imageLayerMap.resize(getWidth(), getHeight());
		imageLayerRegion.resize(getWidth(), getHeight());
		imageLayerMouse.resize(getWidth(), getHeight());
	}

	private void updateLayerMap()
	{
		imageLayerMap.update(imageMap, mapData, positionX, positionZ, mapOrigin);
		updateLayerRegion();
	}

	private void updateLayerRegion(ChunkPosition chunkPosition)
	{
		imageLayerRegion.update(imageLayerMap.getImage(), mapData, positionX, positionZ, chunkPosition.plus(-1, -1), chunkPosition.plus(1, 1));
		updateLayerBack();
	}

	private void updateLayerRegion()
	{
		imageLayerRegion.update(imageLayerMap.getImage(), mapData, positionX, positionZ);
		updateLayerBack();
	}

	private void updateLayerBack()
	{
		imageLayerMouse.update(imageLayerRegion.getImage(), mapData, oMousePosition, this::getChunkPosition);
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
		g.drawImage(imageLayerMouse.getImage(), 0, 0, null);
	}

}
