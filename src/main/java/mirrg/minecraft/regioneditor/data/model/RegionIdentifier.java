package mirrg.minecraft.regioneditor.data.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public final class RegionIdentifier implements Comparable<RegionIdentifier>
{

	public static RegionIdentifier decode(JsonElement json) throws ParseException
	{
		try {
			JsonArray array = json.getAsJsonArray();
			return new RegionIdentifier(
				array.get(0).getAsString(),
				array.get(1).getAsString());
		} catch (RuntimeException e) {
			throw new ParseException(e);
		}
	}

	public JsonElement encode()
	{
		JsonArray array = new JsonArray();
		array.add(countryId);
		array.add(stateId);
		return array;
	}

	//

	public final String countryId;
	public final String stateId;

	public RegionIdentifier(String countryId, String stateId)
	{
		this.countryId = countryId;
		this.stateId = stateId;
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
		result = prime * result + countryId.hashCode();
		result = prime * result + stateId.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RegionIdentifier other = (RegionIdentifier) obj;
		if (!countryId.equals(other.countryId)) return false;
		if (!stateId.equals(other.stateId)) return false;
		return true;
	}

	@Override
	public int compareTo(RegionIdentifier other)
	{
		int a;

		a = countryId.compareTo(other.countryId);
		if (a != 0) return a;

		a = stateId.compareTo(other.stateId);
		if (a != 0) return a;

		return 0;
	}

}
