package mirrg.minecraft.regioneditor.data;

import java.util.Optional;

public interface ITileMapWriter
{

	public void set(TileIndex tileIndex, Optional<RegionIdentifier> oRegionInfo);

	public default void set(TileIndex tileIndex, RegionIdentifier regionInfo)
	{
		set(tileIndex, Optional.of(regionInfo));
	}

	public default void remove(TileIndex tileIndex)
	{
		set(tileIndex, Optional.empty());
	}

	public void clear();

}
