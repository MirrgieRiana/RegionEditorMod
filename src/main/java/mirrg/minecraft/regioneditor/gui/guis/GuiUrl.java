package mirrg.minecraft.regioneditor.gui.guis;

import static mirrg.minecraft.regioneditor.util.gui.SwingUtils.*;

import java.awt.CardLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Event;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import mirrg.boron.util.i18n.I18n;
import mirrg.minecraft.regioneditor.gui.PanelResult;
import mirrg.minecraft.regioneditor.util.gui.ActionBuilder;
import mirrg.minecraft.regioneditor.util.gui.ActionButton;
import mirrg.minecraft.regioneditor.util.gui.WindowWrapper;

public class GuiUrl extends GuiBase
{

	private ActionButton actionOk;
	private ActionButton actionCancel;

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

	public GuiUrl(WindowWrapper owner, I18n i18n)
	{
		super(owner, i18n, i18n.localize("GuiUrl.title"), ModalityType.DOCUMENT_MODAL);
	}

	@Override
	protected void initComponenets()
	{

		actionOk = new ActionBuilder<>(new ActionButton(e -> {
			try {
				oResult = Optional.of(new GuiUrlResult(new URI(textArea.getText()), new URL(textArea.getText())));
			} catch (URISyntaxException | MalformedURLException e1) {
				panelResult.setException(e1);
				return;
			}
			windowWrapper.getWindow().setVisible(false);
		}))
			.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0))
			.register(windowWrapper.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW), windowWrapper.getRootPane().getActionMap());
		actionCancel = new ActionBuilder<>(new ActionButton(e -> {
			windowWrapper.getWindow().setVisible(false);
		}))
			.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0))
			.register(windowWrapper.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW), windowWrapper.getRootPane().getActionMap());

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

				button(localize("GuiUrl.buttonOk"), actionOk),

				button(localize("GuiUrl.buttonCancel"), actionCancel)

			)

		));
	}

}
