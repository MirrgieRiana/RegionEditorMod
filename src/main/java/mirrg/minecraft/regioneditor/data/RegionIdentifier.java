package mirrg.minecraft.regioneditor.data;

import java.text.ParseException;

public final class RegionIdentifier implements Comparable<RegionIdentifier>
{

	public static RegionIdentifier decode(String code) throws ParseException
	{
		try {
			String[] cells = code.split(":");
			return new RegionIdentifier(cells[0], cells[1]);
		} catch (NumberFormatException e) {
			ParseException parseException = new ParseException(code, 0);
			parseException.initCause(e);
			throw parseException;
		}
	}

	public String encode()
	{
		return String.format("%s:%s",
			countryNumber,
			stateNumber);
	}

	//

	public final String countryNumber;
	public final String stateNumber;

	public RegionIdentifier(String countryNumber, String stateNumber)
	{
		this.countryNumber = countryNumber;
		this.stateNumber = stateNumber;
	}

	//

	@Override
	public String toString()
	{
		return encode();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + countryNumber.hashCode();
		result = prime * result + stateNumber.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RegionIdentifier other = (RegionIdentifier) obj;
		if (!countryNumber.equals(other.countryNumber)) return false;
		if (!stateNumber.equals(other.stateNumber)) return false;
		return true;
	}

	@Override
	public int compareTo(RegionIdentifier other)
	{
		int a;

		a = countryNumber.compareTo(other.countryNumber);
		if (a != 0) return a;

		a = stateNumber.compareTo(other.stateNumber);
		if (a != 0) return a;

		return 0;
	}

}
