package mirrg.boron.util.i18n;

import java.util.Optional;

import mirrg.boron.util.event.lib.EventProviderRunnable;

public interface ILocalizerEngine
{

	public Optional<ILocalizer> getLocalizer();

	public EventProviderRunnable epChanged();

}
