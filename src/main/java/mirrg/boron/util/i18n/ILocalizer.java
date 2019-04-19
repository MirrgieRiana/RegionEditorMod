package mirrg.boron.util.i18n;

public interface ILocalizer
{

	public boolean canLocalize(String unlocalizedString);

	public String localize(String unlocalizedString);

}
