package mirrg.minecraft.regioneditor.data.model;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import mirrg.boron.util.struct.Tuple;
import mirrg.boron.util.suppliterator.ISuppliterator;
import mirrg.minecraft.regioneditor.data.controller.ITileMapReader;
import mirrg.minecraft.regioneditor.data.controller.ITileMapWriter;

public class TileMap implements ITileMapReader, ITileMapWriter
{

	private Map<TileIndex, RegionIdentifier> map = new TreeMap<>();

	@Override
	public Optional<RegionIdentifier> get(TileIndex tileIndex)
	{
		return Optional.ofNullable(map.get(tileIndex));
	}

	@Override
	public ISuppliterator<TileIndex> getKeys()
	{
		return ISuppliterator.ofIterable(map.keySet());
	}

	@Override
	public ISuppliterator<Tuple<TileIndex, RegionIdentifier>> getEntries()
	{
		return ISuppliterator.ofIterable(map.entrySet())
			.map(e -> new Tuple<>(e.getKey(), e.getValue()));
	}

	@Override
	public void set(TileIndex tileIndex, Optional<RegionIdentifier> oRegionInfo)
	{
		if (oRegionInfo.isPresent()) {
			map.put(tileIndex, oRegionInfo.get());
		} else {
			map.remove(tileIndex);
		}
	}

	@Override
	public void clear()
	{
		map.clear();
	}

}
