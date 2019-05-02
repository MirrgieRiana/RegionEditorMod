package mirrg.minecraft.regioneditor.gui.guis;

import static mirrg.boron.swing.UtilsComponent.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import mirrg.boron.util.i18n.I18n;
import mirrg.boron.util.struct.Struct1;
import mirrg.minecraft.regioneditor.IChatMessageProvider;
import mirrg.minecraft.regioneditor.data.AreaExtractor;
import mirrg.minecraft.regioneditor.data.ModelException;
import mirrg.minecraft.regioneditor.data.objects.RegionEntry;
import mirrg.minecraft.regioneditor.data.objects.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.objects.RegionInfo;
import mirrg.minecraft.regioneditor.data.objects.TileCoordinate;
import mirrg.minecraft.regioneditor.gui.CanvasMap;
import mirrg.minecraft.regioneditor.gui.CanvasMap.ICanvasMapListener;
import mirrg.minecraft.regioneditor.gui.PanelResult;
import mirrg.minecraft.regioneditor.gui.guis.GuiData.IDialogDataListener;
import mirrg.minecraft.regioneditor.gui.imagelayers.ImageLayerTile;
import mirrg.minecraft.regioneditor.gui.mapimage.MapImageProviderBufferedImage;
import mirrg.minecraft.regioneditor.gui.tool.ITool;
import mirrg.minecraft.regioneditor.gui.tools.ToolBrush;
import mirrg.minecraft.regioneditor.gui.tools.ToolFill;
import mirrg.minecraft.regioneditor.gui.tools.ToolLine;
import mirrg.minecraft.regioneditor.gui.tools.ToolNothing;
import mirrg.minecraft.regioneditor.gui.tools.ToolPencil;
import mirrg.minecraft.regioneditor.util.gui.ActionBuilder;
import mirrg.minecraft.regioneditor.util.gui.ActionButton;
import mirrg.minecraft.regioneditor.util.gui.ActionRadio;
import mirrg.minecraft.regioneditor.util.gui.ActionToggle;
import mirrg.minecraft.regioneditor.util.gui.BitMapFont;
import mirrg.minecraft.regioneditor.util.gui.CheckBoxMenuItem;
import mirrg.minecraft.regioneditor.util.gui.FontRenderer;
import mirrg.minecraft.regioneditor.util.gui.MenuItem;
import mirrg.minecraft.regioneditor.util.gui.WindowWrapper;

public class GuiRegionEditor extends GuiBase
{

	private final Optional<Consumer<List<String>>> oSender;
	private final Optional<IChatMessageProvider> oChatMessageProvider;
	private final FontRenderer fontRenderer;

	private InputMap inputMap;
	private ActionMap actionMap;

	private ActionButton actionOpenGuiData;
	private ActionButton actionOpenGuiCommand;

	private ActionButton actionLoadMapFromLocalFile;
	private ActionButton actionLoadMapFromUrl;
	private ActionButton actionScrollLeft;
	private ActionButton actionScrollRight;
	private ActionButton actionScrollUp;
	private ActionButton actionScrollDown;
	private ActionToggle actionToggleShowMap;
	private ActionToggle actionToggleShowTile;
	private ActionToggle actionToggleShowTooltip;
	private ActionToggle actionToggleShowArea;
	private ActionToggle actionToggleShowBorder;
	private ActionToggle actionToggleShowIdentifier;
	private ActionToggle actionToggleShowGrid;

	private ActionRadio actionToolNothing;
	private ActionRadio actionToolPencil;
	private ActionRadio actionToolBrush;
	private ActionRadio actionToolFill;
	private ActionRadio actionToolLine;
	private ActionRadio actionToolSpuit;
	private ActionButton actionIncrementBrushSize;
	private ActionButton actionDecrementBrushSize;

	private ActionButton actionClearMap;

	private ActionButton actionCreateRegion;
	private ActionButton actionEditRegion;
	private ActionButton actionDeleteRegion;
	private ActionButton actionChangeRegionIdentifier;
	private ActionButton actionScrollToRegion;

	private CanvasMap canvasMap;
	private JLabel labelCoordX;
	private JLabel labelCoordZ;
	private JLabel labelTileX;
	private JLabel labelTileZ;
	private JFormattedTextField textFieldX;
	private JFormattedTextField textFieldZ;
	@SuppressWarnings("unused")
	private JSpinner spinnerBrushSize;
	private SpinnerNumberModel modelSpinnerBrushSize;
	private JList<Optional<RegionIdentifier>> tableRegion;
	private DefaultListModel<Optional<RegionIdentifier>> modelTableRegion;
	private PanelResult panelResult;

	public GuiRegionEditor(WindowWrapper owner, I18n i18n, Optional<Consumer<List<String>>> oSender, Optional<IChatMessageProvider> oChatMessageProvider) throws IOException
	{
		super(owner, i18n, i18n.localize("GuiRegionEditor.title"), ModalityType.MODELESS);
		this.oSender = oSender;
		this.oChatMessageProvider = oChatMessageProvider;
		this.fontRenderer = new FontRenderer(new BitMapFont(Objects.requireNonNull(GuiRegionEditor.class.getResource("font.png")), 5, 7, 4));
	}

