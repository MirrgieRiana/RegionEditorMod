package mirrg.minecraft.regioneditor.gui.guis;

import static mirrg.minecraft.regioneditor.util.gui.SwingUtils.*;

import java.awt.CardLayout;
import java.awt.Dialog.ModalityType;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JTextArea;
import javax.swing.JTextPane;

import mirrg.boron.util.i18n.I18n;
import mirrg.minecraft.regioneditor.util.gui.WindowWrapper;

public class GuiUrl extends GuiBase
{

	public GuiUrl(WindowWrapper owner, I18n i18n)
	{
		super(owner, i18n, i18n.localize("GuiUrl.title"), ModalityType.DOCUMENT_MODAL);
	}

	private JTextArea textArea;
	private JTextPane textPaneResult;

	public boolean ok = false;
	public URI uri = null;
	public URL url = null;

	@Override
	protected void initComponenets()
	{
		windowWrapper.getWindow().setLayout(new CardLayout());

		windowWrapper.getWindow().add(borderPanelDown(

			splitPaneVertical(1,

				scrollPane(textArea = get(new JTextArea(), c -> {
					c.setLineWrap(true);
				}), 400, 80),

				get(scrollPane(textPaneResult = get(new JTextPane(), c -> {
					c.setEditable(false);
					c.setOpaque(false);
				}), 400, 40), c -> {
					c.setBorder(null);
					c.setOpaque(false);
				})

			),

			flowPanel(

				button(localize("GuiUrl.buttonOk"), e -> {
					try {
						uri = new URI(textArea.getText());
						url = new URL(textArea.getText());
						ok = true;
						windowWrapper.getWindow().setVisible(false);
					} catch (URISyntaxException | MalformedURLException e1) {
						textPaneResult.setText(e1.getClass().getSimpleName() + "\n" + e1.getMessage());
					}
				}),

				button(localize("GuiUrl.buttonCancel"), e -> {
					windowWrapper.getWindow().setVisible(false);
				})

			)

		));
	}

}
