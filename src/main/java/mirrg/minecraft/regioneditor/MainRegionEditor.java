package mirrg.minecraft.regioneditor;

import javax.swing.UIManager;

import mirrg.minecraft.regioneditor.gui.GuiRegionEditor;

public class MainRegionEditor
{

	public static void main(String[] args)
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		new GuiRegionEditor(null).show();
	}

}
