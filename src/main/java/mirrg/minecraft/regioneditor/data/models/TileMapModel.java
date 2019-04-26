package mirrg.minecraft.regioneditor.data.models;

import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import mirrg.boron.util.struct.Tuple;
import mirrg.boron.util.suppliterator.ISuppliterator;
import mirrg.minecraft.regioneditor.data.objects.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.objects.TileCoordinate;
import mirrg.minecraft.regioneditor.data.objects.TileRectangle;

public class TileMapModel
{

	private static final Optional<RegionIdentifier> empty = Optional.empty();

	public TreeMap<TileCoordinate, Optional<RegionIdentifier>> map = new TreeMap<>();

	public Optional<RegionIdentifier> getTile(TileCoordinate tileCoordinate)
	{
		return map.getOrDefault(tileCoordinate, empty);
	}

	public TileMapModel subMap(TileRectangle region)
	{
		List<Entry<TileCoordinate, Optional<RegionIdentifier>>> entries = map.entrySet().stream()
			.parallel()
			.filter(e -> region.contains(e.getKey()))
			.collect(Collectors.toList());

		TileMapModel tileMapModel = new TileMapModel();
		ISuppliterator.ofIterable(entries)
			.forEach(e -> tileMapModel.map.put(e.getKey(), e.getValue()));

		return tileMapModel;
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

	public void setTile(TileCoordinate tileCoordinate, Optional<RegionIdentifier> tile)
	{
		if (tile.isPresent()) {
			map.put(tileCoordinate, tile);
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
