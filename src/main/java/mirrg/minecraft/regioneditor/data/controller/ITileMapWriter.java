package mirrg.minecraft.regioneditor.data.controller;

import java.util.Optional;

import mirrg.minecraft.regioneditor.data.model.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.model.TileIndex;

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
