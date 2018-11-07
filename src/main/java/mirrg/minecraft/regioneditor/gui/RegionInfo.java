package mirrg.minecraft.regioneditor.gui;

import java.awt.Color;
import java.text.ParseException;

public final class RegionInfo
{

	public static RegionInfo decode(String code) throws ParseException
	{
		try {
			String[] cells = code.split(",");
			return new RegionInfo(
				Integer.parseInt(cells[0], 10),
				Color.decode(cells[1]),
				cells[2],
				Integer.parseInt(cells[3], 10),
				Color.decode(cells[4]),
				cells[5]);
		} catch (NumberFormatException e) {
			ParseException parseException = new ParseException(code, 0);
			parseException.initCause(e);
			throw parseException;
		}
	}

	public String encode()
	{
		return String.format("%s,%s,%s,%s,%s,%s",
			countryNumber,
			countryColor,
			countryName,
			stateNumber,
			stateColor,
			stateName);
	}

	public final int countryNumber;
	public final Color countryColor;
	public final String countryName;
	public final int stateNumber;
	public final Color stateColor;
	public final String stateName;

	public RegionInfo(
		int countryNumber,
		Color countryColor,
		String countryName,
		int stateNumber,
		Color stateColor,
		String stateName)
	{
		this.countryNumber = countryNumber;
		this.countryColor = countryColor;
		this.countryName = countryName;
		this.stateNumber = stateNumber;
		this.stateColor = stateColor;
		this.stateName = stateName;
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
		final int prime = 31;
		int result = 1;
		result = prime * result + countryNumber;
		result = prime * result + stateNumber;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RegionInfo other = (RegionInfo) obj;
		if (countryNumber != other.countryNumber) return false;
		if (stateNumber != other.stateNumber) return false;
		return true;
	}

}
