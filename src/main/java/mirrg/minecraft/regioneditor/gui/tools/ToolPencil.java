package mirrg.minecraft.regioneditor.gui.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.Optional;

import mirrg.minecraft.regioneditor.data.objects.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.objects.TileCoordinate;
import mirrg.minecraft.regioneditor.gui.tool.IToolContext;

public class ToolPencil extends ToolBase
{

	public ToolPencil(IToolContext toolContext)
	{
		super(toolContext);
	}

	private Optional<TileCoordinate> oTileCoordinatePrevious = Optional.empty();

	private MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e)
		{
			Point point = e.getPoint();

			// 左クリックで塗る
			if (e.getButton() == MouseEvent.BUTTON1) {
				setTile(toolContext.getTileCoordinate(point), toolContext.getTileCurrent());
			}

			// 中央クリックでスポイト
			if (e.getButton() == MouseEvent.BUTTON2) {
				toolContext.setTileCurrent(toolContext.getLayerController().tileMapController.model.getTile(toolContext.getTileCoordinate(point)));
			}

			// 右クリックで破壊
			if (e.getButton() == MouseEvent.BUTTON3) {
				setTile(toolContext.getTileCoordinate(point), Optional.empty());
			}

			oTileCoordinatePrevious = Optional.ofNullable(toolContext.getTileCoordinate(point));

			toolContext.repaintOverlay();
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			oTileCoordinatePrevious = Optional.empty();
		}
	};
	private MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
		@Override
		public void mouseDragged(MouseEvent e)
		{
			Point point = e.getPoint();

			// 左クリックで塗る
			if (mouseButtons[1]) {
				if (oTileCoordinatePrevious.isPresent()) {
					for (TileCoordinate tileCoordinate : ToolLine.calcCoordinates(oTileCoordinatePrevious.get(), toolContext.getTileCoordinate(point))) {
						setTile(tileCoordinate, toolContext.getTileCurrent());
					}
				} else {
					setTile(toolContext.getTileCoordinate(point), toolContext.getTileCurrent());
				}
			}

			// 右クリックで破壊
			if (mouseButtons[3]) {
				if (oTileCoordinatePrevious.isPresent()) {
					for (TileCoordinate tileCoordinate : ToolLine.calcCoordinates(oTileCoordinatePrevious.get(), toolContext.getTileCoordinate(point))) {
						setTile(tileCoordinate, Optional.empty());
					}
				} else {
					setTile(toolContext.getTileCoordinate(point), Optional.empty());
				}
			}

			oTileCoordinatePrevious = Optional.ofNullable(toolContext.getTileCoordinate(point));

			toolContext.repaintOverlay();
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
		if (oMousePosition.isPresent()) {

			// ブラシの影響範囲を描画
			Point position = toolContext.getTilePosition(toolContext.getTileCoordinate(oMousePosition.get()));

			graphics.setColor(Color.white);
			graphics.drawLine(
				position.x,
				position.y,
				position.x + toolContext.getTileSize(),
				position.y + toolContext.getTileSize());
			graphics.drawLine(
				position.x + toolContext.getTileSize(),
				position.y,
				position.x,
				position.y + toolContext.getTileSize());

		}
	}

	private void setTile(TileCoordinate tileCoordinate, Optional<RegionIdentifier> tile)
	{
		if (!toolContext.getLayerController().tileMapController.model.getTile(tileCoordinate).equals(tile)) {
			toolContext.getLayerController().tileMapController.model.setTile(tileCoordinate, tile);
			toolContext.getLayerController().tileMapController.epChangedTileSpecified.trigger().accept(tileCoordinate);
		}
	}

}
