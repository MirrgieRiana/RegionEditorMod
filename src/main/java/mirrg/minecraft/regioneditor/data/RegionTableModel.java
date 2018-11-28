package mirrg.minecraft.regioneditor.data;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class RegionTableModel implements IRegionTableWriter
{

	private RegionTable regionTable;

	public RegionTableModel(RegionTable regionTable)
	{
		this.regionTable = regionTable;
	}

	public IRegionTableReader getDataReader()
	{
		return regionTable;
	}

	//

	private Set<IRegionTableListener> listeners = new HashSet<>();

	public void addListener(IRegionTableListener listener)
	{
		listeners.add(listener);
	}

	public void removeListener(IRegionTableListener listener)
	{
		listeners.remove(listener);
	}

	public void fireChange()
	{
		for (IRegionTableListener listener : listeners) {
			listener.onChange();
		}
	}

	//

	public void setData(RegionTable regionTable)
	{
		this.regionTable = regionTable;
		fireChange();
	}

	public void modify(Consumer<RegionTable> consumer)
	{
		try {
			consumer.accept(regionTable);
		} finally {
			fireChange();
		}
	}

	@Override
	public void put(RegionIdentifier regionIdentifier, RegionInfo regionInfo)
	{
		regionTable.put(regionIdentifier, regionInfo);
		fireChange();
	}

	@Override
	public void remove(RegionIdentifier regionIdentifier)
	{
		regionTable.remove(regionIdentifier);
		fireChange();
	}

	@Override
	public void clear()
	{
		regionTable.clear();
		fireChange();
	}

}
