package mirrg.minecraft.regioneditor.data;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import mirrg.boron.util.struct.Tuple;
import mirrg.boron.util.suppliterator.ISuppliterator;

public class TileMap
{

	private Map<TileIndex, RegionIdentifier> map = new TreeMap<>();

	public Optional<RegionIdentifier> get(TileIndex tileIndex)
	{
		return Optional.ofNullable(map.get(tileIndex));
	}

	public ISuppliterator<TileIndex> getKeys()
	{
		return ISuppliterator.ofIterable(map.keySet());
	}

	public ISuppliterator<Tuple<TileIndex, RegionIdentifier>> getEntries()
	{
		return ISuppliterator.ofIterable(map.entrySet())
			.map(e -> new Tuple<>(e.getKey(), e.getValue()));
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

	public void set(TileIndex tileIndex, Optional<RegionIdentifier> oRegionInfo)
	{
		if (oRegionInfo.isPresent()) {
			map.put(tileIndex, oRegionInfo.get());
		} else {
			map.remove(tileIndex);
		}
	}

	public void clear()
	{
		map.clear();
	}

}
