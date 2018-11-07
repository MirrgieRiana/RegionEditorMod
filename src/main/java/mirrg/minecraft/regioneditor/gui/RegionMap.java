package mirrg.minecraft.regioneditor.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class RegionMap
{

	private Map<ChunkPosition, RegionInfo> map = new HashMap<>();

	public Optional<RegionInfo> getRegionInfo(ChunkPosition chunkPosition)
	{
		return Optional.ofNullable(map.get(chunkPosition));
	}

	public void setRegionInfo(ChunkPosition chunkPosition, Optional<RegionInfo> oRegionInfo)
	{
		if (oRegionInfo.isPresent()) {
			map.put(chunkPosition, oRegionInfo.get());
		} else {
			map.remove(chunkPosition);
		}
	}

}
