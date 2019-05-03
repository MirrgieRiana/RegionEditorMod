package mirrg.minecraft.regioneditor.gui.guis;

import static mirrg.boron.swing.UtilsComponent.*;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Event;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import mirrg.boron.util.i18n.I18n;
import mirrg.minecraft.regioneditor.gui.PanelResult;
import mirrg.minecraft.regioneditor.util.gui.ActionBuilder;
import mirrg.minecraft.regioneditor.util.gui.ActionButton;
import mirrg.minecraft.regioneditor.util.gui.WindowWrapper;

public abstract class GuiInputBox extends GuiBase
{

	protected ActionButton actionOk;
	protected ActionButton actionCancel;

	protected JTextArea textArea;
	protected PanelResult panelResult;

	public boolean isOk;

	public GuiInputBox(WindowWrapper owner, I18n i18n, String unlocalizedTitle)
	{
		super(owner, i18n, i18n.localize(unlocalizedTitle), ModalityType.DOCUMENT_MODAL);
	}

	protected boolean parse(String string)
	{
		return true;
	}

	protected void onOk(String string)
	{
		if (parse(string)) {
			close(true);
		}
	}

	protected void onCancel()
	{
		close(false);
	}

	public void setText(String string, String detail, Color color)
	{
		panelResult.setText(string, detail, color);
	}

	public void setException(Exception e)
	{
		panelResult.setException(e);
	}

	public void close(boolean isOk)
	{
		this.isOk = isOk;
		windowWrapper.getWindow().dispose();
	}

	@Override
	protected void initComponenets()
	{

		actionOk = new ActionBuilder<>(new ActionButton(e -> onOk(textArea.getText())))
			.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0))
			.register(windowWrapper.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW), windowWrapper.getRootPane().getActionMap());
		actionCancel = new ActionBuilder<>(new ActionButton(e -> onCancel()))
			.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0))
			.register(windowWrapper.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW), windowWrapper.getRootPane().getActionMap());

		windowWrapper.getWindow().setLayout(new CardLayout());
		windowWrapper.getWindow().add(createPanelBorderDown(0,

			createSplitPaneVertical(1,

				createScrollPane(textArea = get(new JTextArea(), c -> {
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

			createPanelFlow(

				createButton(localize("GuiUrl.buttonOk"), actionOk),

				createButton(localize("GuiUrl.buttonCancel"), actionCancel)

			)

		));

	}

}
