package mirrg.minecraft.regioneditor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Locale;
import java.util.Optional;

import javax.swing.UIManager;

import mirrg.boron.util.i18n.I18n;
import mirrg.boron.util.i18n.localizers.LocalizerResourceBundle;
import mirrg.minecraft.regioneditor.gui.GuiRegionEditor;

public class MainRegionEditor
{

	public static final String I18N_BASENAME = MainRegionEditor.class.getPackage().getName() + ".lang.gui";
	public static I18n i18n = new I18n();

	public static void main(String[] args)
	{

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			i18n.registerLocalizer(LocalizerResourceBundle.create(I18N_BASENAME, Locale.ENGLISH));
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			i18n.registerLocalizer(LocalizerResourceBundle.create(I18N_BASENAME, Locale.getDefault()));
		} catch (IOException e) {
			System.err.println("Could not load the language file: " + Locale.getDefault().toLanguageTag());
		}

		if (args.length >= 1) {
			try {
				i18n.registerLocalizer(LocalizerResourceBundle.create(I18N_BASENAME, Locale.forLanguageTag(args[0].replace('_', '-'))));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			new GuiRegionEditor(null, i18n, Optional.empty(), Optional.empty()).show();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

	}

}
