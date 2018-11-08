package mirrg.minecraft.regioneditor.gui;

public abstract class GuiBase
{

	protected WindowWrapper windowWrapper;

	public GuiBase(WindowWrapper owner, String title)
	{
		windowWrapper = WindowWrapper.createWindow(owner, title);

		initComponenets();

		windowWrapper.getWindow().pack();
	}

	protected abstract void initComponenets();

	public void show()
	{
		windowWrapper.getWindow().setVisible(true);
	}

}
