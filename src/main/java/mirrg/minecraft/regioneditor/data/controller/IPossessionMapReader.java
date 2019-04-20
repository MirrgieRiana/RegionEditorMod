package mirrg.minecraft.regioneditor.data.controller;

import mirrg.boron.util.struct.ImmutableArray;
import mirrg.minecraft.regioneditor.data.model.Area;

public interface IPossessionMapReader
{

	public IRegionTableReader getRegionTableReader();

	public ITileMapReader getTileMapReader();

	public ImmutableArray<Area> getAreas();

}
