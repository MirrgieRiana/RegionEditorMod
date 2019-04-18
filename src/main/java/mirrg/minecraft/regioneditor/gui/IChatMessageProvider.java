package mirrg.minecraft.regioneditor.gui;

import mirrg.boron.util.struct.ImmutableArray;

public interface IChatMessageProvider
{

	public void startCapture(String command);

	public ImmutableArray<String> stopCapture();

}
