package mirrg.boron.util.i18n.localizers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import mirrg.boron.util.i18n.ILocalizer;

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

	//

	public static LocalizerResourceBundle create(String basename, Locale locale) throws IOException
	{
		try {
			return new LocalizerResourceBundle(ResourceBundle.getBundle(basename, locale, new ControlExtension()));
		} catch (MissingResourceException e) {
			throw new IOException(e);
		}
	}

	public static class ControlExtension extends ResourceBundle.Control
	{

		public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
			throws IllegalAccessException, InstantiationException, IOException
		{
			if (!format.equals("java.properties")) return super.newBundle(baseName, locale, format, loader, reload);

			String bundleName = toBundleName(baseName, locale);
			if (bundleName.contains("://")) return null;

			String resourceName = toResourceName(bundleName, "properties");

			InputStream stream;
			try {
				stream = AccessController.doPrivileged(
					new PrivilegedExceptionAction<InputStream>() {
						public InputStream run() throws IOException
						{
							if (reload) {
								URL url = loader.getResource(resourceName);
								if (url != null) {
									URLConnection connection = url.openConnection();
									if (connection != null) {
										connection.setUseCaches(false);
										return connection.getInputStream();
									}
								}
								return null;
							} else {
								return loader.getResourceAsStream(resourceName);
							}
						}
					});
			} catch (PrivilegedActionException e) {
				throw (IOException) e.getException();
			}

			try {
				return new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
			} finally {
				stream.close();
			}
		}

	}

}
