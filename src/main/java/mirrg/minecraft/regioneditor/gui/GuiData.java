package mirrg.minecraft.regioneditor.gui;

import static mirrg.minecraft.regioneditor.gui.SwingUtils.*;

import java.awt.CardLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Font;

import javax.swing.JTextArea;

import mirrg.boron.util.i18n.I18n;

public class GuiData extends GuiBase
{

	private IDialogDataListener listener;

	public GuiData(WindowWrapper owner, I18n i18n, IDialogDataListener listener)
	{
		super(owner, i18n, i18n.localize("GuiData.title"), ModalityType.MODELESS);
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
			}), 400, 400),

			flowPanel(

				button(localize("GuiData.buttonExport"), e2 -> {
					textArea.setText(listener.onExport());
				}),

				button(localize("GuiData.buttonImport"), e2 -> {
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
