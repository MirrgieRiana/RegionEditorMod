package mirrg.minecraft.regioneditor.gui.guis;

import java.awt.Dialog.ModalityType;

import mirrg.boron.util.i18n.I18n;
import mirrg.minecraft.regioneditor.util.gui.WindowWrapper;

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
		if (windowWrapper.parent != null) {
			windowWrapper.getWindow().setLocationRelativeTo(windowWrapper.parent.getWindow());
		}
		windowWrapper.getWindow().setVisible(true);
	}

	protected String localize(String unlocalizedString)
	{
		return i18n.localize(unlocalizedString);
	}

}
