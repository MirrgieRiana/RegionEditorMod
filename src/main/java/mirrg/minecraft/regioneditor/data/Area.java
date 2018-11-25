package mirrg.minecraft.regioneditor.data;

import mirrg.boron.util.struct.ImmutableArray;

public final class Area
{

	public final RegionEntry regionEntry;
	public final ImmutableArray<TilePosition> vertexes;

	public Area(RegionEntry regionEntry, ImmutableArray<TilePosition> vertexes)
	{
		this.regionEntry = regionEntry;
		this.vertexes = vertexes;
	}

}
