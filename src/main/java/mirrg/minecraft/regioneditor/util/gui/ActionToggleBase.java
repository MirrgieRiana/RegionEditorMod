package mirrg.minecraft.regioneditor.util.gui;

public abstract class ActionToggleBase extends ActionBase
{

	public ActionToggleBase()
	{
		setSelected(false);
	}

	public boolean isSelected()
	{
		return (Boolean) getValue(SELECTED_KEY);
	}

	public void setSelected(boolean selected)
	{
		putValue(SELECTED_KEY, selected);
	}

}
