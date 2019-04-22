package mirrg.minecraft.regioneditor.data.models;

import java.util.Optional;
import java.util.TreeMap;

import mirrg.boron.util.struct.Tuple;
import mirrg.boron.util.suppliterator.ISuppliterator;
import mirrg.minecraft.regioneditor.data.objects.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.objects.TileCoordinate;

public class TileMapModel
{

	private static final Optional<RegionIdentifier> empty = Optional.empty();

	public TreeMap<TileCoordinate, Optional<RegionIdentifier>> map = new TreeMap<>();

	public Optional<RegionIdentifier> get(TileCoordinate tileCoordinate)
	{
		return map.getOrDefault(tileCoordinate, empty);
	}

	public int size()
	{
		return map.size();
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

	public void remove(TileCoordinate tileCoordinate)
	{
		map.remove(tileCoordinate);
	}

	public void clear()
	{
		map.clear();
	}

}
