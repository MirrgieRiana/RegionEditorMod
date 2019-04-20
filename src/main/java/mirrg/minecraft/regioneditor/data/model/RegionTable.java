package mirrg.minecraft.regioneditor.data.model;

import java.util.Map;
import java.util.TreeMap;

import mirrg.boron.util.struct.Tuple;
import mirrg.boron.util.suppliterator.ISuppliterator;
import mirrg.minecraft.regioneditor.data.controller.IRegionTableReader;
import mirrg.minecraft.regioneditor.data.controller.IRegionTableWriter;

public class RegionTable implements IRegionTableReader, IRegionTableWriter
{

	private Map<RegionIdentifier, RegionInfo> map = new TreeMap<>();

	@Override
	public RegionInfo get(RegionIdentifier regionIdentifier)
	{
		return map.getOrDefault(regionIdentifier, RegionInfo.DEFAULT);
	}

	@Override
	public int size()
	{
		return map.size();
	}

	@Override
	public ISuppliterator<RegionIdentifier> getKeys()
	{
		return ISuppliterator.ofIterable(map.keySet());
	}

	@Override
	public ISuppliterator<Tuple<RegionIdentifier, RegionInfo>> getEntries()
	{
		return ISuppliterator.ofIterable(map.entrySet())
			.map(e -> new Tuple<>(e.getKey(), e.getValue()));
	}

	@Override
	public void put(RegionIdentifier regionIdentifier, RegionInfo regionInfo)
	{
		map.put(regionIdentifier, regionInfo);
	}

	@Override
	public void remove(RegionIdentifier regionIdentifier)
	{
		map.remove(regionIdentifier);
	}

	@Override
	public void clear()
	{
		map.clear();
	}

}
