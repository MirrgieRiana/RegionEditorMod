package mirrg.minecraft.regioneditor;

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

		if (args.length >= 1) {
			I18n.setLocale(args[0]);
		}

		new GuiRegionEditor(null, Optional.empty(), Optional.empty()).show();
	}

}
