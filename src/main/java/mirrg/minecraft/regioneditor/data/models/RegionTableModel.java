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

	public void insert(RegionIdentifier regionIdentifier, RegionIdentifier regionIdentifierNew, RegionInfo regionInfo) throws ModelException
	{
		if (!containsKey(regionIdentifier)) throw new ModelException();
		if (containsKey(regionIdentifierNew)) throw new ModelException();

		int index = getIndex(regionIdentifier);
		map.put(regionIdentifierNew, regionInfo);
		list.add(index + 1, new RegionEntry(regionIdentifierNew, regionInfo));
	}

	public void insertFirst(RegionIdentifier regionIdentifierNew, RegionInfo regionInfo) throws ModelException
	{
		if (containsKey(regionIdentifierNew)) throw new ModelException();

		map.put(regionIdentifierNew, regionInfo);
		list.add(0, new RegionEntry(regionIdentifierNew, regionInfo));
	}

	public void insertLast(RegionIdentifier regionIdentifierNew, RegionInfo regionInfo) throws ModelException
	{
		if (containsKey(regionIdentifierNew)) throw new ModelException();

		map.put(regionIdentifierNew, regionInfo);
		list.add(new RegionEntry(regionIdentifierNew, regionInfo));
	}

	public void replaceKey(RegionIdentifier regionIdentifierOld, RegionIdentifier regionIdentifierNew) throws ModelException
	{
		if (!containsKey(regionIdentifierOld)) throw new ModelException();
		if (containsKey(regionIdentifierNew)) throw new ModelException();

		RegionInfo regionInfo = get(regionIdentifierOld);
		map.remove(regionIdentifierOld);
		map.put(regionIdentifierNew, regionInfo);
		list.set(getIndex(regionIdentifierOld), new RegionEntry(regionIdentifierNew, regionInfo));
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
