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
import java.util.Optional;

import mirrg.minecraft.regioneditor.data.objects.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.objects.TileCoordinate;
import mirrg.minecraft.regioneditor.gui.tool.IToolContext;

public class ToolBrush extends ToolBase
{

	public ToolBrush(IToolContext toolContext)
	{
		super(toolContext);
	}

	private MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e)
		{
			Point point = e.getPoint();

			// 左クリックで塗る
			if (e.getButton() == MouseEvent.BUTTON1) {
				int brushSize = getBrushSize();
				if (brushSize % 2 == 0) point.translate(toolContext.getTileSize() / 2, toolContext.getTileSize() / 2);
				setTile(toolContext.getTileCoordinate(point), toolContext.getTileCurrent(), brushSize);
			}

			// 中央クリックでスポイト
			if (e.getButton() == MouseEvent.BUTTON2) {
				toolContext.setTileCurrent(toolContext.getLayerController().tileMapController.model.getTile(toolContext.getTileCoordinate(point)));
			}

			// 右クリックで破壊
			if (e.getButton() == MouseEvent.BUTTON3) {
				int brushSize = getBrushSize();
				if (brushSize % 2 == 0) point.translate(toolContext.getTileSize() / 2, toolContext.getTileSize() / 2);
				setTile(toolContext.getTileCoordinate(point), Optional.empty(), brushSize);
			}

			toolContext.repaintOverlay();
		}
	};
	private MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
		@Override
		public void mouseDragged(MouseEvent e)
		{
			Point point = e.getPoint();

			// 左クリックで塗る
			if (mouseButtons[1]) {
				int brushSize = getBrushSize();
				if (brushSize % 2 == 0) point.translate(toolContext.getTileSize() / 2, toolContext.getTileSize() / 2);
				setTile(toolContext.getTileCoordinate(point), toolContext.getTileCurrent(), brushSize);
			}

			// 右クリックで破壊
			if (mouseButtons[3]) {
				int brushSize = getBrushSize();
				if (brushSize % 2 == 0) point.translate(toolContext.getTileSize() / 2, toolContext.getTileSize() / 2);
				setTile(toolContext.getTileCoordinate(point), Optional.empty(), brushSize);
			}

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
			{

				// ブラシサイズ
				int brushSize = getBrushSize();

				// タイル座標
				Point point = new Point(oMousePosition.get());
				if (brushSize % 2 == 0) point.translate(toolContext.getTileSize() / 2, toolContext.getTileSize() / 2);
				TileCoordinate tileCoordinate = toolContext.getTileCoordinate(point);

				// タイルの表示大きさ
				int size = toolContext.getTileSize();

				// 描画
				for (int xi = -brushSize / 2; xi < (brushSize + 1) / 2; xi++) {
					for (int zi = -brushSize / 2; zi < (brushSize + 1) / 2; zi++) {
						Point position = toolContext.getTilePosition(tileCoordinate.plus(xi, zi));

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

	protected int getBrushSize()
	{
		int brushSize = toolContext.getBrushSize();
		if (keys[KeyEvent.VK_CONTROL]) brushSize = 1;
		if (keys[KeyEvent.VK_SHIFT]) brushSize *= 2;
		return brushSize;
	}

	private void setTile(TileCoordinate tileCoordinate, Optional<RegionIdentifier> tile, int brushSize)
	{
		for (int xi = -brushSize / 2; xi < (brushSize + 1) / 2; xi++) {
			for (int zi = -brushSize / 2; zi < (brushSize + 1) / 2; zi++) {
				setTile(tileCoordinate.plus(xi, zi), tile);
			}
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
