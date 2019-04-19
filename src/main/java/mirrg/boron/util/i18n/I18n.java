package mirrg.boron.util.i18n;

import java.util.ArrayDeque;
import java.util.Deque;

import mirrg.boron.util.suppliterator.ISuppliterator;

public class I18n
{

	/**
	 * 優先度の高い順に格納されている。
	 */
	private Deque<ILocalizerEngine> listSOLocalizer = new ArrayDeque<>();

	/**
	 * 現在登録されているローカライザーを優先度の高い順に返します。
	 */
	public synchronized ISuppliterator<ILocalizer> getLocalizers()
	{
		return ISuppliterator.ofIterable(listSOLocalizer)
			.mapIfPresent(le -> le.getLocalizer());
	}

	/**
	 * ローカライザーエンジンを最も優先度の高いものとして追加します。
	 */
	public synchronized void registerLocalizer(ILocalizer localizer)
	{
		registerLocalizerEngine(new LocalizerEngine(localizer));
	}

	/**
	 * ローカライザーエンジンを最も優先度の高いものとして追加します。
	 */
	public synchronized void registerLocalizerEngine(ILocalizerEngine localizerEngine)
	{
		listSOLocalizer.addFirst(localizerEngine);
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

}
