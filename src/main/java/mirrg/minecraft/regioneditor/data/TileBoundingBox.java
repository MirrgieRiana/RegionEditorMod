package mirrg.minecraft.regioneditor.data;

public final class TileBoundingBox
{

	public final TilePosition min;
	public final TilePosition max;

	public TileBoundingBox(TilePosition min, TilePosition max)
	{
		this.min = min;
		this.max = max;
	}

	@Override
	public String toString()
	{
		return "[" + min + "],[" + max + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + min.hashCode();
		result = prime * result + max.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		TileBoundingBox other = (TileBoundingBox) obj;
		if (!min.equals(other.min)) return false;
		if (!max.equals(other.max)) return false;
		return true;
	}

	public boolean contains(TilePosition tilePosition)
	{
		if (tilePosition.x < min.x) return false;
		if (tilePosition.x > max.x) return false;
		if (tilePosition.z < min.z) return false;
		if (tilePosition.z > max.z) return false;
		return true;
	}

}
