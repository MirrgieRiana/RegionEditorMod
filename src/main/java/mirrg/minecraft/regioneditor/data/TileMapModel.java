package mirrg.minecraft.regioneditor.data;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class TileMapModel
{

	private TileMap tileMap = new TileMap();
	private Set<Runnable> listeners = new HashSet<>();

	public TileMap getTileMap()
	{
		return tileMap;
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
