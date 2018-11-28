package mirrg.minecraft.regioneditor.data;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class TileMapModel implements ITileMapWriter
{

	private TileMap tileMap;

	public TileMapModel(TileMap tileMap)
	{
		this.tileMap = tileMap;
	}

	public ITileMapReader getDataReader()
	{
		return tileMap;
	}

	//

	private Set<ITileMapListener> listeners = new HashSet<>();

	public void addListener(ITileMapListener listener)
	{
		listeners.add(listener);
	}

	public void removeListener(ITileMapListener listener)
	{
		listeners.remove(listener);
	}

	public void fireChange()
	{
		for (ITileMapListener listener : listeners) {
			listener.onChange();
		}
	}

	public void fireChange(TileIndex tileIndex)
	{
		for (ITileMapListener listener : listeners) {
			listener.onChange(tileIndex);
		}
	}

	//

	public void setData(TileMap tileMap)
	{
		this.tileMap = tileMap;
		fireChange();
	}

	public void modify(Consumer<TileMap> consumer)
	{
		try {
			consumer.accept(tileMap);
		} finally {
			fireChange();
		}
	}

	public void set(TileIndex tileIndex, Optional<RegionIdentifier> oRegionInfo)
	{
		tileMap.set(tileIndex, oRegionInfo);
		fireChange(tileIndex);
	}

	public void clear()
	{
		tileMap.clear();
		fireChange();
	}

}
