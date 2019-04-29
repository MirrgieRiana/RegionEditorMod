package mirrg.minecraft.regioneditor.gui;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import mirrg.boron.util.i18n.I18n;
import mirrg.boron.util.struct.Tuple;
import mirrg.minecraft.regioneditor.data.controller.LayerController;
import mirrg.minecraft.regioneditor.data.models.LayerModel;
import mirrg.minecraft.regioneditor.data.models.TileMapModel;
import mirrg.minecraft.regioneditor.data.objects.RegionEntry;
import mirrg.minecraft.regioneditor.data.objects.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.objects.RegionInfo;
import mirrg.minecraft.regioneditor.data.objects.TileCoordinate;
import mirrg.minecraft.regioneditor.data.objects.TileRectangle;
import mirrg.minecraft.regioneditor.gui.imagelayers.ImageLayerMap;
import mirrg.minecraft.regioneditor.gui.imagelayers.ImageLayerOverlay;
import mirrg.minecraft.regioneditor.gui.imagelayers.ImageLayerTile;
import mirrg.minecraft.regioneditor.gui.tool.ITool;
import mirrg.minecraft.regioneditor.gui.tool.IToolContext;
import mirrg.minecraft.regioneditor.util.gui.FontRenderer;

public class CanvasMap extends Canvas
{

	private final I18n i18n;
	private final ICanvasMapListener listener;
	private final ImageLayerMap imageLayerMap;
	private final ImageLayerTile imageLayerTile;
	private final ImageLayerOverlay imageLayerOverlay;

	private BufferedImage imageMap = null;
	private Point mapOrigin = null;

	private int positionX = 0;
	private int positionZ = 0;

	private Optional<ITool> oTool = Optional.empty();

	public void setTool(Optional<ITool> oTool)
	{
		if (this.oTool.isPresent()) this.oTool.get().off();
		this.oTool = oTool;
		if (this.oTool.isPresent()) this.oTool.get().on();
		updateLayerOverlay();
	}

	private IToolContext toolContext = new IToolContext() {
		@Override
		public Component getComponent()
		{
			return CanvasMap.this;
		}

		@Override
		public String localize(String unlocalizedString)
		{
			return i18n.localize(unlocalizedString);
		}

		@Override
		public LayerController getLayerController()
		{
			return CanvasMap.this.layerController;
		}

		@Override
		public int getWidth()
		{
			return CanvasMap.this.getWidth();
		}

		@Override
		public int getHeight()
		{
			return CanvasMap.this.getHeight();
		}

		@Override
		public TileCoordinate getTileCoordinate(Point point)
		{
			return new TileCoordinate(
				positionX + (int) Math.floor(((double) point.x - getWidth() / 2) / getTileSize()),
				positionZ + (int) Math.floor(((double) point.y - getHeight() / 2) / getTileSize()));
		}

		@Override
		public Point getTilePosition(TileCoordinate tileCoordinate)
		{
			return new Point(
				(tileCoordinate.x - positionX) * getTileSize() + getWidth() / 2,
				(tileCoordinate.z - positionZ) * getTileSize() + getHeight() / 2);
		}

		@Override
		public int getTileSize()
		{
			return 16;
		}

		@Override
		public TileRectangle getVisibleArea()
		{
			return imageLayerTile.getVisibleArea(positionX, positionZ);
		}

		@Override
		public boolean isVisible(TileCoordinate tileCoordinate)
		{
			return imageLayerTile.isVisible(positionX, positionZ, tileCoordinate);
		}

		@Override
		public Optional<RegionIdentifier> getTileCurrent()
		{
			return CanvasMap.this.tileCurrent;
		}

		@Override
		public void setTileCurrent(Optional<RegionIdentifier> tileCurrent)
		{
			CanvasMap.this.setTileCurrent(tileCurrent);
		}

		@Override
		public int getBrushSize()
		{
			return CanvasMap.this.brushSize;
		}

		@Override
		public void repaintTile()
		{
			CanvasMap.this.updateLayerTile();
		}

		@Override
		public void repaintTile(TileCoordinate tileCoordinate)
		{
			CanvasMap.this.updateLayerTile(tileCoordinate);
		}

		@Override
		public void repaintOverlay()
		{
			CanvasMap.this.updateLayerOverlay();
		}
	};

	public IToolContext getToolContext()
	{
		return toolContext;
	}

	private Optional<RegionIdentifier> tileCurrent = Optional.empty();
	private int brushSize = 1;

