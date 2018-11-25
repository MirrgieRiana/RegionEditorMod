package mirrg.minecraft.regioneditor.data;

public final class TilePosition implements Comparable<TilePosition>
{

	public final int x;
	public final int z;

	public TilePosition(int x, int z)
	{
		this.x = x;
		this.z = z;
	}

	public TilePosition minus(TilePosition tilePosition)
	{
		return minus(tilePosition.x, tilePosition.z);
	}

	public TilePosition minus(int xi, int zi)
	{
		return plus(-xi, -zi);
	}

	public TilePosition plus(TilePosition tilePosition)
	{
		return plus(tilePosition.x, tilePosition.z);
	}

	public TilePosition plus(int xi, int zi)
	{
		return new TilePosition(x + xi, z + zi);
	}

	@Override
	public String toString()
	{
		return x + "," + z;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		TilePosition other = (TilePosition) obj;
		if (x != other.x) return false;
		if (z != other.z) return false;
		return true;
	}

	@Override
	public int compareTo(TilePosition other)
	{
		int a;

		a = z - other.z;
		if (a != 0) return a;

		a = x - other.x;
		if (a != 0) return a;

		return 0;
	}

}
