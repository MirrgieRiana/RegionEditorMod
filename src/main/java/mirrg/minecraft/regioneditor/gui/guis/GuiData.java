package mirrg.minecraft.regioneditor.gui.guis;

import static mirrg.minecraft.regioneditor.util.gui.SwingUtils.*;

import java.awt.CardLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import mirrg.boron.util.i18n.I18n;
import mirrg.minecraft.regioneditor.util.gui.ActionBuilder;
import mirrg.minecraft.regioneditor.util.gui.ActionButton;
import mirrg.minecraft.regioneditor.util.gui.WindowWrapper;

public class GuiData extends GuiBase
{

	private IDialogDataListener listener;

	private ActionButton actionExport;
	private ActionButton actionImport;
	@SuppressWarnings("unused")
	private ActionButton actionClose;

	private JTextArea textArea;

	public GuiData(WindowWrapper owner, I18n i18n, IDialogDataListener listener)
	{
		super(owner, i18n, i18n.localize("GuiData.title"), ModalityType.MODELESS);
		this.listener = listener;
	}

	@Override
	protected void initComponenets()
	{

		actionExport = new ActionBuilder<>(new ActionButton(e -> {
			textArea.setText(listener.onExport());
		}))
			.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK))
			.register(windowWrapper.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW), windowWrapper.getRootPane().getActionMap());
		actionImport = new ActionBuilder<>(new ActionButton(e -> {
			listener.onImport(textArea.getText());
		}))
			.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK))
			.register(windowWrapper.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW), windowWrapper.getRootPane().getActionMap());
		actionClose = new ActionBuilder<>(new ActionButton(e -> {
			windowWrapper.getWindow().dispose();
		}))
			.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0))
			.register(windowWrapper.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW), windowWrapper.getRootPane().getActionMap());

		windowWrapper.getWindow().setLayout(new CardLayout());
		windowWrapper.getWindow().add(borderPanelDown(

			scrollPane(textArea = get(new JTextArea(), c -> {
				c.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
			}), 800, 800),

			flowPanel(

				button(localize("GuiData.buttonExport"), actionExport),

				button(localize("GuiData.buttonImport"), actionImport)

			)

		));

	}

	public static interface IDialogDataListener
	{

		public void onImport(String string);

		public String onExport();

	}

}
