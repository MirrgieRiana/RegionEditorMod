package mirrg.minecraft.regioneditor.gui.guis;

import java.awt.CardLayout;
import java.awt.Dialog.ModalityType;

import mirrg.boron.util.UtilsString;
import mirrg.boron.util.i18n.I18n;
import mirrg.minecraft.regioneditor.gui.PanelMessage;
import mirrg.minecraft.regioneditor.util.gui.WindowWrapper;

public class GuiMessage extends GuiBase
{

	private PanelMessage panelMessage;

	public GuiMessage(WindowWrapper owner, I18n i18n, ModalityType modalityType)
	{
		super(owner, i18n, i18n.localize("GuiMessage.title"), modalityType);
	}

	@Override
	protected void initComponenets()
	{
		windowWrapper.getWindow().setLayout(new CardLayout());

		windowWrapper.getWindow().add(panelMessage = new PanelMessage());
	}

	public void setMessage(String message)
	{
		panelMessage.setText(message);
	}

	public static void showException(Exception e)
	{
		GuiMessage gui = new GuiMessage(WindowWrapper.createFrame("Exception"), new I18n(), ModalityType.MODELESS);
		gui.show();
		gui.setMessage(UtilsString.getStackTrace(e));
	}

}
