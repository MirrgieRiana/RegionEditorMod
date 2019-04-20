package mirrg.minecraft.regioneditor.data.controller;

import mirrg.minecraft.regioneditor.data.model.TileIndex;

public interface ITileMapListener
{

	public void onChange();

	public void onChange(TileIndex tileIndex);

}
