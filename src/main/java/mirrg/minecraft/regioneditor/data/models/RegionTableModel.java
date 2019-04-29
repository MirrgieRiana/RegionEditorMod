package mirrg.minecraft.regioneditor.data.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import mirrg.boron.util.suppliterator.ISuppliterator;
import mirrg.boron.util.suppliterator.ISuppliterator.IndexedObject;
import mirrg.minecraft.regioneditor.data.ModelException;
import mirrg.minecraft.regioneditor.data.objects.RegionEntry;
import mirrg.minecraft.regioneditor.data.objects.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.objects.RegionInfo;

public class RegionTableModel
{

	protected Map<RegionIdentifier, RegionInfo> map = new HashMap<>();
	protected final List<RegionEntry> list = new ArrayList<>();

	// getters

	public RegionInfo get(RegionIdentifier regionIdentifier)
	{
		return map.getOrDefault(regionIdentifier, RegionInfo.DEFAULT);
	}

	public boolean containsKey(RegionIdentifier regionIdentifier)
	{
		return map.containsKey(regionIdentifier);
	}

	public int size()
	{
		return list.size();
	}

	public ISuppliterator<RegionIdentifier> getKeys()
	{
		return ISuppliterator.ofIterable(list)
			.map(re -> re.regionIdentifier);
	}

	public ISuppliterator<RegionEntry> getEntries()
	{
		return ISuppliterator.ofIterable(list);
	}

	private int getIndex(RegionIdentifier regionIdentifier)
	{
		Optional<IndexedObject<RegionEntry>> result = ISuppliterator.ofIterable(list)
			.findWithIndex(re -> re.regionIdentifier.equals(regionIdentifier));
		return result.isPresent() ? result.get().index : -1;
	}

	// setters

	public void set(RegionIdentifier regionIdentifier, RegionInfo regionInfo)
	{
		RegionEntry regionEntry = new RegionEntry(regionIdentifier, regionInfo);

		if (containsKey(regionIdentifier)) {
			map.put(regionIdentifier, regionInfo);
			list.set(getIndex(regionIdentifier), regionEntry);
		} else {
			map.put(regionIdentifier, regionInfo);
			list.add(regionEntry);
		}
	}

	public void remove(RegionIdentifier regionIdentifier) throws ModelException
	{
		if (containsKey(regionIdentifier)) {
			map.remove(regionIdentifier);
			list.remove(getIndex(regionIdentifier));
		} else {
			throw new ModelException();
		}
	}

	public void clear()
	{
		map.clear();
		list.clear();
	}

}
