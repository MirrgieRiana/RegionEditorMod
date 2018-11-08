package mirrg.minecraft.regioneditor.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class RegionMap
{

	private Map<ChunkPosition, RegionIdentifier> map = new HashMap<>();

	public Optional<RegionIdentifier> get(ChunkPosition chunkPosition)
	{
		return Optional.ofNullable(map.get(chunkPosition));
	}

	public void set(ChunkPosition chunkPosition, Optional<RegionIdentifier> oRegionInfo)
	{
		if (oRegionInfo.isPresent()) {
			map.put(chunkPosition, oRegionInfo.get());
		} else {
			map.remove(chunkPosition);
		}
	}

}
