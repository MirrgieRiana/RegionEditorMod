package mirrg.minecraft.regioneditor.gui.guis;

import static mirrg.minecraft.regioneditor.util.gui.SwingUtils.*;

import java.awt.CardLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import mirrg.boron.util.i18n.I18n;
import mirrg.minecraft.regioneditor.gui.PanelResult;
import mirrg.minecraft.regioneditor.util.gui.ActionBuilder;
import mirrg.minecraft.regioneditor.util.gui.ActionButton;
import mirrg.minecraft.regioneditor.util.gui.MenuItem;
import mirrg.minecraft.regioneditor.util.gui.WindowWrapper;

public class GuiData extends GuiBase
{

	private IDialogDataListener listener;

	private ActionButton actionClear;
	private ActionButton actionExport;
	private ActionButton actionImport;
	private ActionButton actionClose;

	private JTextArea textArea;
	private PanelResult panelResult;

	public GuiData(WindowWrapper owner, I18n i18n, IDialogDataListener listener)
	{
		super(owner, i18n, i18n.localize("GuiData.title"), ModalityType.MODELESS);
		this.listener = listener;
	}

	@Override
	protected void initComponenets()
	{

		// アクション
		actionClear = new ActionBuilder<>(new ActionButton(e -> {
			textArea.setText("");
		}))
			.value(Action.NAME, localize("GuiData.actionClear") + "(C)")
			.value(Action.MNEMONIC_KEY, KeyEvent.VK_C)
			.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK))
			.register(windowWrapper.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW), windowWrapper.getRootPane().getActionMap());
		actionExport = new ActionBuilder<>(new ActionButton(e -> {
			textArea.setText(listener.onExport());
		}))
			.value(Action.NAME, localize("GuiData.actionExport") + "(E)")
			.value(Action.MNEMONIC_KEY, KeyEvent.VK_E)
			.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK))
			.register(windowWrapper.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW), windowWrapper.getRootPane().getActionMap());
		actionImport = new ActionBuilder<>(new ActionButton(e -> {
			try {
				listener.onImport(textArea.getText());
			} catch (Exception e1) {
				e1.printStackTrace();
				panelResult.setException(e1);
			}
		}))
			.value(Action.NAME, localize("GuiData.actionImport") + "(I)")
			.value(Action.MNEMONIC_KEY, KeyEvent.VK_I)
			.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK))
			.register(windowWrapper.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW), windowWrapper.getRootPane().getActionMap());
		actionClose = new ActionBuilder<>(new ActionButton(e -> {
			windowWrapper.getWindow().dispose();
		}))
			.value(Action.NAME, localize("GuiData.actionClose") + "(Q)")
			.value(Action.MNEMONIC_KEY, KeyEvent.VK_Q)
			.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0))
			.register(windowWrapper.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW), windowWrapper.getRootPane().getActionMap());

		// メニュー
		windowWrapper.setJMenuBar(get(new JMenuBar(), menuBar2 -> {
			menuBar2.add(get(new JMenu(localize("GuiData.menuData") + "(D)"), menu -> {
				menu.setMnemonic(KeyEvent.VK_D);
				menu.add(new MenuItem(actionClear));
				menu.add(new MenuItem(actionExport));
				menu.add(new MenuItem(actionImport));
				menu.addSeparator();
				menu.add(new MenuItem(actionClose));
			}));
		}));

		// コンポーネント
		windowWrapper.getWindow().setLayout(new CardLayout());
		windowWrapper.getWindow().add(borderPanelDown(

			splitPaneVertical(1,

				scrollPane(textArea = get(new JTextArea(), c -> {
					c.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
				}), 800, 800),

				panelResult = new PanelResult(windowWrapper, i18n)

			),

			flowPanel(

				button(localize("GuiData.buttonExport"), actionExport),

				button(localize("GuiData.buttonImport"), actionImport)

			)

		));

	}

	public static interface IDialogDataListener
	{

		public void onImport(String string) throws Exception;

		public String onExport();

	}

}
