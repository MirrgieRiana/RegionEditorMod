package mirrg.minecraft.regioneditor.gui;

import static mirrg.minecraft.regioneditor.util.gui.SwingUtils.*;

import java.awt.CardLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import mirrg.boron.swing.UtilsColor;
import mirrg.boron.util.UtilsString;
import mirrg.boron.util.i18n.I18n;
import mirrg.minecraft.regioneditor.gui.guis.GuiMessage;
import mirrg.minecraft.regioneditor.util.gui.WindowWrapper;

public class PanelResult extends JPanel
{

	private JTextPane textPaneResult;
	private String detail = "";

	public PanelResult(WindowWrapper owner, I18n i18n)
	{
		setLayout(new CardLayout());

		add(borderPanelRight(

			get(scrollPane(textPaneResult = get(new JTextPane(), c -> {
				c.setEditable(false);
				c.setOpaque(false);
			})), c -> {
				c.setBorder(null);
				c.setOpaque(false);
				c.setPreferredSize(new Dimension(400, textPaneResult.getFontMetrics(textPaneResult.getFont()).getHeight()));
			}),

			button(i18n.localize("ResultPane.detail"), e -> {
				GuiMessage gui = new GuiMessage(owner, i18n, ModalityType.MODELESS);
				gui.show();
				gui.setMessage(detail);
			})

		));
	}

	public JTextPane getTextPaneResult()
	{
		return textPaneResult;
	}

	public void setException(Exception e)
	{
		setText(e.getClass().getSimpleName() + ": " + e.getMessage(), UtilsString.getStackTrace(e));
	}

	public void setText(String string, String detail)
	{
		try {
			textPaneResult.getStyledDocument().remove(0, textPaneResult.getStyledDocument().getLength());
			SimpleAttributeSet attributeSet = new SimpleAttributeSet();
			StyleConstants.setForeground(attributeSet, UtilsColor.createColorRandomDark());
			textPaneResult.getStyledDocument().insertString(0, string, attributeSet);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		this.detail = detail;
	}

}
