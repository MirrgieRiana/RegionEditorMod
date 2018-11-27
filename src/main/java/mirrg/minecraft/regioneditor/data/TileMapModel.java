package mirrg.minecraft.regioneditor.data;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import mirrg.boron.util.struct.Tuple;
import mirrg.boron.util.suppliterator.ISuppliterator;

public class TileMapModel
{

	private TileMap tileMap = new TileMap();
	private Set<Runnable> listeners = new HashSet<>();

	public TileMap getTileMap()
	{
		return tileMap;
	}

	public Optional<RegionIdentifier> get(TileIndex tileIndex)
	{
		return tileMap.get(tileIndex);
	}

	public ISuppliterator<TileIndex> getKeys()
	{
		return tileMap.getKeys();
	}

	public ISuppliterator<Tuple<TileIndex, RegionIdentifier>> getEntries()
	{
		return tileMap.getEntries();
	}

	public TileBoundingBox getBoundingBox()
	{
		return tileMap.getBoundingBox();
	}

	public void setTileMap(TileMap tileMap)
	{
		this.tileMap = tileMap;
		fireChangeEvent();
	}

	public void set(TileIndex tileIndex, Optional<RegionIdentifier> oRegionInfo)
	{
		tileMap.set(tileIndex, oRegionInfo);
		fireChangeEvent();
	}

	public void clear()
	{
		tileMap.clear();
		fireChangeEvent();
	}

	public void addListener(Runnable listener)
	{
		listeners.add(listener);
	}

	public void removeListener(Runnable listener)
	{
		listeners.remove(listener);
	}

	public void fireChangeEvent()
	{
		for (Runnable listener : listeners) {
			listener.run();
		}
	}

}
