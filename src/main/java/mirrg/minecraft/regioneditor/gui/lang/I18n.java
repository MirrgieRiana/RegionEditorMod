package mirrg.minecraft.regioneditor.gui.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class I18n
{

	private static Map<String, String> instanceCurrent = new HashMap<>();
	private static Map<String, String> instanceDefault = new HashMap<>();
	static {
		try {
			instanceDefault = load("en_US");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized static String localize(String unlocalizedString)
	{
		if (instanceCurrent.containsKey(unlocalizedString)) {
			return instanceCurrent.get(unlocalizedString);
		}
		if (instanceDefault.containsKey(unlocalizedString)) {
			return instanceDefault.get(unlocalizedString);
		}
		return unlocalizedString;
	}

	public synchronized static void setLocale(String locale)
	{
		if (locale.matches("[a-zA-Z][a-zA-Z]_[a-zA-Z][a-zA-Z]")) {
			locale = locale.substring(0, 2).toLowerCase() + "_" + locale.substring(3, 5);
		}

		try {
			instanceCurrent = load(locale);
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		instanceCurrent = new HashMap<>();
	}

	private static Map<String, String> load(URL url) throws IOException
	{
		Map<String, String> map = new HashMap<>();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
			Properties properties = new Properties();
			properties.load(in);
			for (String key : properties.stringPropertyNames()) {
				map.put(key, properties.getProperty(key));
			}
		}
		return map;
	}

	private static Map<String, String> load(String locale) throws IOException
	{
		return load(I18n.class.getResource(locale + ".txt"));
	}

}
