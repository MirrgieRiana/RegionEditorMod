package mirrg.minecraft.regioneditor.data;

import mirrg.boron.util.struct.ImmutableArray;

public final class Area
{

	public final RegionInfo regionInfo;
	public final ImmutableArray<ChunkPosition> vertexes;

	public Area(RegionInfo regionInfo, ImmutableArray<ChunkPosition> vertexes)
	{
		this.regionInfo = regionInfo;
		this.vertexes = vertexes;
	}

}
