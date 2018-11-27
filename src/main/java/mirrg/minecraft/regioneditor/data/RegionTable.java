package mirrg.minecraft.regioneditor.data;

import java.util.Map;
import java.util.TreeMap;

import mirrg.boron.util.struct.Tuple;
import mirrg.boron.util.suppliterator.ISuppliterator;

public class RegionTable
{

	private Map<RegionIdentifier, RegionInfo> map = new TreeMap<>();

	public RegionInfo get(RegionIdentifier regionIdentifier)
	{
		return map.getOrDefault(regionIdentifier, RegionInfo.DEFAULT);
	}

	public int size()
	{
		return map.size();
	}

	public ISuppliterator<RegionIdentifier> getKeys()
	{
		return ISuppliterator.ofIterable(map.keySet());
	}

	public ISuppliterator<Tuple<RegionIdentifier, RegionInfo>> getEntries()
	{
		return ISuppliterator.ofIterable(map.entrySet())
			.map(e -> new Tuple<>(e.getKey(), e.getValue()));
	}

	public void put(RegionIdentifier regionIdentifier, RegionInfo regionInfo)
	{
		map.put(regionIdentifier, regionInfo);
	}

	public void remove(RegionIdentifier regionIdentifier)
	{
		map.remove(regionIdentifier);
	}

	public void clear()
	{
		map.clear();
	}

}