	public final LayerController layerController = new LayerController(new LayerModel());

	public void init()
	{
		try {
			addRegionInfo(
				RegionIdentifier.decode(new Gson().fromJson("[\"DUMY\",\"1234\"]", JsonElement.class)),
				RegionInfo.decode(new Gson().fromJson("[\"" +
					i18n.localize("CanvasMap.defaultCountry") + "\",\"#FF0000\",\"" +
					i18n.localize("CanvasMap.defaultState") + "\",\"#823413\"]", JsonElement.class)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void addRegionInfo(RegionIdentifier regionIdentifier, RegionInfo regionInfo)
	{
		layerController.regionTableController.model.set(regionIdentifier, regionInfo);
		layerController.regionTableController.epChangedState.trigger().run();
	}

	public CanvasMap(FontRenderer fontRenderer, I18n i18n, ICanvasMapListener listener)
	{
		this.i18n = i18n;
		this.listener = listener;
		this.imageLayerMap = new ImageLayerMap();
		this.imageLayerTile = new ImageLayerTile(fontRenderer);
		this.imageLayerOverlay = new ImageLayerOverlay();

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e)
			{
				resizeLayer();
				updateLayerMap();
			}
		});
		layerController.regionTableController.epChangedState.register(() -> {
			updateLayerTile();
		});
		layerController.tileMapController.epChangedTileSpecified.register(tileCoordinate -> {
			updateLayerTile(tileCoordinate);
		});
		layerController.tileMapController.epChangedTileUnspecified.register(() -> {
			updateLayerTile();
		});

		setSize(1, 1);
		resizeLayer();
		updateLayerMap();
	}

	public Optional<RegionIdentifier> getTileCurrent()
	{
		return tileCurrent;
	}

	public void setTileCurrent(Optional<RegionIdentifier> tileCurrent)
	{
		this.tileCurrent = tileCurrent;
		listener.onChangeTileCurrent(tileCurrent);
		updateLayerOverlay();
	}

	public int getBrushSize()
	{
		return brushSize;
	}

	public void setBrushSize(int brushSize)
	{
		this.brushSize = brushSize;
		listener.onBrushSizeChange(brushSize);
		updateLayerOverlay();
	}

	public static interface ICanvasMapListener
	{

		public default void onChangeTileCurrent(Optional<RegionIdentifier> tile)
		{

		}

		public default void onBrushSizeChange(int brushSize)
		{

		}

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

	public void setPossessionMap(LayerModel layerModel)
	{
		layerController.setModel(layerModel);
		updateLayerTile();
	}

	public void setExpression(String string) throws Exception
	{
		setPossessionMap(fromExpression(string));
	}

	public String getExpression() throws Exception
	{
		return toExpression(layerController.model);
	}

	private static LayerModel fromExpression(String string) throws Exception
	{
		LayerModel layerModel = new LayerModel();

		{
			JsonObject json = fromJson(string).getAsJsonObject();

			{
				JsonArray infos = json.get("infos").getAsJsonArray();

				for (JsonElement info : infos) {
					if (info.isJsonArray()) {
						JsonArray entry = info.getAsJsonArray();
						layerModel.regionTableModel.set(
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
				String tileMapExpression = decompress(stringZipEncodeBase64, "UTF-8");

				// 地図データに入れる
				setTileMapExpression(layerModel.tileMapModel, tileMapExpression);

			}

		}

		return layerModel;
	}

	private static String toExpression(LayerModel layerModel) throws Exception
	{
		Map<String, String> replaceTable = new HashMap<>();
		int replaceIndex = 0;

		JsonObject json = new JsonObject();
		{
			JsonArray infos = new JsonArray();

			for (RegionEntry entry : layerModel.regionTableModel.getEntries()) {

				// RegionEntryのJson表現の生成
				String string;
				{
					JsonArray array = new JsonArray();
					array.add(entry.regionIdentifier.encode());
					array.add(entry.regionInfo.encode());
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
			String tileMapExpression = getTileMapExpression(layerModel.tileMapModel);

			// 圧縮
			List<String> list = compress(tileMapExpression, "UTF-8");

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

	private static String getTileMapExpression(TileMapModel tileMapModel)
	{
		StringBuilder sb = new StringBuilder();

		TileCoordinate tileCoordinateLast = null;
		RegionIdentifier regionIdentifierLast = null;
		int length = 0;

		for (TileCoordinate tileCoordinate : tileMapModel.getKeys()) {
			RegionIdentifier regionIdentifier = tileMapModel.getTile(tileCoordinate).get();

			if (tileCoordinateLast != null) {
				// 1個前の領地がある場合

				if (tileCoordinateLast.x + 1 == tileCoordinate.x
					&& tileCoordinateLast.z == tileCoordinate.z
					&& regionIdentifierLast.equals(regionIdentifier)) {
					// 1個前の領地のすぐ右で同じ領地情報の場合

					// この領地を飛ばす
					tileCoordinateLast = tileCoordinate;
					length++;

				} else {
					// そうでない場合

					// 前の領地を出力する
					sb.append(String.format("%s,%s,%s,%s,%s",
						regionIdentifierLast.countryId,
						regionIdentifierLast.stateId,
						tileCoordinateLast.x - length + 1,
						tileCoordinateLast.z,
						length));
					sb.append(";\n");

					// この領地を飛ばす
					tileCoordinateLast = tileCoordinate;
					regionIdentifierLast = regionIdentifier;
					length = 1;

				}

			} else {
				// 1個前の領地がない場合

				// この領地を飛ばす
				tileCoordinateLast = tileCoordinate;
				regionIdentifierLast = regionIdentifier;
				length = 1;

			}

		}

		if (tileCoordinateLast != null) {
			// 1個前の領地がある場合

			// 前の領地を出力する
			sb.append(String.format("%s,%s,%s,%s,%s",
				regionIdentifierLast.countryId,
				regionIdentifierLast.stateId,
				tileCoordinateLast.x - length + 1,
				tileCoordinateLast.z,
				length));
			sb.append(";\n");

		}

		return sb.toString();
	}

	private static void setTileMapExpression(TileMapModel tileMap, String tileMapExpression)
	{

		// 空白文字無視
		tileMapExpression = tileMapExpression.replaceAll("[\\r\\n\\t ]", "");

		// 区切ってコマンド列にする
		String[] commands = tileMapExpression.isEmpty() ? new String[0] : tileMapExpression.split(";");

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
				tileMap.setTile(new TileCoordinate(x + xi, z), Optional.of(regionIdentifier));
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
		ArrayList<String> lines = new ArrayList<>();
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

	public void update()
	{
		updateLayerMap();
	}

	private void resizeLayer()
	{
		imageLayerMap.resize(getWidth(), getHeight());
		imageLayerTile.resize(getWidth(), getHeight());
		imageLayerOverlay.resize(getWidth(), getHeight());
	}

	private void updateLayerMap()
	{
		imageLayerMap.update(imageMap, layerController, positionX, positionZ, mapOrigin);
		updateLayerTile();
	}

	public void setShowMap(boolean showMap)
	{
		imageLayerMap.showMap = showMap;
		updateLayerMap();
	}

	private void updateLayerTile(TileCoordinate tileCoordinate)
	{
		imageLayerTile.update(imageLayerMap.getImage(), layerController, positionX, positionZ, new TileRectangle(tileCoordinate.plus(-1, -1), tileCoordinate.plus(1, 1)));
		updateLayerOverlay();
	}

	private void updateLayerTile()
	{
		imageLayerTile.update(imageLayerMap.getImage(), layerController, positionX, positionZ);
		updateLayerOverlay();
	}

	public void setShowTile(boolean showTile)
	{
		imageLayerTile.showTile = showTile;
		updateLayerTile();
	}

	public void setShowArea(boolean showArea)
	{
		imageLayerTile.showArea = showArea;
		updateLayerTile();
	}

	public void setShowBorder(boolean showBorder)
	{
		imageLayerTile.showBorder = showBorder;
		updateLayerTile();
	}

	public void setShowIdentifier(boolean showIdentifier)
	{
		imageLayerTile.showIdentifier = showIdentifier;
		updateLayerTile();
	}

	public void setShowGrid(boolean showGrid)
	{
		imageLayerTile.showGrid = showGrid;
		updateLayerTile();
	}

	private void updateLayerOverlay()
	{
		imageLayerOverlay.update(imageLayerTile.getImage(), layerController, oTool);
		repaint();
	}

	public void setShowTooltip(boolean showTooltip)
	{
		imageLayerOverlay.showTooltip = showTooltip;
		updateLayerOverlay();
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
		g.drawImage(imageLayerOverlay.getImage(), 0, 0, null);
	}

}
