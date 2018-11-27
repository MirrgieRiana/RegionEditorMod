package mirrg.minecraft.regioneditor.data;

import java.util.HashSet;
import java.util.Set;

import mirrg.boron.util.struct.Tuple;
import mirrg.boron.util.suppliterator.ISuppliterator;

public class RegionTableModel
{

	private RegionTable regionTable = new RegionTable();
	private Set<Runnable> listeners = new HashSet<>();

	public RegionTable getRegionTable()
	{
		return regionTable;
	}

	public RegionInfo get(RegionIdentifier regionIdentifier)
	{
		return regionTable.get(regionIdentifier);
	}

	public int size()
	{
		return regionTable.size();
	}

	public ISuppliterator<RegionIdentifier> getKeys()
	{
		return regionTable.getKeys();
	}

	public ISuppliterator<Tuple<RegionIdentifier, RegionInfo>> getEntries()
	{
		return regionTable.getEntries();
	}

	public void setRegionTable(RegionTable regionTable)
	{
		this.regionTable = regionTable;
		fireChangeEvent();
	}

	public void put(RegionIdentifier regionIdentifier, RegionInfo regionInfo)
	{
		regionTable.put(regionIdentifier, regionInfo);
		fireChangeEvent();
	}

	public void remove(RegionIdentifier regionIdentifier)
	{
		regionTable.remove(regionIdentifier);
		fireChangeEvent();
	}

	public void clear()
	{
		regionTable.clear();
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
