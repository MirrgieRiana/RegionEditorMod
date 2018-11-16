package mirrg.minecraft.regioneditor.gui;

import java.awt.Dialog.ModalityType;

public abstract class GuiBase
{

	protected WindowWrapper windowWrapper;

	public GuiBase(WindowWrapper owner, String title, ModalityType modalityType)
	{
		windowWrapper = WindowWrapper.createWindow(owner, title, modalityType);
	}

	protected abstract void initComponenets();

	public void show()
	{
		initComponenets();
		windowWrapper.getWindow().pack();
		windowWrapper.getWindow().setVisible(true);
	}

}
