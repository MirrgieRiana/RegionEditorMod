package mirrg.minecraft.regioneditor.data.objects;

public final class TileRectangle
{

	public final TileCoordinate min;

	/**
	 * 範囲はこの座標を含みます。
	 */
	public final TileCoordinate max;

	public TileRectangle(TileCoordinate min, TileCoordinate max)
	{
		this.min = min;
		this.max = max;
	}

	public TileRectangle(int minX, int minZ, int maxX, int maxZ)
	{
		this.min = new TileCoordinate(minX, minZ);
		this.max = new TileCoordinate(maxX, maxZ);
	}

	@Override
	public String toString()
	{
		return "[" + min + "," + max + "]";
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
		TileRectangle other = (TileRectangle) obj;
		if (!min.equals(other.min)) return false;
		if (!max.equals(other.max)) return false;
		return true;
	}

	public boolean contains(TileCoordinate tileCoordinate)
	{
		if (tileCoordinate.x < min.x) return false;
		if (tileCoordinate.x > max.x) return false;
		if (tileCoordinate.z < min.z) return false;
		if (tileCoordinate.z > max.z) return false;
		return true;
	}

}
