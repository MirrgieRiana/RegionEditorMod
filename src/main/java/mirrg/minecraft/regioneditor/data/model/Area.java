package mirrg.minecraft.regioneditor.data.model;

import mirrg.boron.util.struct.ImmutableArray;

public final class Area
{

	public final RegionEntry regionEntry;
	public final ImmutableArray<TileIndex> vertexes;

	public Area(RegionEntry regionEntry, ImmutableArray<TileIndex> vertexes)
	{
		this.regionEntry = regionEntry;
		this.vertexes = vertexes;
	}

}
