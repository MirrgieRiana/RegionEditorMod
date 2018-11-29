package mirrg.minecraft.regioneditor.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import mirrg.minecraft.regioneditor.data.ITileMapReader;
import mirrg.minecraft.regioneditor.data.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.TileBoundingBox;
import mirrg.minecraft.regioneditor.data.TileIndex;

public class ToolFill implements ITool
{

	protected final IToolContext toolContext;

	public ToolFill(IToolContext toolContext)
	{
		this.toolContext = toolContext;
	}

	private MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e)
		{
			TileIndex tileIndex = toolContext.getTileIndex(e.getPoint());

			Optional<RegionIdentifier> tile;
			if (e.getButton() == MouseEvent.BUTTON1) {
				tile = toolContext.getCurrentRegionIdentifier();
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				tile = Optional.empty();
			} else {
				return;
			}

			Optional<Set<TileIndex>> oTileIndexes = calc(tileIndex);
			if (!oTileIndexes.isPresent()) return;

			setTiles(oTileIndexes.get(), tile);
		}

		private Optional<Set<TileIndex>> calc(TileIndex tileIndexOrigin)
		{
			ITileMapReader tileMap = toolContext.getPossessionMapModel().tileMapModel.getDataReader();
			TileBoundingBox boundingBox = tileMap.getBoundingBox();
			Optional<RegionIdentifier> tileOrigin = tileMap.get(tileIndexOrigin);

			return new Object() {
				private HashSet<TileIndex> tileIndexesVisited;
				private ArrayDeque<TileIndex> tileIndexesWaiting;

				private Optional<Set<TileIndex>> calc()
				{
					tileIndexesVisited = new HashSet<>();
					tileIndexesWaiting = new ArrayDeque<>();

					visit(tileIndexOrigin);

					while (!tileIndexesWaiting.isEmpty()) {
						TileIndex tileIndex = tileIndexesWaiting.removeFirst();

						// 四方のマスが踏める（同属性かつ未訪問）場合、そのマスを踏みつつ訪問先に予約する
						try {
							tryVisit(tileIndex.plus(-1, 0));
							tryVisit(tileIndex.plus(1, 0));
							tryVisit(tileIndex.plus(0, -1));
							tryVisit(tileIndex.plus(0, 1));
						} catch (BoundingBoxOverflowException e) {
							return Optional.empty();
						}

					}

					return Optional.of(tileIndexesVisited);
				}

				private void tryVisit(TileIndex tileIndex) throws BoundingBoxOverflowException
				{
					Optional<RegionIdentifier> tile = tileMap.get(tileIndex);

					if (!tile.equals(tileOrigin)) return; // 原点と同じ属性のマスで、
					if (!tile.isPresent() && !boundingBox.contains(tileIndex)) throw new BoundingBoxOverflowException(); // 範囲が無限ではなく
					if (tileIndexesVisited.contains(tileIndex)) return; // かつ訪問済みでなければ
					visit(tileIndex);
				}

				private void visit(TileIndex tileIndex)
				{
					tileIndexesVisited.add(tileIndex);
					tileIndexesWaiting.addLast(tileIndex);
				}

				class BoundingBoxOverflowException extends Exception
				{

				}
			}.calc();
		}

		private void setTiles(Set<TileIndex> tileIndexes, Optional<RegionIdentifier> tile)
		{

			toolContext.getPossessionMapModel().tileMapModel.modify(tileMap -> {
				for (TileIndex tileIndex : tileIndexes) {
					toolContext.getPossessionMapModel().tileMapModel.set(tileIndex, tile);
				}
			});
		}
	};

	@Override
	public void on()
	{
		toolContext.getComponent().addMouseListener(mouseListener);
	}

	@Override
	public void off()
	{
		toolContext.getComponent().removeMouseListener(mouseListener);
	}

}
