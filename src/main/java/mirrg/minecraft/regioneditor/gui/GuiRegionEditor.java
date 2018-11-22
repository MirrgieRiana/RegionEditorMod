package mirrg.minecraft.regioneditor.gui;

import static mirrg.minecraft.regioneditor.gui.SwingUtils.*;

import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Insets;
import java.awt.event.ActionEvent;
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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
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
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import mirrg.minecraft.regioneditor.data.ChunkPosition;
import mirrg.minecraft.regioneditor.data.RegionEntry;
import mirrg.minecraft.regioneditor.data.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.RegionInfo;
import mirrg.minecraft.regioneditor.data.RegionInfoTable;
import mirrg.minecraft.regioneditor.gui.CanvasMap.ICanvasMapListener;
import mirrg.minecraft.regioneditor.gui.GuiData.IDialogDataListener;

public class GuiRegionEditor extends GuiBase
{

	private Optional<Consumer<List<String>>> oSender;

	private InputMap inputMap;
	private ActionMap actionMap;

	private Action actionOpenGuiData;
	private Action actionOpenGuiCommand;

	private Action actionLoadMapFromLocalFile;
	private Action actionLoadMapFromUrl;
	private Action actionScrollLeft;
	private Action actionScrollRight;
	private Action actionScrollUp;
	private Action actionScrollDown;
	private Action actionToggleShowMap;

	private Action actionClearMap;

	private Action actionCreateRegion;
	private Action actionEditRegion;
	private Action actionDeleteRegion;
	private Action actionChangeRegionIdentifier;
	private Action actionScrollToRegion;

	private CanvasMap canvasMap;
	private JLabel labelCoordX;
	private JLabel labelCoordZ;
	private JLabel labelChunkX;
	private JLabel labelChunkZ;
	private JFormattedTextField textFieldX;
	private JFormattedTextField textFieldZ;
	private JList<RegionEntry> tableRegionInfoTable;
	private DefaultListModel<RegionEntry> modelTableRegionInfoTable;

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

			class Action1 extends AbstractAction
			{

				private Consumer<ActionEvent> listener;

				public Action1(Consumer<ActionEvent> listener)
				{
					this.listener = listener;
				}

				public Action1 value(String key, Object value)
				{
					putValue(key, value);
					return this;
				}

				public Action1 key(KeyStroke keyStroke)
				{
					inputMap.put(keyStroke, this);
					return this;
				}

				public Action1 register()
				{
					actionMap.put(this, this);
					return this;
				}

				@Override
				public void actionPerformed(ActionEvent e)
				{
					listener.accept(e);
				}

			}

			class Action2 extends AbstractAction
			{

				private Consumer<ActionEvent> listener;

				public Action2(boolean defaultValue, Consumer<Boolean> listener)
				{
					value(SELECTED_KEY, defaultValue);
					addPropertyChangeListener(e -> {
						if (e.getPropertyName().equals(Action.SELECTED_KEY)) {
							listener.accept((Boolean) e.getNewValue());
						}
					});
				}

				public Action2 value(String key, Object value)
				{
					putValue(key, value);
					return this;
				}

				public Action2 key(KeyStroke keyStroke)
				{
					inputMap.put(keyStroke, this);
					return this;
				}

				public Action2 register()
				{
					actionMap.put(this, this);
					return this;
				}

				@Override
				public void actionPerformed(ActionEvent e)
				{

				}

			}

			//

