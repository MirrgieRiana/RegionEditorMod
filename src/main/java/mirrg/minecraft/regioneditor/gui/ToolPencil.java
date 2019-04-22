package mirrg.minecraft.regioneditor.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.Optional;

import mirrg.minecraft.regioneditor.data.objects.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.objects.RegionInfo;
import mirrg.minecraft.regioneditor.data.objects.TileCoordinate;

public class ToolPencil implements ITool
{

	protected final IToolContext toolContext;

	private boolean[] mouseButtons = new boolean[8];
	private Optional<Point> oMousePosition = Optional.empty();

	public ToolPencil(IToolContext toolContext)
	{
		this.toolContext = toolContext;
	}

	private FocusListener focusListener = new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e)
		{
			for (int i = 0; i < mouseButtons.length; i++) {
				mouseButtons[i] = false;
			}
		}
	};
	private MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e)
		{
			oMousePosition = Optional.of(e.getPoint());
			mouseButtons[Math.min(e.getButton(), mouseButtons.length - 1)] = true;
			toolContext.repaintOverlay();

			TileCoordinate tileCoordinate = toolContext.getTileCoordinate(e.getPoint());
			if (e.getButton() == MouseEvent.BUTTON2) {
				toolContext.setCurrentRegionIdentifier(toolContext.getLayerController().tileMapController.model.get(tileCoordinate));
			} else if (e.getButton() == MouseEvent.BUTTON1) {
				setTile(tileCoordinate, toolContext.getCurrentRegionIdentifier());
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				setTile(tileCoordinate, Optional.empty());
			}
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			oMousePosition = Optional.of(e.getPoint());
			mouseButtons[Math.min(e.getButton(), mouseButtons.length - 1)] = false;
			toolContext.repaintOverlay();
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
			oMousePosition = Optional.of(e.getPoint());
			toolContext.repaintOverlay();
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			oMousePosition = Optional.empty();
			toolContext.repaintOverlay();
		}
	};
	private MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
		@Override
		public void mouseMoved(MouseEvent e)
		{
			oMousePosition = Optional.of(e.getPoint());
			toolContext.repaintOverlay();
		}

		@Override
		public void mouseDragged(MouseEvent e)
		{
			oMousePosition = Optional.of(e.getPoint());
			toolContext.repaintOverlay();

			TileCoordinate tileCoordinate = toolContext.getTileCoordinate(e.getPoint());
			if (mouseButtons[MouseEvent.BUTTON1]) {
				setTile(tileCoordinate, toolContext.getCurrentRegionIdentifier());
			} else if (mouseButtons[MouseEvent.BUTTON3]) {
				setTile(tileCoordinate, Optional.empty());
			}
		}
	};

	@Override
	public void on()
	{
		toolContext.getComponent().addFocusListener(focusListener);
		toolContext.getComponent().addMouseListener(mouseListener);
		toolContext.getComponent().addMouseMotionListener(mouseMotionListener);
	}

	@Override
	public void off()
	{
		toolContext.getComponent().removeFocusListener(focusListener);
		toolContext.getComponent().removeMouseListener(mouseListener);
		toolContext.getComponent().removeMouseMotionListener(mouseMotionListener);
	}

	@Override
	public void draw(Graphics2D graphics)
	{
		if (oMousePosition.isPresent()) {
			TileCoordinate tileCoordinate = toolContext.getTileCoordinate(oMousePosition.get());
			int size = toolContext.getTileSize();
			Point position = toolContext.getTilePosition(tileCoordinate);

			graphics.setColor(Color.white);
			graphics.drawLine(
				position.x,
				position.y,
				position.x + size,
				position.y + toolContext.getTileSize());
			graphics.drawLine(
				position.x + size,
				position.y,
				position.x,
				position.y + toolContext.getTileSize());

		}
	}

	@Override
	public void drawTooltip(Graphics2D graphics)
	{
		if (oMousePosition.isPresent()) {

			int height = graphics.getFontMetrics().getHeight();

			TileCoordinate tileCoordinate = toolContext.getTileCoordinate(oMousePosition.get());

			graphics.drawString(
				tileCoordinate.x + ", " + tileCoordinate.z,
				oMousePosition.get().x + 2,
				oMousePosition.get().y - height * 2 - 2);

			Optional<RegionIdentifier> oRegionIdentifier = toolContext.getLayerController().tileMapController.model.get(tileCoordinate);
			if (oRegionIdentifier.isPresent()) {
				RegionInfo regionInfo = toolContext.getLayerController().regionTableController.model.get(oRegionIdentifier.get());

				graphics.drawString(
					"Country: (" + oRegionIdentifier.get().countryId + ") " + regionInfo.countryName,
					oMousePosition.get().x + 2,
					oMousePosition.get().y - height * 1 - 2);
				graphics.drawString(
					"State: (" + oRegionIdentifier.get().stateId + ") " + regionInfo.stateName,
					oMousePosition.get().x + 2,
					oMousePosition.get().y - height * 0 - 2);

			}

		}
	}

	private void setTile(TileCoordinate tileCoordinate, Optional<RegionIdentifier> oRegionIdentifier)
	{
		if (!toolContext.getLayerController().tileMapController.model.get(tileCoordinate).equals(oRegionIdentifier)) {
			toolContext.getLayerController().tileMapController.model.set(tileCoordinate, oRegionIdentifier);
		}
	}

}
