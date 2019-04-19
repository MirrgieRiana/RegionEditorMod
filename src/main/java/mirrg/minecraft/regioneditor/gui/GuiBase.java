package mirrg.minecraft.regioneditor.gui;

import java.awt.Dialog.ModalityType;

import mirrg.boron.util.i18n.I18n;

public abstract class GuiBase
{

	protected WindowWrapper windowWrapper;
	protected I18n i18n;

	public GuiBase(WindowWrapper owner, I18n i18n, String title, ModalityType modalityType)
	{
		this.i18n = i18n;
		windowWrapper = WindowWrapper.createWindow(owner, title, modalityType);
	}

	protected abstract void initComponenets();

	public void show()
	{
		initComponenets();
		windowWrapper.getWindow().pack();
		windowWrapper.getWindow().setVisible(true);
	}

	protected String localize(String unlocalizedString)
	{
		return i18n.localize(unlocalizedString);
	}

}
