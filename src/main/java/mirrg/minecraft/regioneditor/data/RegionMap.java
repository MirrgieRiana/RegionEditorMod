package mirrg.minecraft.regioneditor.data;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

public final class RegionMap
{

	private Map<ChunkPosition, RegionIdentifier> map = new TreeMap<>();

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

	public Set<ChunkPosition> getKeys()
	{
		return map.keySet();
	}

	public void clear()
	{
		map.clear();
	}

}