	@Override
	protected void initComponenets()
	{
		ArrayList<Runnable> listenersPreInit = new ArrayList<>();

		// アイコン
		try (InputStream in = GuiRegionEditor.class.getResourceAsStream("icon2.png")) {
			if (in != null) {
				windowWrapper.getWindow().setIconImage(ImageIO.read(in));
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// アクション
		{
			inputMap = new InputMap();
			actionMap = new ActionMap();

			actionOpenGuiData = new ActionBuilder<>(new ActionButton(e -> new GuiData(windowWrapper, i18n, new IDialogDataListener() {
				@Override
				public void onImport(String string) throws Exception
				{
					canvasMap.setExpression(string);
				}

				@Override
				public String onExport()
				{
					try {
						return canvasMap.getExpression();
					} catch (Exception e) {
						e.printStackTrace();
						return "Error";
					}
				}
			}).show()))
				.value(Action.NAME, localize("GuiRegionEditor.actionOpenGuiData") + "(I)...")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_I)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK))
				.register(inputMap, actionMap);
			actionOpenGuiCommand = new ActionBuilder<>(new ActionButton(e -> {
				new GuiCommand(windowWrapper, i18n, new AreaExtractor(canvasMap.layerController.model).getAreas(), oSender, oChatMessageProvider).show();
			}))
				.value(Action.NAME, localize("GuiRegionEditor.actionOpenGuiCommand") + "(D)...")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_D)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK))
				.register(inputMap, actionMap);

			actionLoadMapFromLocalFile = new ActionBuilder<>(new ActionButton(e -> loadMapFromLocal()))
				.value(Action.NAME, localize("GuiRegionEditor.actionLoadMapFromLocalFile") + "(F)...")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_F)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK))
				.register(inputMap, actionMap);
			actionLoadMapFromUrl = new ActionBuilder<>(new ActionButton(e -> loadMapFromURL()))
				.value(Action.NAME, localize("GuiRegionEditor.actionLoadMapFromUrl") + "(U)...")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_U)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK))
				.register(inputMap, actionMap);

			actionScrollLeft = new ActionBuilder<>(new ActionButton(e -> scroll(-4, 0)))
				.value(Action.NAME, localize("GuiRegionEditor.actionScrollLeft") + "(L)...")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_L)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0))
				.register(inputMap, actionMap);
			actionScrollRight = new ActionBuilder<>(new ActionButton(e -> scroll(4, 0)))
				.value(Action.NAME, localize("GuiRegionEditor.actionScrollRight") + "(R)...")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_R)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0))
				.register(inputMap, actionMap);
			actionScrollUp = new ActionBuilder<>(new ActionButton(e -> scroll(0, -4)))
				.value(Action.NAME, localize("GuiRegionEditor.actionScrollUp") + "(U)...")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_U)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0))
				.register(inputMap, actionMap);
			actionScrollDown = new ActionBuilder<>(new ActionButton(e -> scroll(0, 4)))
				.value(Action.NAME, localize("GuiRegionEditor.actionScrollDown") + "(D)...")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_D)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0))
				.register(inputMap, actionMap);
			{
				ActionToggle action = actionToggleShowMap = new ActionBuilder<>(new ActionToggle(v -> canvasMap.setShowMap(v)))
					.value(Action.NAME, localize("GuiRegionEditor.actionToggleShowMap") + "(M)")
					.value(Action.MNEMONIC_KEY, KeyEvent.VK_M)
					.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0))
					.register(inputMap, actionMap);
				listenersPreInit.add(() -> action.setSelected(true));
			}
			{
				ActionToggle action = actionToggleShowTile = new ActionBuilder<>(new ActionToggle(v -> canvasMap.setShowTile(v)))
					.value(Action.NAME, localize("GuiRegionEditor.actionToggleShowTile") + "(T)")
					.value(Action.MNEMONIC_KEY, KeyEvent.VK_T)
					.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0))
					.register(inputMap, actionMap);
				listenersPreInit.add(() -> action.setSelected(true));
			}
			{
				ActionToggle action = actionToggleShowTooltip = new ActionBuilder<>(new ActionToggle(v -> canvasMap.setShowTooltip(v)))
					.value(Action.NAME, localize("GuiRegionEditor.actionToggleShowTooltip") + "(T)")
					.value(Action.MNEMONIC_KEY, KeyEvent.VK_T)
					.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0))
					.register(inputMap, actionMap);
				listenersPreInit.add(() -> action.setSelected(true));
			}
			{
				ActionToggle action = actionToggleShowArea = new ActionBuilder<>(new ActionToggle(v -> canvasMap.setShowArea(v)))
					.value(Action.NAME, localize("GuiRegionEditor.actionToggleShowArea") + "(A)")
					.value(Action.MNEMONIC_KEY, KeyEvent.VK_A)
					.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0))
					.register(inputMap, actionMap);
				listenersPreInit.add(() -> action.setSelected(true));
			}
			{
				ActionToggle action = actionToggleShowBorder = new ActionBuilder<>(new ActionToggle(v -> canvasMap.setShowBorder(v)))
					.value(Action.NAME, localize("GuiRegionEditor.actionToggleShowBorder") + "(B)")
					.value(Action.MNEMONIC_KEY, KeyEvent.VK_B)
					.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0))
					.register(inputMap, actionMap);
				listenersPreInit.add(() -> action.setSelected(true));
			}
			{
				ActionToggle action = actionToggleShowIdentifier = new ActionBuilder<>(new ActionToggle(v -> canvasMap.setShowIdentifier(v)))
					.value(Action.NAME, localize("GuiRegionEditor.actionToggleShowIdentifier") + "(I)")
					.value(Action.MNEMONIC_KEY, KeyEvent.VK_I)
					.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0))
					.register(inputMap, actionMap);
				listenersPreInit.add(() -> action.setSelected(true));
			}
			{
				ActionToggle action = actionToggleShowGrid = new ActionBuilder<>(new ActionToggle(v -> canvasMap.setShowGrid(v)))
					.value(Action.NAME, localize("GuiRegionEditor.actionToggleShowGrid") + "(G)")
					.value(Action.MNEMONIC_KEY, KeyEvent.VK_G)
					.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0))
					.register(inputMap, actionMap);
				listenersPreInit.add(() -> action.setSelected(true));
			}

			List<ActionRadio> groupTool = new ArrayList<>();
			actionToolNothing = new ActionBuilder<>(new ActionRadio(groupTool, v -> {
				canvasMap.setTool(Optional.of(new ToolNothing(canvasMap.getToolContext())));
			}))
				.value(Action.NAME, localize("GuiRegionEditor.actionToolNothing") + "(N)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_N)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0))
				.register(inputMap, actionMap);
			actionToolPencil = new ActionBuilder<>(new ActionRadio(groupTool, v -> {
				canvasMap.setTool(Optional.of(new ToolPencil(canvasMap.getToolContext())));
			}))
				.value(Action.NAME, localize("GuiRegionEditor.actionToolPencil") + "(P)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_P)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0))
				.register(inputMap, actionMap);
			actionToolBrush = new ActionBuilder<>(new ActionRadio(groupTool, v -> {
				canvasMap.setTool(Optional.of(new ToolBrush(canvasMap.getToolContext())));
			}))
				.value(Action.NAME, localize("GuiRegionEditor.actionToolBrush") + "(B)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_B)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_B, 0))
				.register(inputMap, actionMap);
			actionToolFill = new ActionBuilder<>(new ActionRadio(groupTool, v -> {
				canvasMap.setTool(Optional.of(new ToolFill(canvasMap.getToolContext())));
			}))
				.value(Action.NAME, localize("GuiRegionEditor.actionToolFill") + "(F)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_F)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0))
				.register(inputMap, actionMap);
			actionToolLine = new ActionBuilder<>(new ActionRadio(groupTool, v -> {
				canvasMap.setTool(Optional.of(new ToolLine(canvasMap.getToolContext())));
			}))
				.value(Action.NAME, localize("GuiRegionEditor.actionToolLine") + "(L)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_L)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0))
				.register(inputMap, actionMap);
			actionToolSpuit = new ActionBuilder<>(new ActionRadio(groupTool, v -> {
				canvasMap.setTool(Optional.of(new ITool() { // TODO
					@Override
					public void on()
					{

					}

					@Override
					public void off()
					{

					}
				}));
			}))
				.value(Action.NAME, localize("GuiRegionEditor.actionToolSpuit") + "(K)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_K)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_K, 0))
				.register(inputMap, actionMap);
			listenersPreInit.add(() -> actionToolBrush.setSelected(true));
			actionIncrementBrushSize = new ActionBuilder<>(new ActionButton(e -> plusBrushSize(1)))
				.value(Action.NAME, localize("GuiRegionEditor.actionIncrementBrushSize") + "(I)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_I)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0))
				.register(inputMap, actionMap);
			actionDecrementBrushSize = new ActionBuilder<>(new ActionButton(e -> plusBrushSize(-1)))
				.value(Action.NAME, localize("GuiRegionEditor.actionDecrementBrushSize") + "(D)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_D)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0))
				.register(inputMap, actionMap);

			actionClearMap = new ActionBuilder<>(new ActionButton(e -> {
				if (JOptionPane.showConfirmDialog(
					windowWrapper.getWindow(),
					localize("GuiRegionEditor.actionClearMap.message"),
					localize("GuiRegionEditor.actionClearMap.title"),
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {

					for (TileCoordinate tileCoordinate : canvasMap.layerController.tileMapController.model.getKeys().toCollection()) {
						canvasMap.layerController.tileMapController.model.setTile(tileCoordinate, Optional.empty());
					}
					canvasMap.update();

				}
			}))
				.value(Action.NAME, localize("GuiRegionEditor.actionClearMap") + "(C)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_C)
				.register(inputMap, actionMap);

			actionCreateRegion = new ActionBuilder<>(new ActionButton(e -> createRegion()))
				.value(Action.NAME, localize("GuiRegionEditor.actionCreateRegion") + "(N)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_N)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK))
				.register(inputMap, actionMap);
			actionEditRegion = new ActionBuilder<>(new ActionButton(e -> {
				System.out.println("B"); // TODO
			}))
				.value(Action.NAME, localize("GuiRegionEditor.actionEditRegion") + "(E)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_E)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.SHIFT_DOWN_MASK))
				.register(inputMap, actionMap);
			actionDeleteRegion = new ActionBuilder<>(new ActionButton(e -> deleteRegion()))
				.value(Action.NAME, localize("GuiRegionEditor.actionDeleteRegion") + "(D)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_D)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK))
				.register(inputMap, actionMap);
			actionChangeRegionIdentifier = new ActionBuilder<>(new ActionButton(e -> changeRegionIdentifier()))
				.value(Action.NAME, localize("GuiRegionEditor.actionChangeRegionIdentifier") + "(I)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_I)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK))
				.register(inputMap, actionMap);
			actionScrollToRegion = new ActionBuilder<>(new ActionButton(e -> {
				Optional<RegionIdentifier> tileCurrent = canvasMap.getTileCurrent();
				if (tileCurrent.isPresent()) {
					scrollToRegion(tileCurrent.get());
				}
			}))
				.value(Action.NAME, localize("GuiRegionEditor.actionScrollToRegion") + "(S)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_S)
				.register(inputMap, actionMap);
		}

		// メニュー
		{
			class Menu extends JMenu
			{

				public Menu(String text)
				{
					super(text);
					addMenuListener(new MenuListener() {
						@Override
						public void menuSelected(MenuEvent e)
						{
							revalidate();
						}

						@Override
						public void menuDeselected(MenuEvent e)
						{

						}

						@Override
						public void menuCanceled(MenuEvent e)
						{

						}
					});
				}

			}

			windowWrapper.setJMenuBar(get(new JMenuBar(), menuBar2 -> {
				menuBar2.add(get(new Menu(localize("GuiRegionEditor.menuData") + "(D)"), menu -> {
					menu.setMnemonic(KeyEvent.VK_D);
					menu.add(new MenuItem(actionOpenGuiData));
					menu.add(new MenuItem(actionOpenGuiCommand));
				}));
				menuBar2.add(get(new Menu(localize("GuiRegionEditor.menuView") + "(V)"), menu -> {
					menu.setMnemonic(KeyEvent.VK_V);
					menu.add(new MenuItem(actionLoadMapFromLocalFile));
					menu.add(new MenuItem(actionLoadMapFromUrl));
					menu.addSeparator();
					menu.add(new MenuItem(actionScrollLeft));
					menu.add(new MenuItem(actionScrollRight));
					menu.add(new MenuItem(actionScrollUp));
					menu.add(new MenuItem(actionScrollDown));
					menu.addSeparator();
					menu.add(new CheckBoxMenuItem(actionToggleShowMap));
					menu.add(new CheckBoxMenuItem(actionToggleShowTile));
					menu.add(new CheckBoxMenuItem(actionToggleShowTooltip));
					menu.addSeparator();
					menu.add(new CheckBoxMenuItem(actionToggleShowArea));
					menu.add(new CheckBoxMenuItem(actionToggleShowBorder));
					menu.add(new CheckBoxMenuItem(actionToggleShowIdentifier));
					menu.add(new CheckBoxMenuItem(actionToggleShowGrid));
				}));
				menuBar2.add(get(new Menu(localize("GuiRegionEditor.menuMap") + "(M)"), menu -> {
					menu.setMnemonic(KeyEvent.VK_M);
					menu.add(new MenuItem(actionClearMap));
				}));
				menuBar2.add(get(new Menu(localize("GuiRegionEditor.menuTool") + "(T)"), menu -> {
					menu.setMnemonic(KeyEvent.VK_T);
					menu.add(new CheckBoxMenuItem(actionToolNothing));
					menu.addSeparator();
					menu.add(new CheckBoxMenuItem(actionToolPencil));
					menu.add(new CheckBoxMenuItem(actionToolBrush));
					menu.add(new CheckBoxMenuItem(actionToolFill));
					menu.add(new CheckBoxMenuItem(actionToolLine));
					menu.add(new CheckBoxMenuItem(actionToolSpuit));
					menu.addSeparator();
					menu.add(new MenuItem(actionIncrementBrushSize));
					menu.add(new MenuItem(actionDecrementBrushSize));
				}));
				menuBar2.add(get(new Menu(localize("GuiRegionEditor.menuRegion") + "(R)"), menu -> {
					menu.setMnemonic(KeyEvent.VK_R);
					menu.add(new MenuItem(actionCreateRegion));
					menu.add(new MenuItem(actionEditRegion));
					menu.add(new MenuItem(actionDeleteRegion));
					menu.addSeparator();
					menu.add(new MenuItem(actionChangeRegionIdentifier));
					menu.addSeparator();
					menu.add(new MenuItem(actionScrollToRegion));
				}));
			}));
		}

		// コンポーネント
		JComponent component = createSplitPaneVertical(0,

			createSplitPaneHorizontal(0.7,

				createPanelBorderDown(0,

					// 左ペイン：地図側
					createPanelBorderVertical(0,

						get(createButton(localize("GuiRegionEditor.buttonScrollUp"), actionScrollUp), c -> {
							c.setMargin(new Insets(0, 0, 0, 0));
							c.setPreferredSize(new Dimension(32, 32));
						}),

						createPanelBorderHorizontal(0,

							get(createButton(localize("GuiRegionEditor.buttonScrollLeft"), actionScrollLeft), c -> {
								c.setMargin(new Insets(0, 0, 0, 0));
								c.setPreferredSize(new Dimension(32, 32));
							}),

							// 地図
							canvasMap = get(new CanvasMap(fontRenderer, i18n, new ICanvasMapListener() {
								@Override
								public void onChangeTileCurrent(Optional<RegionIdentifier> tileCurrent)
								{
									updateSelection(tileCurrent);
								}

								@Override
								public void onBrushSizeChange(int brushSize)
								{
									modelSpinnerBrushSize.setValue(brushSize);
								}
							}), c -> {
								c.setMinimumSize(new Dimension(100, 100));
								c.setPreferredSize(new Dimension(600, 600));
							}),

							get(createButton(localize("GuiRegionEditor.buttonScrollRight"), actionScrollRight), c -> {
								c.setMargin(new Insets(0, 0, 0, 0));
								c.setPreferredSize(new Dimension(32, 32));
							})

						),

						get(createButton(localize("GuiRegionEditor.buttonScrollDown"), actionScrollDown), c -> {
							c.setMargin(new Insets(0, 0, 0, 0));
							c.setPreferredSize(new Dimension(32, 32));
						})

					),

					createPanelFlow(

						new JLabel(localize("GuiRegionEditor.labelBlockCoordinate") + ":"),

						labelCoordX = new JLabel("???"),

						new JLabel(","),

						labelCoordZ = new JLabel("???"),

						new JLabel(localize("GuiRegionEditor.labelTileCoordinate") + ":"),

						labelTileX = new JLabel("???"),

						new JLabel(","),

						labelTileZ = new JLabel("???")

					),

					createPanelFlow(

						new JLabel(localize("GuiRegionEditor.labelX") + ":"),

						textFieldX = get(new JFormattedTextField(NumberFormat.getIntegerInstance()), c -> {
							c.setValue(0);
							c.setColumns(8);
							c.setHorizontalAlignment(JTextField.RIGHT);
						}),

						new JLabel(localize("GuiRegionEditor.labelZ") + ":"),

						textFieldZ = get(new JFormattedTextField(NumberFormat.getIntegerInstance()), c -> {
							c.setValue(0);
							c.setColumns(8);
							c.setHorizontalAlignment(JTextField.RIGHT);
						}),

						createButton(localize("GuiRegionEditor.buttonJumpToBlockCoordinate"), e -> {
							setPosition(
								((Number) textFieldX.getValue()).intValue() / 16,
								((Number) textFieldZ.getValue()).intValue() / 16);
						}),

						createButton(localize("GuiRegionEditor.buttonJumpToTileCoordinate"), e -> {
							setPosition(
								((Number) textFieldX.getValue()).intValue(),
								((Number) textFieldZ.getValue()).intValue());
						})

					)

				),

				// 右ペイン：領地リストとか操作ボタンとか
				createPanelBorderUp(0,

					// ブラシサイズ
					createPanelFlow(

						new JLabel(localize("GuiRegionEditor.labelBrushSize") + ":"),

						spinnerBrushSize = get(new JSpinner(modelSpinnerBrushSize = get(new SpinnerNumberModel(7, 0, 100, 1), c -> {
							c.addChangeListener(e -> {
								canvasMap.setBrushSize(c.getNumber().intValue());
							});
							listenersPreInit.add(() -> canvasMap.setBrushSize(modelSpinnerBrushSize.getNumber().intValue()));
						})), c -> {
							c.addMouseWheelListener(e -> {
								plusBrushSize(-(int) e.getPreciseWheelRotation());
							});
						})

					),

					createPanelBorderDown(0,

						// 領地一覧
						createScrollPane(tableRegion = get(new JList<>(modelTableRegion = new DefaultListModel<>()), c -> {
							c.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
							c.addMouseListener(new MouseAdapter() {
								@Override
								public void mouseReleased(MouseEvent e)
								{
									if (e.getButton() == MouseEvent.BUTTON1) {
										canvasMap.setTileCurrent(getSelectedItem());
									} else if (e.getButton() == MouseEvent.BUTTON3) {

										int index = c.locationToIndex(e.getPoint());
										if (index < 0) return;
										if (index >= c.getModel().getSize()) return;
										Optional<RegionIdentifier> tile = c.getModel().getElementAt(index);
										if (!tile.isPresent()) return;

										scrollToRegion(tile.get());

									}
								}

								private Optional<RegionIdentifier> getSelectedItem()
								{
									int index = tableRegion.getSelectedIndex();
									if (index < 0) return Optional.empty();
									if (index >= tableRegion.getModel().getSize()) return Optional.empty();
									return tableRegion.getModel().getElementAt(index);
								}
							});
							c.setCellRenderer(new ListCellRenderer<Optional<RegionIdentifier>>() {
								private BufferedImage image = new BufferedImage(17, 17, BufferedImage.TYPE_INT_RGB);
								private Graphics2D graphics = image.createGraphics();

								private JLabel label = new JLabel();
								{
									label.setOpaque(true);
								}

								public Component getListCellRendererComponent(
									JList<? extends Optional<RegionIdentifier>> list,
									Optional<RegionIdentifier> value,
									int index,
									boolean isSelected,
									boolean cellHasFocus)
								{
									if (value.isPresent()) {
										RegionIdentifier regionIdentifier = value.get();
										RegionEntry regionEntry = new RegionEntry(
											regionIdentifier,
											canvasMap.layerController.regionTableController.model.get(regionIdentifier));

										label.setText(regionEntry.toString());
									} else {
										label.setText(localize("GuiRegionEditor.tableRegion.empty"));
									}

									graphics.setBackground(Color.gray);
									graphics.clearRect(0, 0, 17, 17);
									if (value.isPresent()) {
										RegionIdentifier regionIdentifier = value.get();
										RegionEntry regionEntry = new RegionEntry(
											regionIdentifier,
											canvasMap.layerController.regionTableController.model.get(regionIdentifier));

										ImageLayerTile.drawArea(graphics, regionEntry, 0, 0, 16);
										ImageLayerTile.drawBorder(graphics, regionEntry, 0, 0, 16, true, true, true, true);
										ImageLayerTile.drawIdentifier(image, regionEntry, 0, 0, 16, fontRenderer);
										ImageLayerTile.drawGrid(graphics, 0, 0, 16);
									}
									label.setIcon(new ImageIcon(image));

									Color background;
									Color foreground;

									JList.DropLocation dropLocation = list.getDropLocation();
									if (dropLocation != null
										&& !dropLocation.isInsert()
										&& dropLocation.getIndex() == index) {
										background = Color.BLUE;
										foreground = Color.WHITE;
									} else if (isSelected) {
										background = list.getSelectionBackground();
										foreground = list.getSelectionForeground();
									} else {
										background = list.getBackground();
										foreground = list.getForeground();
									}

									label.setBackground(background);
									label.setForeground(foreground);

									return label;
								}
							});
							canvasMap.layerController.regionTableController.epChangedState.register(() -> {
								modelTableRegion.clear();
								modelTableRegion.addElement(Optional.empty());
								for (RegionIdentifier regionIdentifier : canvasMap.layerController.regionTableController.model.getKeys()) {
									modelTableRegion.addElement(Optional.of(regionIdentifier));
								}

								updateSelection(canvasMap.getTileCurrent());
							});
						}), 300, 600),

						// 操作ボタン
						createPanelFlow(

							createButton(localize("GuiRegionEditor.button.actionCreateRegion"), actionCreateRegion),

							createButton(localize("GuiRegionEditor.button.actionEditRegion"), actionEditRegion),

							createButton(localize("GuiRegionEditor.button.actionDeleteRegion"), actionDeleteRegion),

							createButton(localize("GuiRegionEditor.button.actionChangeRegionIdentifier"), actionChangeRegionIdentifier)

						)

					)

				)

			),

			panelResult = new PanelResult(windowWrapper, i18n)

		);
		windowWrapper.setContentPane(get(component, c -> {
			c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).setParent(inputMap);
			c.getActionMap().setParent(actionMap);
		}));

		// 初期化
		listenersPreInit.forEach(Runnable::run);
		setPosition(0, 0);
		canvasMap.init();

	}

	private void plusBrushSize(int dBrushSize)
	{
		canvasMap.setBrushSize(Math.max(Math.min(canvasMap.getBrushSize() + dBrushSize, 100), 0));
	}

	private void updateSelection(Optional<RegionIdentifier> tileCurrent)
	{
		tableRegion.getSelectionModel().clearSelection();
		Enumeration<Optional<RegionIdentifier>> elements = modelTableRegion.elements();
		int i = 0;
		while (elements.hasMoreElements()) {
			Optional<RegionIdentifier> tile = elements.nextElement();
			if (tile.equals(tileCurrent)) {
				tableRegion.setSelectedIndex(i);
				break;
			}
			i++;
		}
	}

	private void loadMapFromLocal()
	{
		FileDialog fileDialog;
		if (windowWrapper.frame != null) {
			fileDialog = new FileDialog(windowWrapper.frame, "Open Map Image", FileDialog.LOAD);
		} else {
			fileDialog = new FileDialog(windowWrapper.dialog, "Open Map Image", FileDialog.LOAD);
		}
		fileDialog.setDirectory(".");
		fileDialog.setVisible(true);
		if (fileDialog.getFile() == null) return;
		File file = new File(fileDialog.getDirectory(), fileDialog.getFile());

		try (FileInputStream in = new FileInputStream(file)) {
			loadMap(in, file.getName());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadMapFromURL() // TODO キャッシュ
	{
		GuiUrl gui = new GuiUrl(windowWrapper, i18n);
		gui.oValidator = Optional.of(result -> {
			try (InputStream in = result.url.openStream()) {
				loadMap(in, new File(result.uri.getPath()).getName());
				return true;
			} catch (IOException e1) {
				e1.printStackTrace();
				gui.setException(e1);
				return false;
			}
		});
		gui.show();
	}

	private void loadMap(InputStream in, String fileName) throws IOException
	{
		BufferedImage image = ImageIO.read(in);

		if (image == null) {
			throw new IOException("Invalid image type");
		}

		java.awt.Point mapOrigin = new java.awt.Point(0, 0);
		{
			Matcher matcher = Pattern.compile("\\.X([0-9]+)Z([0-9]+)\\.png\\Z").matcher(fileName);
			if (matcher.find()) {
				mapOrigin.x = Integer.parseInt(matcher.group(1), 10);
				mapOrigin.y = Integer.parseInt(matcher.group(2), 10);
			}
		}

		canvasMap.setMapImageProvider(new MapImageProviderBufferedImage(image, mapOrigin));
	}

	private void createRegion()
	{

		// ダイアログ表示
		GuiRegionIdentifier gui = new GuiRegionIdentifier(
			windowWrapper,
			i18n,
			canvasMap.getTileCurrent().map(t -> t.countryId).orElse(""),
			"");
		gui.oValidator = Optional.of(ri -> {

			// 構文チェック
			if (!ri.countryId.matches("[a-zA-Z0-9]{1,4}")) {
				gui.setText(localize("GuiRegionEditor.actionCreateRegion.messageInvalidCountryId"), "", PanelResult.EXCEPTION);
				return false;
			}
			if (!ri.stateId.matches("[a-zA-Z0-9]{1,4}")) {
				gui.setText(localize("GuiRegionEditor.actionCreateRegion.messageInvalidStateId"), "", PanelResult.EXCEPTION);
				return false;
			}

			// 既に存在しないか
			if (canvasMap.layerController.regionTableController.model.containsKey(ri)) {
				gui.setText(localize("GuiRegionEditor.actionCreateRegion.messageAlreadyExists"), "", PanelResult.EXCEPTION);
				return false;
			}

			return true;
		});
		gui.show();
		if (gui.oResult.isPresent()) {

			// 地域表の更新
			Optional<RegionIdentifier> tileCurrent = canvasMap.getTileCurrent();
			RegionInfo regionInfo = tileCurrent.isPresent() ? canvasMap.layerController.regionTableController.model.get(tileCurrent.get()) : RegionInfo.DEFAULT;
			if (tileCurrent.isPresent() && canvasMap.layerController.regionTableController.model.containsKey(tileCurrent.get())) {

				// 古い領域が既にある場合はその1個下に作成
				try {
					canvasMap.layerController.regionTableController.model.insert(tileCurrent.get(), gui.oResult.get(), regionInfo);
				} catch (ModelException e) {
					e.printStackTrace();
					GuiMessage.showException(e);
					return;
				}
				canvasMap.layerController.regionTableController.epChangedState.trigger().run();
				canvasMap.update();

			} else {

				// 一番上に作成
				try {
					canvasMap.layerController.regionTableController.model.insertFirst(gui.oResult.get(), regionInfo);
				} catch (ModelException e) {
					e.printStackTrace();
					GuiMessage.showException(e);
					return;
				}
				canvasMap.layerController.regionTableController.epChangedState.trigger().run();
				canvasMap.update();

			}

		}
	}

	private void deleteRegion()
	{

		// 古い領域
		Optional<RegionIdentifier> tileCurrent = canvasMap.getTileCurrent();

		// 選択チェック
		if (!tileCurrent.isPresent()) {
			panelResult.setText(localize("GuiRegionEditor.actionDeleteRegion.messageNoRegion"), "", PanelResult.EXCEPTION);
			return;
		}

		// リストアップされていない領域を選択しているなら中止
		if (!canvasMap.layerController.regionTableController.model.containsKey(tileCurrent.get())) {
			panelResult.setText(localize("GuiRegionEditor.actionDeleteRegion.messageInvalidSelection"), "", PanelResult.EXCEPTION);
			return;
		}

		// 地図も更新しますか
		boolean updateMap;
		{
			int result = JOptionPane.showConfirmDialog(
				windowWrapper.getWindow(),
				localize("GuiRegionEditor.actionDeleteRegion.confirmation.message"),
				localize("GuiRegionEditor.actionDeleteRegion.confirmation.title"),
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE);
			if (result == JOptionPane.YES_OPTION) {
				updateMap = true;
			} else if (result == JOptionPane.NO_OPTION) {
				updateMap = false;
			} else {
				panelResult.setText(localize("GuiRegionEditor.actionDeleteRegion.messageCanceled"), "", PanelResult.WARNING);
				return;
			}
		}

		// 地域表の更新
		try {
			canvasMap.layerController.regionTableController.model.remove(tileCurrent.get());
		} catch (ModelException e1) {
			e1.printStackTrace();
			GuiMessage.showException(e1);
			return;
		}
		canvasMap.layerController.regionTableController.epChangedState.trigger().run();
		canvasMap.setTileCurrent(Optional.empty());

		// 地図の更新
		if (updateMap) {
			canvasMap.layerController.tileMapController.model.getEntries()
				.filter(e -> e.y.equals(tileCurrent.get()))
				.toImmutableArray()
				.suppliterator()
				.forEach(e -> {
					canvasMap.layerController.tileMapController.model.setTile(e.x, Optional.empty());
				});
			canvasMap.layerController.tileMapController.epChangedTileUnspecified.trigger().run();
			canvasMap.layerController.tileMapController.epChangedState.trigger().run();
		}

		canvasMap.update();
	}

	private void changeRegionIdentifier()
	{

		// 古い領域
		Optional<RegionIdentifier> tileCurrent = canvasMap.getTileCurrent();

		// 選択チェック
		if (!tileCurrent.isPresent()) {
			panelResult.setText(localize("GuiRegionEditor.actionChangeRegionIdentifier.messageNoRegion"), "", PanelResult.EXCEPTION);
			return;
		}

		// リストアップされていない領域を選択しているなら中止
		if (!canvasMap.layerController.regionTableController.model.containsKey(tileCurrent.get())) {
			panelResult.setText(localize("GuiRegionEditor.actionChangeRegionIdentifier.messageInvalidSelection"), "", PanelResult.EXCEPTION);
			return;
		}

		// ダイアログ表示
		Optional<RegionIdentifier> tileNew;
		Struct1<Boolean> sUpdateMap = new Struct1<>();
		{
			GuiRegionIdentifier gui = new GuiRegionIdentifier(
				windowWrapper,
				i18n,
				tileCurrent.get().countryId,
				tileCurrent.get().stateId);
			gui.oValidator = Optional.of(ri -> {

				// 構文チェック
				if (!ri.countryId.matches("[a-zA-Z0-9]{1,4}")) {
					gui.setText(localize("GuiRegionEditor.actionChangeRegionIdentifier.messageInvalidCountryId"), "", PanelResult.EXCEPTION);
					return false;
				}
				if (!ri.stateId.matches("[a-zA-Z0-9]{1,4}")) {
					gui.setText(localize("GuiRegionEditor.actionChangeRegionIdentifier.messageInvalidStateId"), "", PanelResult.EXCEPTION);
					return false;
				}

				// 既に存在しないか
				if (canvasMap.layerController.regionTableController.model.containsKey(ri)) {
					gui.setText(localize("GuiRegionEditor.actionChangeRegionIdentifier.messageAlreadyExists"), "", PanelResult.EXCEPTION);
					return false;
				}

				// 地図も更新しますか
				{
					int result = JOptionPane.showConfirmDialog(
						windowWrapper.getWindow(),
						localize("GuiRegionEditor.actionChangeRegionIdentifier.confirmation.message"),
						localize("GuiRegionEditor.actionChangeRegionIdentifier.confirmation.title"),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE);
					if (result == JOptionPane.YES_OPTION) {
						sUpdateMap.x = true;
					} else if (result == JOptionPane.NO_OPTION) {
						sUpdateMap.x = false;
					} else {
						gui.setText(localize("GuiRegionEditor.actionChangeRegionIdentifier.messageCanceled"), "", PanelResult.WARNING);
						return false;
					}
				}

				return true;
			});
			gui.show();
			if (gui.oResult.isPresent()) {
				tileNew = gui.oResult;
			} else {
				return;
			}
		}

		// 地域表の更新
		try {
			canvasMap.layerController.regionTableController.model.replaceKey(tileCurrent.get(), tileNew.get());
		} catch (ModelException e) {
			e.printStackTrace();
			GuiMessage.showException(e);
			return;
		}
		canvasMap.layerController.regionTableController.epChangedState.trigger().run();
		canvasMap.setTileCurrent(tileNew);

		// 地図の更新
		if (sUpdateMap.x) {
			canvasMap.layerController.tileMapController.model.getEntries()
				.filter(e -> e.y.equals(tileCurrent.get()))
				.toImmutableArray()
				.suppliterator()
				.forEach(e -> {
					canvasMap.layerController.tileMapController.model.setTile(e.x, tileNew);
				});
			canvasMap.layerController.tileMapController.epChangedTileUnspecified.trigger().run();
			canvasMap.layerController.tileMapController.epChangedState.trigger().run();
		}

		canvasMap.update();
	}

	private void setPosition(int x, int z)
	{
		canvasMap.setPosition(x, z);
		labelCoordX.setText("" + canvasMap.getPositionX() * 16);
		labelCoordZ.setText("" + canvasMap.getPositionZ() * 16);
		labelTileX.setText("" + canvasMap.getPositionX());
		labelTileZ.setText("" + canvasMap.getPositionZ());
	}

	private void scroll(int x, int z)
	{
		setPosition(canvasMap.getPositionX() + x, canvasMap.getPositionZ() + z);
	}

	private void scrollToRegion(RegionIdentifier regionIdentifier)
	{
		ArrayList<TileCoordinate> list = canvasMap.layerController.tileMapController.model.getKeys().stream()
			.filter(cp -> regionIdentifier.equals(canvasMap.layerController.tileMapController.model.getTile(cp).orElse(null)))
			.collect(Collectors.toCollection(ArrayList::new));
		if (list.size() <= 0) return;

		setPosition(
			(int) Math.floor(list.stream()
				.mapToInt(cp -> cp.x)
				.average()
				.getAsDouble()),
			(int) Math.floor(list.stream()
				.mapToInt(cp -> cp.z)
				.average()
				.getAsDouble()));
	}

}
