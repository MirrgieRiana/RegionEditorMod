package mirrg.minecraft.regioneditor.data;

import mirrg.boron.util.struct.Tuple;
import mirrg.boron.util.suppliterator.ISuppliterator;

public interface IRegionTableReader
{

	public RegionInfo get(RegionIdentifier regionIdentifier);

	public int size();

	public ISuppliterator<RegionIdentifier> getKeys();

	public ISuppliterator<Tuple<RegionIdentifier, RegionInfo>> getEntries();

}
