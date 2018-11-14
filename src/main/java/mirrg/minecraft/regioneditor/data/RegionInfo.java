package mirrg.minecraft.regioneditor.data;

import java.awt.Color;
import java.text.ParseException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public final class RegionInfo
{

	public static final RegionInfo DEFAULT = new RegionInfo("No Country", new Color(0xFF00FF), "No State", new Color(0xFF00FF));

	public static RegionInfo decode(JsonElement json) throws ParseException
	{
		JsonArray array = json.getAsJsonArray();
		return new RegionInfo(
			array.get(0).getAsString(),
			Color.decode(array.get(1).getAsString()),
			array.get(2).getAsString(),
			Color.decode(array.get(3).getAsString()));
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
		return String.format("%s:%s",
			countryName,
			stateName);
	}

}
