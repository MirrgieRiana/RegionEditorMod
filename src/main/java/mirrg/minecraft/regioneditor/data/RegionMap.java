package mirrg.minecraft.regioneditor.data;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

public final class RegionMap
{

	private Map<TilePosition, RegionIdentifier> map = new TreeMap<>();

	public Optional<RegionIdentifier> get(TilePosition tilePosition)
	{
		return Optional.ofNullable(map.get(tilePosition));
	}

	public void set(TilePosition tilePosition, Optional<RegionIdentifier> oRegionInfo)
	{
		if (oRegionInfo.isPresent()) {
			map.put(tilePosition, oRegionInfo.get());
		} else {
			map.remove(tilePosition);
		}
	}

	public Set<TilePosition> getKeys()
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
		for (TilePosition tilePosition : map.keySet()) {
			if (tilePosition.x < minX) minX = tilePosition.x;
			if (tilePosition.x > maxX) maxX = tilePosition.x;
			if (tilePosition.z < minZ) minZ = tilePosition.z;
			if (tilePosition.z > maxZ) maxZ = tilePosition.z;
		}
		return new TileBoundingBox(new TilePosition(minX, minZ), new TilePosition(maxX, maxZ));
	}

}
