package mirrg.minecraft.regioneditor.gui;

import static mirrg.minecraft.regioneditor.gui.SwingUtils.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
							
							for(int i = 0;i < input.length;i++) {
								for(int j = 0;j < input[i].length(); j++) {
									if(input[i].toCharArray()[j] == '1') System.out.println(i + "," + j);;
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
