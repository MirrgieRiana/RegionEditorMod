package mirrg.minecraft.regioneditor.data.controller;

import mirrg.boron.util.struct.Tuple;
import mirrg.boron.util.suppliterator.ISuppliterator;
import mirrg.minecraft.regioneditor.data.model.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.model.RegionInfo;

public interface IRegionTableReader
{

	public RegionInfo get(RegionIdentifier regionIdentifier);

	public int size();

	public ISuppliterator<RegionIdentifier> getKeys();

	public ISuppliterator<Tuple<RegionIdentifier, RegionInfo>> getEntries();

}
