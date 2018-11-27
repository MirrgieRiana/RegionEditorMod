package mirrg.minecraft.regioneditor.data;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

public final class TileMap
{

	private Map<TileIndex, RegionIdentifier> map = new TreeMap<>();

	public Optional<RegionIdentifier> get(TileIndex tileIndex)
	{
		return Optional.ofNullable(map.get(tileIndex));
	}

	public void set(TileIndex tileIndex, Optional<RegionIdentifier> oRegionInfo)
	{
		if (oRegionInfo.isPresent()) {
			map.put(tileIndex, oRegionInfo.get());
		} else {
			map.remove(tileIndex);
		}
	}

	public Set<TileIndex> getKeys()
	{
		return map.keySet();
	}

	public void clear()
	{
		map.clear();
	}

	public TileBoundingBox getBoundingBox()
	{
		int minX = 0;
		int minZ = 0;
		int maxX = 0;
		int maxZ = 0;
		for (TileIndex tileIndex : map.keySet()) {
			if (tileIndex.x < minX) minX = tileIndex.x;
			if (tileIndex.x > maxX) maxX = tileIndex.x;
			if (tileIndex.z < minZ) minZ = tileIndex.z;
			if (tileIndex.z > maxZ) maxZ = tileIndex.z;
		}
		return new TileBoundingBox(new TileIndex(minX, minZ), new TileIndex(maxX, maxZ));
	}

}
