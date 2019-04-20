package mirrg.minecraft.regioneditor.data.controller;

import mirrg.minecraft.regioneditor.data.model.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.model.RegionInfo;

public interface IRegionTableWriter
{

	public void put(RegionIdentifier regionIdentifier, RegionInfo regionInfo);

	public void remove(RegionIdentifier regionIdentifier);

	public void clear();

}
