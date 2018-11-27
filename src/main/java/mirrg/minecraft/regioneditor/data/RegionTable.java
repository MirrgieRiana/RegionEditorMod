package mirrg.minecraft.regioneditor.data;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class RegionTable
{

	private Map<RegionIdentifier, RegionInfo> map = new TreeMap<>();

	public Set<RegionIdentifier> keySet()
	{
		return map.keySet();
	}

	public Set<Entry<RegionIdentifier, RegionInfo>> entrySet()
	{
		return map.entrySet();
	}

	public void clear()
	{
		map.clear();
	}

	public void put(RegionIdentifier regionIdentifier, RegionInfo regionInfo)
	{
		map.put(regionIdentifier, regionInfo);
	}

	public void remove(RegionIdentifier regionIdentifier)
	{
		map.remove(regionIdentifier);
	}

	public int size()
	{
		return map.size();
	}

	public RegionInfo get(RegionIdentifier regionIdentifier)
	{
		return map.getOrDefault(regionIdentifier, RegionInfo.DEFAULT);
	}

}
