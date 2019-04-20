package mirrg.minecraft.regioneditor.data.model;

import java.util.Optional;
import java.util.TreeMap;

import mirrg.boron.util.struct.Tuple;
import mirrg.boron.util.suppliterator.ISuppliterator;

public class TileMap
{

	private static final Optional<RegionIdentifier> empty = Optional.empty();

	private TreeMap<TileCoordinate, Optional<RegionIdentifier>> map = new TreeMap<>();

	public Optional<RegionIdentifier> get(TileCoordinate tileCoordinate)
	{
		return map.getOrDefault(tileCoordinate, empty);
	}

	public ISuppliterator<TileCoordinate> getKeys()
	{
		return ISuppliterator.ofIterable(map.keySet());
	}

	public ISuppliterator<Tuple<TileCoordinate, RegionIdentifier>> getEntries()
	{
		return ISuppliterator.ofIterable(map.entrySet())
			.map(e -> new Tuple<>(e.getKey(), e.getValue().get()));
	}

	public void set(TileCoordinate tileCoordinate, Optional<RegionIdentifier> oRegionInfo)
	{
		if (oRegionInfo.isPresent()) {
			map.put(tileCoordinate, oRegionInfo);
		} else {
			map.remove(tileCoordinate);
		}
	}

	public void clear()
	{
		map.clear();
	}

}
