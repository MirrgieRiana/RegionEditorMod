package mirrg.minecraft.regioneditor.gui;

import static mirrg.minecraft.regioneditor.gui.SwingUtils.*;

import java.awt.CardLayout;
import java.awt.Font;

import javax.swing.JTextArea;

public class GuiData extends GuiBase
{

	private WindowWrapper windowWrapper;
	private IDialogDataListener listener;

	public GuiData(WindowWrapper owner, IDialogDataListener listener)
	{
		super(owner, "Data");
		this.listener = listener;
	}

	private JTextArea textArea;

	@Override
	protected void initComponenets()
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

	public static interface IDialogDataListener
	{

		public void onImport(String string);

		public String onExport();

	}

}
