package mirrg.minecraft.regioneditor;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

import javax.swing.UIManager;

import mirrg.minecraft.regioneditor.gui.GuiRegionEditor;
import mirrg.minecraft.regioneditor.gui.lang.I18n;

public class MainRegionEditor
{

	public static void main(String[] args)
	{

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			I18n.registerLocalizerEngine(I18n.getLocalizerResourceBundle(Locale.ENGLISH));
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			I18n.registerLocalizerEngine(I18n.getLocalizerResourceBundle(Locale.getDefault()));
		} catch (IOException e) {
			System.err.println("Could not load the language file: " + Locale.getDefault().toLanguageTag());
		}

		if (args.length >= 1) {
			try {
				I18n.registerLocalizerEngine(I18n.getLocalizerResourceBundle(Locale.forLanguageTag(args[0].replace('_', '-'))));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		new GuiRegionEditor(null, Optional.empty(), Optional.empty()).show();
	}

}
