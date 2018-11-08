package mirrg.minecraft.regioneditor.gui;

import static mirrg.minecraft.regioneditor.gui.SwingUtils.*;

import java.awt.CardLayout;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class DialogData
{

	private JDialog dialog;
	private JTextArea textArea;

	public DialogData(JFrame frame, IDialogDataListener listener)
	{
		dialog = new JDialog(frame);

		{
			dialog.setLayout(new CardLayout());

			dialog.add(borderPanelDown(

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

		dialog.pack();
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setLocationByPlatform(true);
	}

	public void show()
	{
		dialog.setVisible(true);
	}

	public static interface IDialogDataListener
	{

		public void onImport(String string);

		public String onExport();

	}

}
