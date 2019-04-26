package mirrg.minecraft.regioneditor.gui.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import mirrg.minecraft.regioneditor.data.objects.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.objects.TileCoordinate;
import mirrg.minecraft.regioneditor.gui.tool.IToolContext;

public class ToolLine extends ToolBase
{

	public ToolLine(IToolContext toolContext)
	{
		super(toolContext);
	}

	private Optional<TileCoordinate> oStart = Optional.empty();
	private Optional<TileCoordinate> oEnd = Optional.empty();

	private MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e)
		{
			Point point = e.getPoint();

			// 左ダウンで押下状態にする
			if (e.getButton() == MouseEvent.BUTTON1) {
				oStart = Optional.of(toolContext.getTileCoordinate(point));
				oEnd = Optional.of(shiftFilter(toolContext.getTileCoordinate(point)));
			}

			// 中央クリックでスポイト
			if (e.getButton() == MouseEvent.BUTTON2) {
				toolContext.setTileCurrent(toolContext.getLayerController().tileMapController.model.getTile(toolContext.getTileCoordinate(point)));
			}

			// 右ダウンで押下状態にする
			if (e.getButton() == MouseEvent.BUTTON3) {
				oStart = Optional.of(toolContext.getTileCoordinate(point));
				oEnd = Optional.of(shiftFilter(toolContext.getTileCoordinate(point)));
			}

			toolContext.repaintOverlay();
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			Point point = e.getPoint();

			// 左アップで配置して押下状態解除
			if (e.getButton() == MouseEvent.BUTTON1) {

				// 範囲の終端を更新
				oEnd = Optional.of(shiftFilter(toolContext.getTileCoordinate(point)));

				// 配置
				if (oStart.isPresent()) {
					List<TileCoordinate> tileCoordinates = calcCoordinates(oStart.get(), oEnd.get());
					for (TileCoordinate tileCoordinate : tileCoordinates) {
						setTile(tileCoordinate, toolContext.getTileCurrent());
					}
				}

				// 初期化
				oStart = Optional.empty();
				oEnd = Optional.empty();

			}

			// 右アップで配置して押下状態解除
			if (e.getButton() == MouseEvent.BUTTON3) {

				// 範囲の終端を更新
				oEnd = Optional.of(shiftFilter(toolContext.getTileCoordinate(point)));

				// 配置
				if (oStart.isPresent()) {
					List<TileCoordinate> tileCoordinates = calcCoordinates(oStart.get(), oEnd.get());
					for (TileCoordinate tileCoordinate : tileCoordinates) {
						setTile(tileCoordinate, Optional.empty());
					}
				}

				// 初期化
				oStart = Optional.empty();
				oEnd = Optional.empty();

			}

			toolContext.repaintOverlay();
		}
	};
	private MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
		@Override
		public void mouseDragged(MouseEvent e)
		{
			Point point = e.getPoint();

			// 範囲の終端を更新
			oEnd = Optional.of(shiftFilter(toolContext.getTileCoordinate(point)));

			toolContext.repaintOverlay();
		}
	};

	private TileCoordinate shiftFilter(TileCoordinate end)
	{
		if (oStart.isPresent()) {
			if (keys[KeyEvent.VK_SHIFT]) {
				TileCoordinate offset = end.minus(oStart.get());
				if (Math.abs(offset.x) > Math.abs(offset.z)) { // 横長
					return new TileCoordinate(end.x, oStart.get().z);
				} else { // 縦長
					return new TileCoordinate(oStart.get().x, end.z);
				}
			}
		}
		return end;
	}

	@Override
	public void on()
	{
		super.on();
		toolContext.getComponent().addMouseListener(mouseListener);
		toolContext.getComponent().addMouseMotionListener(mouseMotionListener);
	}

	@Override
	public void off()
	{
		super.off();
		toolContext.getComponent().removeMouseListener(mouseListener);
		toolContext.getComponent().removeMouseMotionListener(mouseMotionListener);
	}

	@Override
	public void draw(Graphics2D graphics)
	{
		if (oMousePosition.isPresent()) {

			// ブラシの影響範囲を描画
			{

				if (oStart.isPresent() && oEnd.isPresent()) {
					List<TileCoordinate> tileCoordinates = calcCoordinates(oStart.get(), oEnd.get());
					for (TileCoordinate tileCoordinate : tileCoordinates) {

						// タイルの表示大きさ
						int size = toolContext.getTileSize();

						// 描画
						Point position = toolContext.getTilePosition(tileCoordinate);

						graphics.setColor(Color.white);
						graphics.drawLine(
							position.x,
							position.y,
							position.x + size,
							position.y + size);
						graphics.drawLine(
							position.x + size,
							position.y,
							position.x,
							position.y + size);

					}
				}

			}

		}
	}

	/**
	 * @param end
	 *            この座標を含みます。
	 */
	public static List<TileCoordinate> calcCoordinates(TileCoordinate start, TileCoordinate end)
	{
		return a1(end.minus(start), c -> c.plus(start));
	}

	private static List<TileCoordinate> a1(TileCoordinate offset, Function<TileCoordinate, TileCoordinate> function)
	{
		if (Math.abs(offset.x) > Math.abs(offset.z)) { // 横に長い
			return a2(new TileCoordinate(offset.x, offset.z), c -> function.apply(new TileCoordinate(c.x, c.z)));
		} else { // 縦に長い
			return a2(new TileCoordinate(offset.z, offset.x), c -> function.apply(new TileCoordinate(c.z, c.x)));
		}
	}

	/**
	 * 横に長くなければならない
	 */
	private static List<TileCoordinate> a2(TileCoordinate offset, Function<TileCoordinate, TileCoordinate> function)
	{
		if (offset.x > 0) { // 右向き
			return a3(new TileCoordinate(offset.x, offset.z), c -> function.apply(new TileCoordinate(c.x, c.z)));
		} else { // 左向き
			return a3(new TileCoordinate(-offset.x, offset.z), c -> function.apply(new TileCoordinate(-c.x, c.z)));
		}
	}

	/**
	 * 横に長く、+X向きでなければならない
	 */
	private static List<TileCoordinate> a3(TileCoordinate offset, Function<TileCoordinate, TileCoordinate> function)
	{
		if (offset.z > 0) { // 上り
			return a4(new TileCoordinate(offset.x, offset.z), c -> function.apply(new TileCoordinate(c.x, c.z)));
		} else { // 下り
			return a4(new TileCoordinate(offset.x, -offset.z), c -> function.apply(new TileCoordinate(c.x, -c.z)));
		}
	}

	/**
	 * 横に長く、+X+Z向きでなければならない
	 */
	private static List<TileCoordinate> a4(TileCoordinate offset, Function<TileCoordinate, TileCoordinate> function)
	{
		List<TileCoordinate> result = new ArrayList<>();
		for (int i = 0; i <= offset.x; i++) {
			result.add(function.apply(new TileCoordinate(i, (2 * i + 1) * (offset.z + 1) / (offset.x + 1) / 2)));
		}
		return result;
	}

	private void setTile(TileCoordinate tileCoordinate, Optional<RegionIdentifier> tile)
	{
		if (!toolContext.getLayerController().tileMapController.model.getTile(tileCoordinate).equals(tile)) {
			toolContext.getLayerController().tileMapController.model.setTile(tileCoordinate, tile);
			toolContext.getLayerController().tileMapController.epChangedTileSpecified.trigger().accept(tileCoordinate);
		}
	}

}
