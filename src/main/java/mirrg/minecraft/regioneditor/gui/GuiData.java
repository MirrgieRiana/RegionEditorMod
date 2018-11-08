package mirrg.minecraft.regioneditor.gui;

import static mirrg.minecraft.regioneditor.gui.SwingUtils.*;

import java.awt.CardLayout;
import java.awt.Font;

import javax.swing.JTextArea;

public class GuiData
{

	private WindowWrapper windowWrapper;
	private JTextArea textArea;

	public GuiData(WindowWrapper owner, IDialogDataListener listener)
	{
		windowWrapper = WindowWrapper.createWindow(owner, "Data");

		{
			windowWrapper.getWindow().setLayout(new CardLayout());

			windowWrapper.getWindow().add(borderPanelDown(

				scrollPane(textArea = get(new JTextArea(), c -> {
					c.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
				}), 200, 200),

				flowPanel(

					button("↑Export↑", e2 -> {
						textArea.setText(listener.onExport());
					}),

					button("↓Import↓", e2 -> {
						listener.onImport(textArea.getText());
					})

				)

			));
		}

		windowWrapper.getWindow().pack();
	}

	public void show()
	{
		windowWrapper.getWindow().setVisible(true);
	}

	public static interface IDialogDataListener
	{

		public void onImport(String string);

		public String onExport();

	}

}
