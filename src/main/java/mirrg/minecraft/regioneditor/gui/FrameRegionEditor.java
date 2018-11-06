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

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.WindowConstants;

public class FrameRegionEditor
{

	private JFrame frame;
	private CanvasMap canvasMap;

	public FrameRegionEditor()
	{
		frame = new JFrame("RegionEditor");

		// 内容
		{
			frame.setLayout(new BorderLayout());

			frame.add(splitPaneHorizontal(0.7,

				// 左ペイン：地図側
				borderPanelUp(

					button("↑", e -> scroll(0, -4)),

					borderPanelDown(

						borderPanelLeft(

							button("←", e -> scroll(-4, 0)),

							borderPanelRight(

								// 地図
								canvasMap = get(new CanvasMap(), canvasMap -> {
									canvasMap.setMinimumSize(new Dimension(100, 100));
									canvasMap.setPreferredSize(new Dimension(600, 600));
								}),

								button("→", e -> scroll(4, 0))

							)

						),

						button("↓", e -> scroll(0, 4))

					)

				),

				// 右ペイン：領地リストとか操作ボタンとか
				borderPanelDown(

					// 領地一覧
					scrollPane(new JTable()),

					// 操作ボタン
					flowPanel(

						button("地図", e -> {
							FileDialog fileDialog = new FileDialog(frame, "地図を開く", FileDialog.LOAD);
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
								ArrayDeque<int[]> wait = new ArrayDeque<int[]>();
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
											wait.addFirst(new int[] { x, y });
											dispos.get(y)[x] = '0'; //処理待ちキューに入れた座標を0にする
											//幅優先探索の開始
											while (!wait.isEmpty()) {
												//処理待ちの取り出して削除
												int[] pos = wait.removeLast();
												//結果に座標を記録する
												results.add(pos[1] + "," + pos[0]);

												//上下左右の調査の開始
												if (pos[1] + 1 < dispos.size())
													if (dispos.get(pos[1] + 1)[pos[0]] == '1') {
													wait.addFirst(new int[] { pos[0], pos[1] + 1 }); //もしも右が1なら処理待ちキューに追加
													dispos.get(pos[1] + 1)[pos[0]] = '0'; //処理待ちキューに入れた座標を0にする
												}

												if (pos[1] - 1 >= 0)
													if (dispos.get(pos[1] - 1)[pos[0]] == '1') {
													wait.addFirst(new int[] { pos[0], pos[1] - 1 }); //もしも左が1なら   "
													dispos.get(pos[1] - 1)[pos[0]] = '0'; // "
												}

												if (pos[0] + 1 < dispos.get(pos[1]).length)
													if (dispos.get(pos[1])[pos[0] + 1] == '1') {
													wait.addFirst(new int[] { pos[0] + 1, pos[1] }); //もしも下が1なら   
													dispos.get(pos[1])[pos[0] + 1] = '0'; // "
												}

												if (pos[0] - 1 >= 0)
													if (dispos.get(pos[1])[pos[0] - 1] == '1') {
													wait.addFirst(new int[] { pos[0] + 1, pos[1] }); //もしも上が1なら   "
													dispos.get(pos[1])[pos[0] - 1] = '0'; // "
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
									if(input[i].toCharArray()[j] == '1') System.out.println(i + "," + j);;
								"001110",
								"011010",
								"010110",
								"011100",
							};

							for (int i = 0; i < input.length; i++) {
								for (int j = 0; j < input[i].length(); j++) {
									if (input[i].toCharArray()[j] == '1') System.out.println(i + "," + j);
									;
								}
							}
							
						})

					)

				)

			));
		}

		frame.setSize(600, 600);
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	private void scroll(int x, int z)
	{

	}

	public void show()
	{
		frame.setVisible(true);
	}

}
