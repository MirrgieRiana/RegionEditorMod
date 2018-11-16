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

	public ChunkBoundingBox getBoundingBox()
	{
		int minX = 0;
		int minZ = 0;
		int maxX = 0;
		int maxZ = 0;
		for (ChunkPosition chunkPosition : map.keySet()) {
			if (chunkPosition.x < minX) minX = chunkPosition.x;
			if (chunkPosition.x > maxX) maxX = chunkPosition.x;
			if (chunkPosition.z < minZ) minZ = chunkPosition.z;
			if (chunkPosition.z > maxZ) maxZ = chunkPosition.z;
		}
		return new ChunkBoundingBox(new ChunkPosition(minX, minZ), new ChunkPosition(maxX, maxZ));
	}

}
