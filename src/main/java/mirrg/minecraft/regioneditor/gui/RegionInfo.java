package mirrg.minecraft.regioneditor.gui;

import java.awt.Color;
import java.text.ParseException;

public final class RegionInfo implements Comparable<RegionInfo>
{

	public static RegionInfo decode(String code) throws ParseException
	{
		try {
			String[] a = code.split(",");
			String[] b = a[1].split(":");
			return new RegionInfo(
				RegionIdentifier.decode(a[0]),
				b[0],
				Color.decode(b[1]),
				b[2],
				Color.decode(b[3]));
		} catch (NumberFormatException e) {
			ParseException parseException = new ParseException(code, 0);
			parseException.initCause(e);
			throw parseException;
		}
	}

	public String encode()
	{
		return String.format("%s,%s:#%06x:%s:#%06x",
			regionIdentifier.encode(),
			countryName,
			countryColor.getRGB() & 0xffffff,
			stateName,
			stateColor.getRGB() & 0xffffff);
	}

	public final RegionIdentifier regionIdentifier;
	public final String countryName;
	public final Color countryColor;
	public final String stateName;
	public final Color stateColor;

	public RegionInfo(
		RegionIdentifier regionIdentifier,
		String countryName,
		Color countryColor,
		String stateName,
		Color stateColor)
	{
		this.regionIdentifier = regionIdentifier;
		this.countryName = countryName;
		this.countryColor = countryColor;
		this.stateName = stateName;
		this.stateColor = stateColor;
	}

	public Color getDynmapColor()
	{
		return new Color(
			(countryColor.getRed() * 3 + stateColor.getRed() * 1) / 4,
			(countryColor.getGreen() * 3 + stateColor.getGreen() * 1) / 4,
			(countryColor.getBlue() * 3 + stateColor.getBlue() * 1) / 4);
	}

	@Override
	public String toString()
	{
		return countryName + ":" + stateName;
	}

	@Override
	public int hashCode()
	{
		return regionIdentifier.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RegionInfo other = (RegionInfo) obj;
		if (!regionIdentifier.equals(other.regionIdentifier)) return false;
		return true;
	}

	@Override
	public int compareTo(RegionInfo other)
	{
		return regionIdentifier.compareTo(other.regionIdentifier);
	}

}
