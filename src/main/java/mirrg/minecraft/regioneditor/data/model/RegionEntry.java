package mirrg.minecraft.regioneditor.data.model;

public final class RegionEntry
{

	public final RegionIdentifier regionIdentifier;
	public final RegionInfo regionInfo;

	public RegionEntry(RegionIdentifier regionIdentifier, RegionInfo regionInfo)
	{
		this.regionIdentifier = regionIdentifier;
		this.regionInfo = regionInfo;
	}

	@Override
	public String toString()
	{
		return String.format("(%s)%s:(%s)%s",
			regionIdentifier.countryNumber,
			regionInfo.countryName,
			regionIdentifier.stateNumber,
			regionInfo.stateName);
	}

}
