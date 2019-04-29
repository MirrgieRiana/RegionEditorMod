package mirrg.minecraft.regioneditor.gui;

import static mirrg.boron.swing.UtilsComponent.*;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.Timer;
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

	public static Color SUCCESS = Color.decode("#88ff88");
	public static Color WARNING = Color.decode("#ffff88");
	public static Color EXCEPTION = Color.decode("#ffaa88");
	public static Color ERROR = Color.decode("#ff8888");

	private JTextPane textPaneResult;
	private Timer timer;

	private String detail = "";
	private Color color;
	private int opaque;

	public PanelResult(WindowWrapper owner, I18n i18n)
	{
		setLayout(new CardLayout());

		add(createPanelBorderRight(0,

			get(createScrollPane(textPaneResult = get(new JTextPane(), c -> {
				c.setEditable(false);
				c.setOpaque(false);
			})), c -> {
				c.setBorder(null);
				c.getViewport().setOpaque(false);
				c.setOpaque(false);
				c.setPreferredSize(new Dimension(400, textPaneResult.getFontMetrics(textPaneResult.getFont()).getHeight()));
			}),

			createButton(i18n.localize("ResultPane.detail"), e -> {
				GuiMessage gui = new GuiMessage(owner, i18n, ModalityType.MODELESS);
				gui.show();
				gui.setMessage(detail);
			})

		));

		setBackground(Color.white);

		timer = new Timer(10, e -> {
			if (opaque > 0) {
				setBackground(new Color(UtilsColor.ratio(opaque / 256.0, 0xFFFFFF, color.getRGB())));
				opaque -= 10;
			} else {
				setBackground(Color.white);
				timer.stop();
			}
		});
		timer.setRepeats(true);

	}

	public JTextPane getTextPaneResult()
	{
		return textPaneResult;
	}

	public void setException(Exception e)
	{
		setText(e.getClass().getSimpleName() + ": " + e.getMessage(), UtilsString.getStackTrace(e), ERROR);
	}

	public void setText(String string, String detail, Color color)
	{
		try {
			textPaneResult.getStyledDocument().remove(0, textPaneResult.getStyledDocument().getLength());
			SimpleAttributeSet attributeSet = new SimpleAttributeSet();
			StyleConstants.setForeground(attributeSet, UtilsColor.toColorRGB(UtilsColor.randomRGBDark()));
			textPaneResult.getStyledDocument().insertString(0, string, attributeSet);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		this.detail = detail;

		this.color = color;
		opaque = 255;
		timer.start();
	}

}
