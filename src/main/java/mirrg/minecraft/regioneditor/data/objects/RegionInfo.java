package mirrg.minecraft.regioneditor.data.objects;

import java.awt.Color;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import mirrg.minecraft.regioneditor.data.ParseException;

public final class RegionInfo
{

	public static final RegionInfo DEFAULT = new RegionInfo("No Country", new Color(0xFF00FF), "No State", new Color(0xFF00FF));

	public static RegionInfo decode(JsonElement json) throws ParseException
	{
		try {
			JsonArray array = json.getAsJsonArray();
			return new RegionInfo(
				array.get(0).getAsString(),
				Color.decode(array.get(1).getAsString()),
				array.get(2).getAsString(),
				Color.decode(array.get(3).getAsString()));
		} catch (RuntimeException e) {
			throw new ParseException(e);
		}
	}

	public JsonElement encode()
	{
		JsonArray array = new JsonArray();
		array.add(countryName);
		array.add(String.format("#%06x", countryColor.getRGB() & 0xffffff));
		array.add(stateName);
		array.add(String.format("#%06x", stateColor.getRGB() & 0xffffff));
		return array;
	}

	//

	public final String countryName;
	public final Color countryColor;
	public final String stateName;
	public final Color stateColor;

	public RegionInfo(
		String countryName,
		Color countryColor,
		String stateName,
		Color stateColor)
	{
		this.countryName = countryName;
		this.countryColor = countryColor;
		this.stateName = stateName;
		this.stateColor = stateColor;
	}

	@Override
	public String toString()
	{
		return String.format("%s:%s",
			countryName,
			stateName);
	}

}
