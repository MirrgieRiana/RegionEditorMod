package mirrg.minecraft.regioneditor.gui;

import java.awt.Dialog.ModalityType;

public abstract class GuiBase
{

	protected WindowWrapper windowWrapper;

	public GuiBase(WindowWrapper owner, String title, ModalityType modalityType)
	{
		windowWrapper = WindowWrapper.createWindow(owner, title, modalityType);

		initComponenets();

		windowWrapper.getWindow().pack();
	}

	protected abstract void initComponenets();

	public void show()
	{
		windowWrapper.getWindow().setVisible(true);
	}

}
