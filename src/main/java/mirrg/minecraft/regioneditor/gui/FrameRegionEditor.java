package mirrg.minecraft.regioneditor.gui;

import static mirrg.minecraft.regioneditor.gui.SwingUtils.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.awt.FileDialog;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;

import mirrg.minecraft.regioneditor.gui.DialogData.IDialogDataListener;

public class FrameRegionEditor
{

	private WindowWrapper windowWrapper;
	private CanvasMap canvasMap;
	private JLabel labelCoordX;
	private JLabel labelCoordZ;
	private JLabel labelChunkX;
	private JLabel labelChunkZ;
	private JFormattedTextField textFieldX;
	private JFormattedTextField textFieldZ;

	public FrameRegionEditor(WindowWrapper owner)
	{
		windowWrapper = WindowWrapper.createWindow(owner, "RegionEditor");

		// 内容
		{
			windowWrapper.getWindow().setLayout(new BorderLayout());

			windowWrapper.getWindow().add(splitPaneHorizontal(0.7,

				borderPanelDown(

					borderPanelDown(

						// 左ペイン：地図側
						borderPanelUp(

							button("↑", e -> scroll(0, -4)),

							borderPanelDown(

								borderPanelLeft(

									button("←", e -> scroll(-4, 0)),

									borderPanelRight(

										// 地図
										canvasMap = get(new CanvasMap(), c -> {
											c.setMinimumSize(new Dimension(100, 100));
											c.setPreferredSize(new Dimension(600, 600));
										}),

										button("→", e -> scroll(4, 0))

									)

								),

								button("↓", e -> scroll(0, 4))

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
					scrollPane(new JTable(), 300, 600),

					// 操作ボタン
					flowPanel(

						button("Map", e -> {
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

							BufferedImage image;
							try {
								image = ImageIO.read(file);
							} catch (IOException e1) {
								e1.printStackTrace();
								return;
							}

							if (image == null) {
								System.err.println("画像の読み込みに失敗しました。");
								return;
							}

							canvasMap.setMap(image);
						}),

						button("Data", e -> new DialogData(windowWrapper, new IDialogDataListener() {
							@Override
							public void onImport(String string)
							{
								canvasMap.fromExpression(string);
							}

							@Override
							public String onExport()
							{
								return canvasMap.toExpression();
							}
						}).show()),

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
											wait.addFirst(new Point(x,y));
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
													wait.addFirst(new Point(pos.Y, pos.X + 1 )); //もしも右が1なら処理待ちキューに追加
													dispos.get(pos.X + 1)[pos.Y] = '0'; //処理待ちキューに入れた座標を0にする
												}

												if (pos.X - 1 >= 0)
													if (dispos.get(pos.X - 1)[pos.Y] == '1') {
													wait.addFirst(new Point( pos.Y, pos.X - 1 )); //もしも左が1なら   "
													dispos.get(pos.X - 1)[pos.Y] = '0'; // "
												}

												if (pos.Y + 1 < dispos.get(pos.X).length)
													if (dispos.get(pos.X)[pos.Y + 1] == '1') {
													wait.addFirst(new Point( pos.Y + 1, pos.X )); //もしも下が1なら
													dispos.get(pos.X)[pos.Y + 1] = '0'; // "
												}

												if (pos.Y - 1 >= 0)
													if (dispos.get(pos.X)[pos.Y - 1] == '1') {
													wait.addFirst(new Point(pos.Y + 1, pos.X )); //もしも上が1なら   "
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

						}

						),

						button("C", e -> {

						}),

						button("D", e -> {
							String[] input = {
									 "001110",
									 "011010",
									 "010110",
									 "011100",
									};
							
							for(int i = 0;i < input.length;i++) {
								for(int j = 0;j < input[i].length(); j++) {
									if(input[i].toCharArray()[j] == '1') System.out.println(i + "," + j);
								}
							}

						})

					)

				)

			));
		}

		setPosition(0, 0);

		windowWrapper.getWindow().pack();
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

	public void show()
	{
		windowWrapper.getWindow().setVisible(true);
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

	public Side(Point p1,Point p2)
	{
		Point1 = p1;
		Point2 = p2;
	}

	@Override
    public boolean equals(Object obj) {
        //実装中
		return false;
    }

}
