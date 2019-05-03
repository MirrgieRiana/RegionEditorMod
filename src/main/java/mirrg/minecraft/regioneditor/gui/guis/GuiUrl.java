package mirrg.minecraft.regioneditor.gui.guis;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import mirrg.boron.util.i18n.I18n;
import mirrg.minecraft.regioneditor.util.gui.WindowWrapper;

public class GuiUrl extends GuiInputBox
{

	public URI uri;
	public URL url;

	public GuiUrl(WindowWrapper owner, I18n i18n)
	{
		super(owner, i18n, "GuiUrl.title");
	}

	@Override
	protected boolean parse(String string)
	{
		if (!super.parse(string)) return false;

		try {
			uri = new URI(string);
			url = new URL(string);
		} catch (URISyntaxException | MalformedURLException e1) {
			e1.printStackTrace();
			setException(e1);
			return false;
		}

		return true;
	}

}
