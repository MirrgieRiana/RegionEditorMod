package mirrg.minecraft.regioneditor.gui.guis;

import static mirrg.minecraft.regioneditor.util.gui.SwingUtils.*;

import java.awt.CardLayout;
import java.awt.Dialog.ModalityType;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

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

	public static class GuiUrlResult
	{

		public final URI uri;
		public final URL url;

		public GuiUrlResult(URI uri, URL url)
		{
			this.uri = uri;
			this.url = url;
		}

	}

	public Optional<GuiUrlResult> oResult = Optional.empty();

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
						oResult = Optional.of(new GuiUrlResult(new URI(textArea.getText()), new URL(textArea.getText())));
						windowWrapper.getWindow().setVisible(false);
					} catch (URISyntaxException | MalformedURLException e1) {
						textPaneResult.setText(e1.getClass().getSimpleName() + ": " + e1.getMessage());
					}
				}),

				button(localize("GuiUrl.buttonCancel"), e -> {
					windowWrapper.getWindow().setVisible(false);
				})

			)

		));
	}

}
