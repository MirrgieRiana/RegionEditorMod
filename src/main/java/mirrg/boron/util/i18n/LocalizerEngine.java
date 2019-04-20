package mirrg.boron.util.i18n;

import java.util.Optional;

import mirrg.boron.util.event.lib.EventProviderRunnable;

public class LocalizerEngine implements ILocalizerEngine
{

	private Optional<ILocalizer> oLocalizer = Optional.empty();

	public LocalizerEngine()
	{
		this(null);
	}

	/**
	 * @param localizer
	 *            nullable
	 */
	public LocalizerEngine(ILocalizer localizer)
	{
		this.oLocalizer = Optional.ofNullable(localizer);
	}

	/**
	 * @param localizer
	 *            nullable
	 */
	public void setLocalizer(ILocalizer localizer)
	{
		oLocalizer = Optional.ofNullable(localizer);
		epChanged.trigger().run();
	}

	@Override
	public Optional<ILocalizer> getLocalizer()
	{
		return oLocalizer;
	}

	private EventProviderRunnable epChanged = new EventProviderRunnable();

	@Override
	public EventProviderRunnable epChanged()
	{
		return epChanged;
	}

}
