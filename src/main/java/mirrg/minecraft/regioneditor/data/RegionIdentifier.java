package mirrg.minecraft.regioneditor.data;

import java.text.ParseException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public final class RegionIdentifier implements Comparable<RegionIdentifier>
{

	public static RegionIdentifier decode(JsonElement json) throws ParseException
	{
		JsonArray array = json.getAsJsonArray();
		return new RegionIdentifier(
			array.get(0).getAsString(),
			array.get(1).getAsString());
	}

	public JsonElement encode()
	{
		JsonArray array = new JsonArray();
		array.add(countryNumber);
		array.add(stateNumber);
		return array;
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
		return encode().toString();
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
