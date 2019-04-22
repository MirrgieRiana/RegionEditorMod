package mirrg.minecraft.regioneditor.data.objects;

import mirrg.boron.util.struct.ImmutableArray;

public final class Area
{

	public final RegionEntry regionEntry;
	public final ImmutableArray<TileCoordinate> tileCoordinates;

	public Area(RegionEntry regionEntry, ImmutableArray<TileCoordinate> tileCoordinates)
	{
		this.regionEntry = regionEntry;
		this.tileCoordinates = tileCoordinates;
	}

}
