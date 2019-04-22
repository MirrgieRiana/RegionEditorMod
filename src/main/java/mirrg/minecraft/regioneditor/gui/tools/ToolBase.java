package mirrg.minecraft.regioneditor.gui.tools;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.Optional;

import mirrg.minecraft.regioneditor.data.objects.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.objects.RegionInfo;
import mirrg.minecraft.regioneditor.data.objects.TileCoordinate;
import mirrg.minecraft.regioneditor.gui.tool.ITool;
import mirrg.minecraft.regioneditor.gui.tool.IToolContext;

public abstract class ToolBase implements ITool
{

	protected final IToolContext toolContext;

	protected boolean[] mouseButtons = new boolean[8];
	protected boolean[] keys = new boolean[2048];
	protected Optional<Point> oMousePosition = Optional.empty();

	public ToolBase(IToolContext toolContext)
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
			for (int i = 0; i < keys.length; i++) {
				keys[i] = false;
			}
			toolContext.repaintOverlay();
		}
	};
	private KeyListener keyListener = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e)
		{
			keys[Math.min(e.getKeyCode(), keys.length - 1)] = true;
			toolContext.repaintOverlay();
		}

		@Override
		public void keyReleased(KeyEvent e)
		{
			keys[Math.min(e.getKeyCode(), keys.length - 1)] = false;
			toolContext.repaintOverlay();
		}
	};
	private MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e)
		{
			oMousePosition = Optional.of(e.getPoint());
			mouseButtons[Math.min(e.getButton(), mouseButtons.length - 1)] = true;
			toolContext.repaintOverlay();
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
		}
	};

	@Override
	public void on()
	{
		toolContext.getComponent().addFocusListener(focusListener);
		toolContext.getComponent().addKeyListener(keyListener);
		toolContext.getComponent().addMouseListener(mouseListener);
		toolContext.getComponent().addMouseMotionListener(mouseMotionListener);
	}

	@Override
	public void off()
	{
		toolContext.getComponent().removeFocusListener(focusListener);
		toolContext.getComponent().removeKeyListener(keyListener);
		toolContext.getComponent().removeMouseListener(mouseListener);
		toolContext.getComponent().removeMouseMotionListener(mouseMotionListener);
	}

	@Override
	public void drawTooltip(Graphics2D graphics)
	{
		if (oMousePosition.isPresent()) {

			// フォント高さ
			int height = graphics.getFontMetrics().getHeight();

			// マウスが乗っているタイルの位置
			TileCoordinate tileCoordinate = toolContext.getTileCoordinate(oMousePosition.get());

			// 座標表示
			graphics.drawString(
				tileCoordinate.x + ", " + tileCoordinate.z,
				oMousePosition.get().x + 2,
				oMousePosition.get().y - height * 2 - 2);

			// 領域情報表示
			Optional<RegionIdentifier> oRegionIdentifier = toolContext.getLayerController().tileMapController.model.get(tileCoordinate);
			if (oRegionIdentifier.isPresent()) {
				RegionInfo regionInfo = toolContext.getLayerController().regionTableController.model.get(oRegionIdentifier.get());

				graphics.drawString(
					toolContext.localize("ToolBase.label.country") + ": (" + oRegionIdentifier.get().countryId + ") " + regionInfo.countryName,
					oMousePosition.get().x + 2,
					oMousePosition.get().y - height * 1 - 2);
				graphics.drawString(
					toolContext.localize("ToolBase.label.state") + ": (" + oRegionIdentifier.get().stateId + ") " + regionInfo.stateName,
					oMousePosition.get().x + 2,
					oMousePosition.get().y - height * 0 - 2);

			}

		}
	}

}
