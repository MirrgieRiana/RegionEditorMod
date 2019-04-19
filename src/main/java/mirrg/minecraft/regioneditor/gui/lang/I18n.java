package mirrg.minecraft.regioneditor.gui.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import com.google.common.base.Supplier;

import mirrg.boron.util.suppliterator.ISuppliterator;

public class I18n
{

	/**
	 * 優先度の高い順に格納されている。
	 */
	private Deque<Supplier<Optional<? extends ILocalizer>>> listSOLocalizer = new ArrayDeque<>();

	/**
	 * 現在登録されているローカライザーを優先度の高い順に返します。
	 */
	public synchronized ISuppliterator<ILocalizer> getLocalizers()
	{
		return ISuppliterator.ofIterable(listSOLocalizer)
			.mapIfPresent(Supplier::get);
	}

	/**
	 * ローカライザーエンジンを最も優先度の高いものとして追加します。
	 */
	public synchronized void registerLocalizerEngine(Supplier<Optional<? extends ILocalizer>> soLocalizer)
	{
		listSOLocalizer.addFirst(soLocalizer);
	}

	/**
	 * ローカライザーエンジンを最も優先度の高いものとして追加します。
	 */
	public synchronized void registerLocalizerEngine(ILocalizer localizer)
	{
		Optional<ILocalizer> oLocalizer = Optional.of(localizer);
		listSOLocalizer.addFirst(() -> oLocalizer);
	}

	public synchronized String localize(String unlocalizedString)
	{
		for (ILocalizer localizer : getLocalizers()) {
			if (localizer.canLocalize(unlocalizedString)) {
				return localizer.localize(unlocalizedString);
			}
		}
		return unlocalizedString;
	}

	//

	public static LocalizerResourceBundle getLocalizerResourceBundle(Locale locale) throws IOException
	{
		return getLocalizerResourceBundle(I18n.class.getPackage().getName() + ".gui", locale);
	}

	public static LocalizerResourceBundle getLocalizerResourceBundle(String basename, Locale locale) throws IOException
	{
		try {
			return new LocalizerResourceBundle(ResourceBundle.getBundle(basename, locale, new ResourceBundle.Control() {
				public ResourceBundle newBundle(
					String baseName,
					Locale locale,
					String format,
					ClassLoader loader,
					boolean reload) throws IllegalAccessException, InstantiationException, IOException
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
			}));
		} catch (MissingResourceException e) {
			throw new IOException(e);
		}
	}

}
