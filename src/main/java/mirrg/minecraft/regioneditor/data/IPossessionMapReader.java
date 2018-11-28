package mirrg.minecraft.regioneditor.data;

import mirrg.boron.util.struct.ImmutableArray;

public interface IPossessionMapReader
{

	public IRegionTableReader getRegionTableReader();

	public ITileMapReader getTileMapReader();

	public ImmutableArray<Area> getAreas();

}
