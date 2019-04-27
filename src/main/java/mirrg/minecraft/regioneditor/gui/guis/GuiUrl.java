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
import java.util.function.Predicate;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextArea;
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
	private PanelResult panelResult;

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

	public Optional<Predicate<GuiUrlResult>> oValidator = Optional.empty();

	public Optional<GuiUrlResult> oResult = Optional.empty();

	public GuiUrl(WindowWrapper owner, I18n i18n)
	{
		super(owner, i18n, i18n.localize("GuiUrl.title"), ModalityType.DOCUMENT_MODAL);
	}

	@Override
	protected void initComponenets()
	{

		actionOk = new ActionBuilder<>(new ActionButton(e -> {

			GuiUrlResult result;
			try {
				result = new GuiUrlResult(new URI(textArea.getText()), new URL(textArea.getText()));
			} catch (URISyntaxException | MalformedURLException e1) {
				e1.printStackTrace();
				panelResult.setException(e1);
				return;
			}

			if (oValidator.isPresent() && !oValidator.get().test(result)) {
				return;
			}

			oResult = Optional.of(result);

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

					// タブでフォーカス移動
					c.addKeyListener(new KeyAdapter() {
						@Override
						public void keyPressed(KeyEvent e)
						{
							if (e.getKeyCode() == KeyEvent.VK_TAB) {
								System.out.println(e.getModifiers());
								if ((e.getModifiers() & Event.SHIFT_MASK) != 0) {
									c.transferFocusBackward();
								} else {
									c.transferFocus();
								}
								e.consume();
							}
						}
					});

					// エンターで改行を入力する機能の不活性化
					{
						final Object CANCEL = "CANCEL_54632095834680215631809";
						InputMap inputMap;
						inputMap = new InputMap() {
							@Override
							public Object get(KeyStroke keyStroke)
							{
								Object object = super.get(keyStroke);
								return CANCEL.equals(object) ? null : object;
							}
						};
						inputMap.setParent(c.getInputMap(JComponent.WHEN_FOCUSED));
						inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), CANCEL);
						inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), CANCEL);
						inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), actionOk);
						c.setInputMap(JComponent.WHEN_FOCUSED, inputMap);
					}

				}), 400, 80),

				panelResult = new PanelResult(windowWrapper, i18n)

			),

			flowPanel(

				button(localize("GuiUrl.buttonOk"), actionOk),

				button(localize("GuiUrl.buttonCancel"), actionCancel)

			)

		));

	}

	public void setException(Exception e)
	{
		panelResult.setException(e);
	}

}
