package mirrg.minecraft.regioneditor.gui.lang;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

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

}
