package mirrg.minecraft.regioneditor.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RegionMap
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

	public void setRegionInfo(ChunkPosition chunkPosition, RegionInfo regionInfo)
	{
		if (regionInfo != null) {
			map.put(chunkPosition, regionInfo);
		} else {
			map.remove(chunkPosition);
		}
	}

	public void clearRegionInfo(ChunkPosition chunkPosition)
	{
		map.remove(chunkPosition);
	}

}
