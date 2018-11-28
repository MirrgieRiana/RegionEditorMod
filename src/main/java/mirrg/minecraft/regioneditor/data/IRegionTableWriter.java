package mirrg.minecraft.regioneditor.data;

public interface IRegionTableWriter
{

	public void put(RegionIdentifier regionIdentifier, RegionInfo regionInfo);

	public void remove(RegionIdentifier regionIdentifier);

	public void clear();

}
