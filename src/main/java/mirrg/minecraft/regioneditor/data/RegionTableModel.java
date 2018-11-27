package mirrg.minecraft.regioneditor.data;

import java.util.HashSet;
import java.util.Set;

public class RegionTableModel
{

	private RegionTable regionTable = new RegionTable();
	private Set<Runnable> listeners = new HashSet<>();

	public RegionTable getRegionTable()
	{
		return regionTable;
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
