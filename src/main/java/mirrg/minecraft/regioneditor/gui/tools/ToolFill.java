package mirrg.minecraft.regioneditor.gui.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import mirrg.minecraft.regioneditor.data.AreaExtractor;
import mirrg.minecraft.regioneditor.data.models.TileMapModel;
import mirrg.minecraft.regioneditor.data.objects.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.objects.TileCoordinate;
import mirrg.minecraft.regioneditor.data.objects.TileRectangle;
import mirrg.minecraft.regioneditor.gui.tool.IToolContext;

public class ToolFill extends ToolBase
{

	public ToolFill(IToolContext toolContext)
	{
		super(toolContext, "ToolFill.name");
	}

	private MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e)
		{
			TileCoordinate tileCoordinate = toolContext.getTileCoordinate(e.getPoint());

			Optional<RegionIdentifier> tile;
			if (e.getButton() == MouseEvent.BUTTON1) {
				tile = toolContext.getTileCurrent();
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				tile = Optional.empty();
			} else {
				return;
			}

			Optional<Set<TileCoordinate>> oTileCoordinates = calc(tileCoordinate);
			if (!oTileCoordinates.isPresent()) return;

			setTiles(oTileCoordinates.get(), tile);
			updateCursor(e.getPoint(), true);
		}

		private void setTiles(Set<TileCoordinate> tileCoordinates, Optional<RegionIdentifier> tile)
		{
			if (tileCoordinates.size() > 50) {
				for (TileCoordinate tileCoordinate : tileCoordinates) {
					toolContext.getLayerController().tileMapController.model.setTile(tileCoordinate, tile);
				}
				toolContext.getLayerController().tileMapController.epChangedTileUnspecified.trigger().run();
			} else {
				for (TileCoordinate tileCoordinate : tileCoordinates) {
					toolContext.getLayerController().tileMapController.model.setTile(tileCoordinate, tile);
					toolContext.getLayerController().tileMapController.epChangedTileSpecified.trigger().accept(tileCoordinate);
				}
			}
		}
	};
	private MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
		@Override
		public void mouseMoved(MouseEvent e)
		{
			updateCursor(e.getPoint(), false);
		}
	};

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

		// 塗りつぶし範囲を描画
		if (oTileCoordinates.isPresent()) {

			// タイルの表示大きさ
			int size = toolContext.getTileSize();

			for (TileCoordinate tileCoordinate2 : oTileCoordinates.get()) {

				// 可視範囲にあるなら
				if (toolContext.isVisible(tileCoordinate2)) {

					// 描画
					Point position = toolContext.getTilePosition(tileCoordinate2);

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

	private Optional<Set<TileCoordinate>> calc(TileCoordinate tileCoordinateOrigin)
	{
		TileMapModel tileMapModel = toolContext.getLayerController().tileMapController.model;
		TileRectangle boundingBox = AreaExtractor.getBoundingBox(tileMapModel);
		Optional<RegionIdentifier> tileOrigin = tileMapModel.getTile(tileCoordinateOrigin);

		return new Object() {
			private HashSet<TileCoordinate> tileCoordinatesVisited;
			private ArrayDeque<TileCoordinate> tileCoordinatesWaiting;

			private Optional<Set<TileCoordinate>> calc()
			{
				tileCoordinatesVisited = new HashSet<>();
				tileCoordinatesWaiting = new ArrayDeque<>();

				visit(tileCoordinateOrigin);

				while (!tileCoordinatesWaiting.isEmpty()) {
					TileCoordinate tileCoordinate = tileCoordinatesWaiting.removeFirst();

					// 四方のマスが踏める（同属性かつ未訪問）場合、そのマスを踏みつつ訪問先に予約する
					try {
						tryVisit(tileCoordinate.plus(-1, 0));
						tryVisit(tileCoordinate.plus(1, 0));
						tryVisit(tileCoordinate.plus(0, -1));
						tryVisit(tileCoordinate.plus(0, 1));
					} catch (BoundingBoxOverflowException e) {
						return Optional.empty();
					}

				}

				return Optional.of(tileCoordinatesVisited);
			}

			private void tryVisit(TileCoordinate tileCoordinate) throws BoundingBoxOverflowException
			{
				Optional<RegionIdentifier> tile = tileMapModel.getTile(tileCoordinate);

				if (!tile.equals(tileOrigin)) return; // 原点と同じ属性のマスで、
				if (!tile.isPresent() && !boundingBox.contains(tileCoordinate)) throw new BoundingBoxOverflowException(); // 範囲が無限ではなく
				if (tileCoordinatesVisited.contains(tileCoordinate)) return; // かつ訪問済みでなければ
				visit(tileCoordinate);
			}

			private void visit(TileCoordinate tileCoordinate)
			{
				tileCoordinatesVisited.add(tileCoordinate);
				tileCoordinatesWaiting.addLast(tileCoordinate);
			}

			class BoundingBoxOverflowException extends Exception
			{

			}
		}.calc();
	}

	private Optional<TileCoordinate> oTileCoordinate = Optional.empty();
	private Optional<Set<TileCoordinate>> oTileCoordinates = Optional.empty();

	private void updateCursor(Point point, boolean forced)
	{

		// タイル座標
		TileCoordinate tileCoordinate = toolContext.getTileCoordinate(point);

		// 未更新なら更新
		if (forced || (!oTileCoordinate.isPresent() || !oTileCoordinate.get().equals(tileCoordinate))) {
			oTileCoordinate = Optional.of(tileCoordinate);
			oTileCoordinates = calc(tileCoordinate);
		}

	}

}
