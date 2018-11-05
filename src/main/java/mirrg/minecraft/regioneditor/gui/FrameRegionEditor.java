package mirrg.minecraft.regioneditor.gui;

import static mirrg.minecraft.regioneditor.gui.SwingUtils.*;

import java.awt.BorderLayout;
import java.awt.Dimension;

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

						}),

						button("B", e -> {

						}),

						button("C", e -> {

						}),

						button("D", e -> {

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
