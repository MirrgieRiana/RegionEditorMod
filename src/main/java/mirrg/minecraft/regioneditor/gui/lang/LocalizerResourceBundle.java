package mirrg.minecraft.regioneditor.gui.lang;

import java.util.ResourceBundle;

public class LocalizerResourceBundle implements ILocalizer
{

	public final ResourceBundle resourceBundle;

	public LocalizerResourceBundle(ResourceBundle resourceBundle)
	{
		this.resourceBundle = resourceBundle;
	}

	@Override
	public boolean canLocalize(String unlocalizedString)
	{
		return resourceBundle.containsKey(unlocalizedString);
	}

	@Override
	public String localize(String unlocalizedString)
	{
		return resourceBundle.getString(unlocalizedString);
	}

}
