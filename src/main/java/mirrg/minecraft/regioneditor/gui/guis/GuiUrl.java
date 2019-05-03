package mirrg.minecraft.regioneditor.gui.guis;

import java.net.URI;
import java.net.URL;

import mirrg.boron.util.i18n.I18n;
import mirrg.minecraft.regioneditor.util.gui.WindowWrapper;

public class GuiUrl extends GuiInputBox
{

	public GuiUrl(WindowWrapper owner, I18n i18n)
	{
		super(owner, i18n, "GuiUrl.title");
	}

	public URI resultUri;
	public URL resultUrl;

	@Override
	protected boolean parse(String string) throws Exception
	{
		if (!super.parse(string)) return false;

		resultUri = new URI(string);
		resultUrl = new URL(string);

		return true;
	}

}