			actionOpenGuiData = new Action1(e -> new GuiData(windowWrapper, new IDialogDataListener() {
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
			}).show())
				.value(Action.NAME, "Open Import/Export Window(I)...")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_I)
				.value(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK))
				.register();
			actionOpenGuiCommand = new Action1(e -> {
				new GuiCommand(windowWrapper, canvasMap.mapData.getAreas(), oSender).show();
			})
				.value(Action.NAME, "Open Dynmap Command Window(D)...")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_D)
				.value(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK))
				.register();

			actionLoadMapFromLocalFile = new Action1(e -> loadMapFromLocal())
				.value(Action.NAME, "Load Map From Local File(F)...")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_F)
				.value(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK))
				.register();
			actionLoadMapFromUrl = new Action1(e -> {
				GuiUrl guiUrl = new GuiUrl(windowWrapper);
				guiUrl.show();
				if (guiUrl.ok) {
					try (InputStream in = guiUrl.url.openStream()) {
						loadMapFrom(in, new File(guiUrl.uri.getPath()).getName());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			})
				.value(Action.NAME, "Load Map From URL(U)...")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_U)
				.value(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK))
				.register();

			actionScrollLeft = new Action1(e -> scroll(-4, 0))
				.value(Action.NAME, "Scroll Left(L)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_L)
				.value(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, 0))
				.key(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0))
				.register();
			actionScrollRight = new Action1(e -> scroll(4, 0))
				.value(Action.NAME, "Scroll Right(R)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_R)
				.value(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, 0))
				.key(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0))
				.register();
			actionScrollUp = new Action1(e -> scroll(0, -4))
				.value(Action.NAME, "Scroll Up(U)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_U)
				.value(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, 0))
				.key(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0))
				.register();
			actionScrollDown = new Action1(e -> scroll(0, 4))
				.value(Action.NAME, "Scroll Down(D)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_D)
				.value(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, 0))
				.key(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0))
				.register();
			actionToggleShowMap = new Action2(true, v -> canvasMap.setShowMap(v))
				.value(Action.NAME, "Show Map(M)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_M)
				.value(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0))
				.register();
			listenersPreInit.add(() -> canvasMap.setShowMap((Boolean) actionToggleShowMap.getValue(Action.SELECTED_KEY)));

			actionClearMap = new Action1(e -> {
				if (JOptionPane.showConfirmDialog(
					windowWrapper.getWindow(),
					"All tiles on the map will be blank",
					"Clear Map",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {

					ArrayList<ChunkPosition> list = new ArrayList<>(canvasMap.mapData.regionMap.getKeys());
					for (ChunkPosition chunkPosition : list) {
						canvasMap.mapData.regionMap.set(chunkPosition, Optional.empty());
					}
					canvasMap.update();

				}
			})
				.value(Action.NAME, "Clear Map(C)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_C)
				.register();

			actionCreateRegion = new Action1(e -> {
				System.out.println("A"); // TODO
			})
				.value(Action.NAME, "Create New Region(N)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_N)
				.value(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK))
				.register();
			actionEditRegion = new Action1(e -> {
				System.out.println("B"); // TODO
			})
				.value(Action.NAME, "Edit Region(E)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_E)
				.value(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.SHIFT_DOWN_MASK))
				.register();
			actionDeleteRegion = new Action1(e -> {
				System.out.println("C"); // TODO
			})
				.value(Action.NAME, "Delete Region(D)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_D)
				.value(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK))
				.register();
			actionChangeRegionIdentifier = new Action1(e -> {
				System.out.println("D"); // TODO
			})
				.value(Action.NAME, "Change Region Identifier(I)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_I)
				.value(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK))
				.register();
			actionScrollToRegion = new Action1(e -> {
				Optional<RegionEntry> oRegionEntry = getSelectedRegionEntry();
				if (oRegionEntry.isPresent()) {
					scrollToRegion(oRegionEntry.get());
				}
			})
				.value(Action.NAME, "Scroll To Region(S)")
				.value(Action.MNEMONIC_KEY, KeyEvent.VK_S)
				.register();
		}

		{
			JMenuBar menuBar = get(new JMenuBar(), menuBar2 -> {
				menuBar2.add(get(new JMenu("Data(D)"), menu -> {
					menu.setMnemonic(KeyEvent.VK_D);
					menu.add(new JMenuItem(actionOpenGuiData));
					menu.add(new JMenuItem(actionOpenGuiCommand));
				}));
				menuBar2.add(get(new JMenu("View(V)"), menu -> {
					menu.setMnemonic(KeyEvent.VK_V);
					menu.add(new JMenuItem(actionLoadMapFromLocalFile));
					menu.add(new JMenuItem(actionLoadMapFromUrl));
					menu.addSeparator();
					menu.add(new JMenuItem(actionScrollLeft));
					menu.add(new JMenuItem(actionScrollRight));
					menu.add(new JMenuItem(actionScrollUp));
					menu.add(new JMenuItem(actionScrollDown));
					menu.addSeparator();
					menu.add(new JCheckBoxMenuItem(actionToggleShowMap));
				}));
				menuBar2.add(get(new JMenu("Map(M)"), menu -> {
					menu.setMnemonic(KeyEvent.VK_M);
					menu.add(new JMenuItem(actionClearMap));
				}));
				menuBar2.add(get(new JMenu("Region(R)"), menu -> {
					menu.setMnemonic(KeyEvent.VK_R);
					menu.add(new JMenuItem(actionCreateRegion));
					menu.add(new JMenuItem(actionEditRegion));
					menu.add(new JMenuItem(actionDeleteRegion));
					menu.addSeparator();
					menu.add(new JMenuItem(actionChangeRegionIdentifier));
					menu.addSeparator();
					menu.add(new JMenuItem(actionScrollToRegion));
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
										public void onRegionInfoTableChange(RegionInfoTable regionInfoTable)
										{
											modelTableRegionInfoTable.clear();
											for (Entry<RegionIdentifier, RegionInfo> entry : regionInfoTable.entrySet()) {
												modelTableRegionInfoTable.addElement(new RegionEntry(entry.getKey(), entry.getValue()));
											}
										}

										@Override
										public void onRegionIdentifierCurrentChange(Optional<RegionIdentifier> oRegionIdentifierCurrent)
										{
											tableRegionInfoTable.getSelectionModel().clearSelection();
											Enumeration<RegionEntry> elements = modelTableRegionInfoTable.elements();
											int i = 0;
											while (elements.hasMoreElements()) {
												RegionEntry regionEntry = elements.nextElement();
												if (Optional.of(regionEntry.regionIdentifier).equals(oRegionIdentifierCurrent)) {
													tableRegionInfoTable.setSelectedIndex(i);
													break;
												}
												i++;
											}
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

						new JLabel("Chunk:"),

						labelChunkX = new JLabel("???"),

						new JLabel(","),

						labelChunkZ = new JLabel("???")

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

					button("Jump to chunk position", e -> {
						setPosition(
							((Number) textFieldX.getValue()).intValue(),
							((Number) textFieldZ.getValue()).intValue());
					})

				)

			),

			// 右ペイン：領地リストとか操作ボタンとか
			borderPanelDown(

				// 領地一覧
				scrollPane(tableRegionInfoTable = get(new JList<>(modelTableRegionInfoTable = new DefaultListModel<>()), c -> {
					c.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					c.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseReleased(MouseEvent e)
						{
							if (e.getButton() == MouseEvent.BUTTON1) {
								canvasMap.setRegionIdentifierCurrent(getSelectedRegionEntry().map(re -> re.regionIdentifier));
							} else if (e.getButton() == MouseEvent.BUTTON3) {

								int index = c.locationToIndex(e.getPoint());
								if (index < 0) return;
								if (index >= c.getModel().getSize()) return;
								RegionEntry regionEntry = c.getModel().getElementAt(index);

								scrollToRegion(regionEntry);

							}
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

		), c -> {
			c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).setParent(inputMap);
			c.getActionMap().setParent(actionMap);
		}));

		listenersPreInit.forEach(Runnable::run);
		setPosition(0, 0);
		canvasMap.init();
	}

	private Optional<RegionEntry> getSelectedRegionEntry()
	{
		int index = tableRegionInfoTable.getSelectedIndex();
		if (index < 0) return Optional.empty();
		if (index >= tableRegionInfoTable.getModel().getSize()) return Optional.empty();
		return Optional.of(tableRegionInfoTable.getModel().getElementAt(index));
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
		labelChunkX.setText("" + canvasMap.getPositionX());
		labelChunkZ.setText("" + canvasMap.getPositionZ());
	}

	private void scroll(int x, int z)
	{
		setPosition(canvasMap.getPositionX() + x, canvasMap.getPositionZ() + z);
	}

	private void scrollToRegion(RegionEntry regionEntry)
	{
		ArrayList<ChunkPosition> list = canvasMap.mapData.regionMap.getKeys().stream()
			.filter(cp -> regionEntry.regionIdentifier.equals(canvasMap.mapData.regionMap.get(cp).orElse(null)))
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
