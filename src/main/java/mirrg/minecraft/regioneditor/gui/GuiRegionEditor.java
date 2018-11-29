package mirrg.minecraft.regioneditor.gui;

import static mirrg.minecraft.regioneditor.gui.SwingUtils.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import mirrg.minecraft.regioneditor.data.IRegionTableListener;
import mirrg.minecraft.regioneditor.data.RegionEntry;
import mirrg.minecraft.regioneditor.data.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.TileIndex;
import mirrg.minecraft.regioneditor.gui.CanvasMap.ICanvasMapListener;
import mirrg.minecraft.regioneditor.gui.GuiData.IDialogDataListener;

public class GuiRegionEditor extends GuiBase
{

	private Optional<Consumer<List<String>>> oSender;

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
	private JSpinner spinnerBrushSize;
	private SpinnerNumberModel modelSpinnerBrushSize;
	private JList<Optional<RegionIdentifier>> tableRegion;
	private DefaultListModel<Optional<RegionIdentifier>> modelTableRegion;

	public GuiRegionEditor(WindowWrapper owner, Optional<Consumer<List<String>>> oSender)
	{
		super(owner, "RegionEditor", ModalityType.MODELESS);
		this.oSender = oSender;
	}

	@Override
	protected void initComponenets()
	{
		ArrayList<Runnable> listenersPreInit = new ArrayList<>();

		try (InputStream in = GuiRegionEditor.class.getResourceAsStream("icon2.png")) {
			if (in != null) {
				windowWrapper.getWindow().setIconImage(ImageIO.read(in));
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		{
			inputMap = new InputMap();
			actionMap = new ActionMap();

			actionOpenGuiData = new ActionBuilder<>(new ActionButton(e -> new GuiData(windowWrapper, new IDialogDataListener() {
				@Override
				public void onImport(String string)
				{
					try {
						canvasMap.setExpression(string);
					} catch (Exception e) {
						e.printStackTrace();
					}
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
				.value(Action.NAME, "Open Import/Export Window(I)...")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_I)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK))
				.register();
			actionOpenGuiCommand = new ActionBuilder<>(new ActionButton(e -> {
				new GuiCommand(windowWrapper, canvasMap.possessionMapModel.getDataReader().getAreas(), oSender).show();
			}))
				.value(Action.NAME, "Open Dynmap Command Window(D)...")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_D)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK))
				.register();

			actionLoadMapFromLocalFile = new ActionBuilder<>(new ActionButton(e -> loadMapFromLocal()))
				.value(Action.NAME, "Load Map From Local File(F)...")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_F)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK))
				.register();
			actionLoadMapFromUrl = new ActionBuilder<>(new ActionButton(e -> {
				GuiUrl guiUrl = new GuiUrl(windowWrapper);
				guiUrl.show();
				if (guiUrl.ok) {
					try (InputStream in = guiUrl.url.openStream()) {
						loadMapFrom(in, new File(guiUrl.uri.getPath()).getName());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}))
				.value(Action.NAME, "Load Map From URL(U)...")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_U)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK))
				.register();

			actionScrollLeft = new ActionBuilder<>(new ActionButton(e -> scroll(-4, 0)))
				.value(Action.NAME, "Scroll Left(L)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_L)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0))
				.register();
			actionScrollRight = new ActionBuilder<>(new ActionButton(e -> scroll(4, 0)))
				.value(Action.NAME, "Scroll Right(R)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_R)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0))
				.register();
			actionScrollUp = new ActionBuilder<>(new ActionButton(e -> scroll(0, -4)))
				.value(Action.NAME, "Scroll Up(U)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_U)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0))
				.register();
			actionScrollDown = new ActionBuilder<>(new ActionButton(e -> scroll(0, 4)))
				.value(Action.NAME, "Scroll Down(D)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_D)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0))
				.register();
			{
				ActionToggle action = actionToggleShowMap = new ActionBuilder<>(new ActionToggle(v -> canvasMap.setShowMap(v)))
					.value(Action.NAME, "Show Map(M)")
					.value(Action.MNEMONIC_KEY, KeyEvent.VK_M)
					.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0))
					.register();
				listenersPreInit.add(() -> action.setSelected(true));
			}
			{
				ActionToggle action = actionToggleShowTile = new ActionBuilder<>(new ActionToggle(v -> canvasMap.setShowTile(v)))
					.value(Action.NAME, "Show Tile(T)")
					.value(Action.MNEMONIC_KEY, KeyEvent.VK_T)
					.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0))
					.register();
				listenersPreInit.add(() -> action.setSelected(true));
			}
			{
				ActionToggle action = actionToggleShowTooltip = new ActionBuilder<>(new ActionToggle(v -> canvasMap.setShowTooltip(v)))
					.value(Action.NAME, "Show Tooltip(T)")
					.value(Action.MNEMONIC_KEY, KeyEvent.VK_T)
					.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0))
					.register();
				listenersPreInit.add(() -> action.setSelected(true));
			}
			{
				ActionToggle action = actionToggleShowArea = new ActionBuilder<>(new ActionToggle(v -> canvasMap.setShowArea(v)))
					.value(Action.NAME, "Show Area(A)")
					.value(Action.MNEMONIC_KEY, KeyEvent.VK_A)
					.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0))
					.register();
				listenersPreInit.add(() -> action.setSelected(true));
			}
			{
				ActionToggle action = actionToggleShowBorder = new ActionBuilder<>(new ActionToggle(v -> canvasMap.setShowBorder(v)))
					.value(Action.NAME, "Show Border(B)")
					.value(Action.MNEMONIC_KEY, KeyEvent.VK_B)
					.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0))
					.register();
				listenersPreInit.add(() -> action.setSelected(true));
			}
			{
				ActionToggle action = actionToggleShowIdentifier = new ActionBuilder<>(new ActionToggle(v -> canvasMap.setShowIdentifier(v)))
					.value(Action.NAME, "Show Identifier(I)")
					.value(Action.MNEMONIC_KEY, KeyEvent.VK_I)
					.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0))
					.register();
				listenersPreInit.add(() -> action.setSelected(true));
			}
			{
				ActionToggle action = actionToggleShowGrid = new ActionBuilder<>(new ActionToggle(v -> canvasMap.setShowGrid(v)))
					.value(Action.NAME, "Show Grid(G)")
					.value(Action.MNEMONIC_KEY, KeyEvent.VK_G)
					.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0))
					.register();
				listenersPreInit.add(() -> action.setSelected(true));
			}

			List<ActionRadio> groupTool = new ArrayList<>();
			actionToolNothing = new ActionBuilder<>(new ActionRadio(groupTool, v -> {
				canvasMap.setTool(Optional.of(new ToolNothing(canvasMap.getToolContext())));
			}))
				.value(Action.NAME, "Do Nothing(N)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_N)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0))
				.register();
			actionToolPencil = new ActionBuilder<>(new ActionRadio(groupTool, v -> {
				canvasMap.setTool(Optional.of(new ToolPencil(canvasMap.getToolContext())));
			}))
				.value(Action.NAME, "Pencil Tool(P)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_P)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0))
				.register();
			actionToolBrush = new ActionBuilder<>(new ActionRadio(groupTool, v -> {
				canvasMap.setTool(Optional.of(new ToolBrush(canvasMap.getToolContext())));
			}))
				.value(Action.NAME, "Brush Tool(B)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_B)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_B, 0))
				.register();
			actionToolFill = new ActionBuilder<>(new ActionRadio(groupTool, v -> {
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
				.value(Action.NAME, "Fill Tool(F)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_F)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0))
				.register();
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
				.value(Action.NAME, "Spuit Tool(K)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_K)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_K, 0))
				.register();
			listenersPreInit.add(() -> actionToolBrush.setSelected(true));
			actionIncrementBrushSize = new ActionBuilder<>(new ActionButton(e -> canvasMap.setBrushSize(Math.max(Math.min(canvasMap.getBrushSize() + 1, 100), 1))))
				.value(Action.NAME, "Increment Brush Size(I)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_I)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0))
				.register();
			actionDecrementBrushSize = new ActionBuilder<>(new ActionButton(e -> canvasMap.setBrushSize(Math.max(Math.min(canvasMap.getBrushSize() - 1, 100), 1))))
				.value(Action.NAME, "Decrement Brush Size(D)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_L)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0))
				.register();

			actionClearMap = new ActionBuilder<>(new ActionButton(e -> {
				if (JOptionPane.showConfirmDialog(
					windowWrapper.getWindow(),
					"All tiles on the map will be blank",
					"Clear Map",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {

					for (TileIndex tileIndex : canvasMap.possessionMapModel.tileMapModel.getDataReader().getKeys().toCollection()) {
						canvasMap.possessionMapModel.tileMapModel.set(tileIndex, Optional.empty());
					}
					canvasMap.update();

				}
			}))
				.value(Action.NAME, "Clear Map(C)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_C)
				.register();

			actionCreateRegion = new ActionBuilder<>(new ActionButton(e -> {
				System.out.println("A"); // TODO
			}))
				.value(Action.NAME, "Create New Region(N)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_N)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK))
				.register();
			actionEditRegion = new ActionBuilder<>(new ActionButton(e -> {
				System.out.println("B"); // TODO
			}))
				.value(Action.NAME, "Edit Region(E)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_E)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.SHIFT_DOWN_MASK))
				.register();
			actionDeleteRegion = new ActionBuilder<>(new ActionButton(e -> {
				Optional<RegionIdentifier> oRegionIdentifier = canvasMap.getCurrentRegionIdentifier();
				if (oRegionIdentifier.isPresent()) {
					canvasMap.possessionMapModel.regionTableModel.remove(oRegionIdentifier.get());
					canvasMap.update();
				}
			}))
				.value(Action.NAME, "Delete Region(D)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_D)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK))
				.register();
			actionChangeRegionIdentifier = new ActionBuilder<>(new ActionButton(e -> {
				System.out.println("D"); // TODO
			}))
				.value(Action.NAME, "Change Region Identifier(I)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_I)
				.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK))
				.register();
			actionScrollToRegion = new ActionBuilder<>(new ActionButton(e -> {
				Optional<RegionIdentifier> oRegionIdentifier = canvasMap.getCurrentRegionIdentifier();
				if (oRegionIdentifier.isPresent()) {
					scrollToRegion(oRegionIdentifier.get());
				}
			}))
				.value(Action.NAME, "Scroll To Region(S)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_S)
				.register();
		}

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

			class MenuItem extends JMenuItem
			{

				public MenuItem(Action action)
				{
					super(action);
				}

				@Override
				protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed)
				{
					return false;
				}

			}

			class CheckBoxMenuItem extends JCheckBoxMenuItem
			{

				public CheckBoxMenuItem(Action action)
				{
					super(action);
				}

				@Override
				protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed)
				{
					return false;
				}

				@Override
				protected ItemListener createItemListener()
				{
					return new ItemListener() {
						@Override
						public void itemStateChanged(ItemEvent event)
						{
							fireItemStateChanged(event);
						}
					};
				}

			}

			JMenuBar menuBar = get(new JMenuBar(), menuBar2 -> {
				menuBar2.add(get(new Menu("Data(D)"), menu -> {
					menu.setMnemonic(KeyEvent.VK_D);
					menu.add(new MenuItem(actionOpenGuiData));
					menu.add(new MenuItem(actionOpenGuiCommand));
				}));
				menuBar2.add(get(new Menu("View(V)"), menu -> {
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
				menuBar2.add(get(new Menu("Map(M)"), menu -> {
					menu.setMnemonic(KeyEvent.VK_M);
					menu.add(new MenuItem(actionClearMap));
				}));
				menuBar2.add(get(new Menu("Tool(T)"), menu -> {
					menu.setMnemonic(KeyEvent.VK_T);
					menu.add(new CheckBoxMenuItem(actionToolNothing));
					menu.addSeparator();
					menu.add(new CheckBoxMenuItem(actionToolPencil));
					menu.add(new CheckBoxMenuItem(actionToolBrush));
					menu.add(new CheckBoxMenuItem(actionToolFill));
					menu.add(new CheckBoxMenuItem(actionToolSpuit));
					menu.addSeparator();
					menu.add(new MenuItem(actionIncrementBrushSize));
					menu.add(new MenuItem(actionDecrementBrushSize));
				}));
				menuBar2.add(get(new Menu("Region(R)"), menu -> {
					menu.setMnemonic(KeyEvent.VK_R);
					menu.add(new MenuItem(actionCreateRegion));
					menu.add(new MenuItem(actionEditRegion));
					menu.add(new MenuItem(actionDeleteRegion));
					menu.addSeparator();
					menu.add(new MenuItem(actionChangeRegionIdentifier));
					menu.addSeparator();
					menu.add(new MenuItem(actionScrollToRegion));
				}));
			});
			if (windowWrapper.frame != null) {
				windowWrapper.frame.setJMenuBar(menuBar);
			} else if (windowWrapper.dialog != null) {
				windowWrapper.dialog.setJMenuBar(menuBar);
			}
		}

		windowWrapper.setContentPane(get(splitPaneHorizontal(0.7,

			borderPanelDown(

				borderPanelDown(

					// 左ペイン：地図側
					borderPanelUp(

						get(button("↑", actionScrollUp), c -> {
							c.setMargin(new Insets(0, 0, 0, 0));
							c.setPreferredSize(new Dimension(32, 32));
						}),

						borderPanelDown(

							borderPanelLeft(

								get(button("←", actionScrollLeft), c -> {
									c.setMargin(new Insets(0, 0, 0, 0));
									c.setPreferredSize(new Dimension(32, 32));
								}),

								borderPanelRight(

									// 地図
									canvasMap = get(new CanvasMap(new ICanvasMapListener() {
										@Override
										public void onCurrentRegionIdentifierChange(Optional<RegionIdentifier> oCurrentRegionIdentifier)
										{
											updateSelection(oCurrentRegionIdentifier);
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

									get(button("→", actionScrollRight), c -> {
										c.setMargin(new Insets(0, 0, 0, 0));
										c.setPreferredSize(new Dimension(32, 32));
									})

								)

							),

							get(button("↓", actionScrollDown), c -> {
								c.setMargin(new Insets(0, 0, 0, 0));
								c.setPreferredSize(new Dimension(32, 32));
							})

						)

					),

					flowPanel(

						new JLabel("Coord:"),

						labelCoordX = new JLabel("???"),

						new JLabel(","),

						labelCoordZ = new JLabel("???"),

						new JLabel("Tile:"),

						labelTileX = new JLabel("???"),

						new JLabel(","),

						labelTileZ = new JLabel("???")

					)

				),

				flowPanel(

					new JLabel("X:"),

					textFieldX = get(new JFormattedTextField(NumberFormat.getIntegerInstance()), c -> {
						c.setValue(0);
						c.setColumns(8);
						c.setHorizontalAlignment(JTextField.RIGHT);
					}),

					new JLabel("Z:"),

					textFieldZ = get(new JFormattedTextField(NumberFormat.getIntegerInstance()), c -> {
						c.setValue(0);
						c.setColumns(8);
						c.setHorizontalAlignment(JTextField.RIGHT);
					}),

					button("Jump to position", e -> {
						setPosition(
							((Number) textFieldX.getValue()).intValue() / 16,
							((Number) textFieldZ.getValue()).intValue() / 16);
					}),

					button("Jump to tile position", e -> {
						setPosition(
							((Number) textFieldX.getValue()).intValue(),
							((Number) textFieldZ.getValue()).intValue());
					})

				)

			),

			// 右ペイン：領地リストとか操作ボタンとか
			borderPanelUp(

				// ブラシサイズ
				flowPanel(

					new JLabel("Brush:"),

					spinnerBrushSize = new JSpinner(modelSpinnerBrushSize = get(new SpinnerNumberModel(7, 1, 100, 1), c -> {
						c.addChangeListener(e -> {
							canvasMap.setBrushSize(c.getNumber().intValue());
						});
						listenersPreInit.add(() -> canvasMap.setBrushSize(modelSpinnerBrushSize.getNumber().intValue()));
					}))

				),

				borderPanelDown(

					// 領地一覧
					scrollPane(tableRegion = get(new JList<>(modelTableRegion = new DefaultListModel<>()), c -> {
						c.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						c.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseReleased(MouseEvent e)
							{
								if (e.getButton() == MouseEvent.BUTTON1) {
									canvasMap.setCurrentRegionIdentifier(getSelectedItem());
								} else if (e.getButton() == MouseEvent.BUTTON3) {

									int index = c.locationToIndex(e.getPoint());
									if (index < 0) return;
									if (index >= c.getModel().getSize()) return;
									Optional<RegionIdentifier> oRegionIdentifier = c.getModel().getElementAt(index);
									if (!oRegionIdentifier.isPresent()) return;

									scrollToRegion(oRegionIdentifier.get());

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
									label.setText(new RegionEntry(
										regionIdentifier,
										canvasMap.possessionMapModel.regionTableModel.getDataReader().get(regionIdentifier)).toString());
								} else {
									label.setText("Empty");
								}

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
						canvasMap.possessionMapModel.regionTableModel.addListener(new IRegionTableListener() {
							@Override
							public void onChange()
							{
								modelTableRegion.clear();
								modelTableRegion.addElement(Optional.empty());
								for (RegionIdentifier regionIdentifier : canvasMap.possessionMapModel.regionTableModel.getDataReader().getKeys()) {
									modelTableRegion.addElement(Optional.of(regionIdentifier));
								}

								updateSelection(canvasMap.getCurrentRegionIdentifier());
							}
						});
					}), 300, 600),

					// 操作ボタン
					flowPanel(

						button("New", actionCreateRegion),

						button("Edit", actionEditRegion),

						button("Delete", actionDeleteRegion),

						button("Change ID", actionChangeRegionIdentifier),

						button("B", e -> {
							try {
								String[] input = {
									"1111111",
									"1000001",
									"1011101",
									"1010101",
									"1011101",
									"1000001",
									"1111111",
								};
								//処理待ちキュー
								ArrayDeque<Point> wait = new ArrayDeque<Point>();
								//Char[]データ配列
								List<char[]> dispos = new ArrayList<char[]>();
								//inputのStringをchar[]に変換
								for (int i = 0; i < input.length; i++)
									dispos.add(input[i].toCharArray());
								//1の座標を記録する["x,y"]
								List<String> results = new ArrayList<String>();
								//
								for (int y = 0; y < dispos.size(); y++) {
									//yの行を取得
									char[] chars = dispos.get(y);
									//y行を一文字ずつ調査
									for (int x = 0; x < chars.length; x++) {
										//その文字が1ならば
										if (chars[x] == '1') {
											//処理待ちキューに追加
											wait.addFirst(new Point(x, y));
											dispos.get(y)[x] = '0'; //処理待ちキューに入れた座標を0にする
											//幅優先探索の開始
											while (!wait.isEmpty()) {
												//処理待ちの取り出して削除
												Point pos = wait.removeLast();
												//結果に座標を記録する
												results.add(pos.X + "," + pos.Y);

												//上下左右の調査の開始
												if (pos.X + 1 < dispos.size())
													if (dispos.get(pos.X + 1)[pos.Y] == '1') {
													wait.addFirst(new Point(pos.Y, pos.X + 1)); //もしも右が1なら処理待ちキューに追加
													dispos.get(pos.X + 1)[pos.Y] = '0'; //処理待ちキューに入れた座標を0にする
												}

												if (pos.X - 1 >= 0)
													if (dispos.get(pos.X - 1)[pos.Y] == '1') {
													wait.addFirst(new Point(pos.Y, pos.X - 1)); //もしも左が1なら   "
													dispos.get(pos.X - 1)[pos.Y] = '0'; // "
												}

												if (pos.Y + 1 < dispos.get(pos.X).length)
													if (dispos.get(pos.X)[pos.Y + 1] == '1') {
													wait.addFirst(new Point(pos.Y + 1, pos.X)); //もしも下が1なら
													dispos.get(pos.X)[pos.Y + 1] = '0'; // "
												}

												if (pos.Y - 1 >= 0)
													if (dispos.get(pos.X)[pos.Y - 1] == '1') {
													wait.addFirst(new Point(pos.Y + 1, pos.X)); //もしも上が1なら   "
													dispos.get(pos.X)[pos.Y - 1] = '0'; // "
												}
												//上下左右の調査の終了
												//もしもまだ処理待ちのキューが存在するならこのループは抜けられない
											}
										}
									}
								}
								for (int i = 0; i < results.size(); i++)
									System.out.println(results.get(i));
							} catch (Exception er) {
								er.printStackTrace();
							}

						}),

						button("C", e -> {

						}),

						button("D", e -> {
							String[] input = {
								"001110",
								"011010",
								"010110",
								"011100",
							};

							for (int i = 0; i < input.length; i++) {
								for (int j = 0; j < input[i].length(); j++) {
									if (input[i].toCharArray()[j] == '1') System.out.println(i + "," + j);
								}
							}

						})

					)

				)

			)

		), c -> {
			c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).setParent(inputMap);
			c.getActionMap().setParent(actionMap);
		}));

		listenersPreInit.forEach(Runnable::run);
		setPosition(0, 0);
		canvasMap.init();
	}

	private void updateSelection(Optional<RegionIdentifier> oCurrentRegionIdentifier)
	{
		tableRegion.getSelectionModel().clearSelection();
		Enumeration<Optional<RegionIdentifier>> elements = modelTableRegion.elements();
		int i = 0;
		while (elements.hasMoreElements()) {
			Optional<RegionIdentifier> oRegionIdentifier = elements.nextElement();
			if (oRegionIdentifier.equals(oCurrentRegionIdentifier)) {
				tableRegion.setSelectedIndex(i);
				break;
			}
			i++;
		}
	}

	private class ActionBuilder<A extends ActionBase>
	{

		private final A action;

		public ActionBuilder(A action)
		{
			this.action = action;
		}

		public ActionBuilder<A> value(String key, Object value)
		{
			action.putValue(key, value);
			return this;
		}

		public ActionBuilder<A> keyStroke(KeyStroke keyStrokeMain, KeyStroke... keyStrokes)
		{
			action.putValue(Action.ACCELERATOR_KEY, keyStrokeMain);
			inputMap.put(keyStrokeMain, action);
			for (KeyStroke keyStroke : keyStrokes) {
				inputMap.put(keyStroke, action);
			}
			return this;
		}

		public A register()
		{
			actionMap.put(action, action);
			action.register();
			return action;
		}

	}

	private abstract class ActionBase extends AbstractAction
	{

		public void register()
		{

		}

	}

	private class ActionButton extends ActionBase
	{

		private Consumer<ActionEvent> listener;

		public ActionButton(Consumer<ActionEvent> listener)
		{
			this.listener = listener;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			listener.accept(e);
		}

	}

	private abstract class ActionToggleBase extends ActionBase
	{

		public ActionToggleBase()
		{
			setSelected(false);
		}

		public boolean isSelected()
		{
			return (Boolean) getValue(SELECTED_KEY);
		}

		public void setSelected(boolean selected)
		{
			putValue(SELECTED_KEY, selected);
		}

	}

	private class ActionToggle extends ActionToggleBase
	{

		public ActionToggle(Consumer<Boolean> listener)
		{
			addPropertyChangeListener(e -> {
				if (e.getPropertyName().equals(SELECTED_KEY)) {
					listener.accept((Boolean) e.getNewValue());
				}
			});
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			setSelected(!isSelected());
		}

	}

	private class ActionRadio extends ActionToggleBase
	{

		private List<ActionRadio> group;

		public ActionRadio(List<ActionRadio> group, Consumer<Boolean> listener)
		{
			this.group = group;
			addPropertyChangeListener(e -> {
				if (e.getPropertyName().equals(SELECTED_KEY)) {
					listener.accept((Boolean) e.getNewValue());
				}
			});
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (!isSelected()) {
				for (ActionRadio actionRadio : group) {
					if (actionRadio.isSelected()) {
						actionRadio.setSelected(false);
					}
				}
				setSelected(true);
			}
		}

		@Override
		public void register()
		{
			group.add(this);
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
			loadMapFrom(in, file.getName());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadMapFrom(InputStream in, String fileName)
	{
		BufferedImage image;
		try {
			image = ImageIO.read(in);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		if (image == null) {
			System.err.println("画像の読み込みに失敗しました。");
			return;
		}

		java.awt.Point mapOrigin = new java.awt.Point(0, 0);
		{
			Matcher matcher = Pattern.compile("\\.X([0-9]+)Z([0-9]+)\\.png\\Z").matcher(fileName);
			if (matcher.find()) {
				mapOrigin.x = Integer.parseInt(matcher.group(1), 10);
				mapOrigin.y = Integer.parseInt(matcher.group(2), 10);
			}
		}

		canvasMap.setMap(image, mapOrigin);
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
		ArrayList<TileIndex> list = canvasMap.possessionMapModel.tileMapModel.getDataReader().getKeys().stream()
			.filter(cp -> regionIdentifier.equals(canvasMap.possessionMapModel.tileMapModel.getDataReader().get(cp).orElse(null)))
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

class Point
{
	int X;
	int Y;

	public Point(int x, int y)
	{
		X = x;
		Y = y;

	}
}

class Side extends Object
{

	Point Point1;
	Point Point2;

	public Side(Point p1, Point p2)
	{
		Point1 = p1;
		Point2 = p2;
	}

	@Override
	public boolean equals(Object obj)
	{
		//実装中
		return false;
	}

}
