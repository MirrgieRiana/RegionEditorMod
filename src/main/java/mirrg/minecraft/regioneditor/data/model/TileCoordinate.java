package mirrg.minecraft.regioneditor.data.model;

public final class TileCoordinate implements Comparable<TileCoordinate>
{

	public final int x;
	public final int z;

	public TileCoordinate(int x, int z)
	{
		this.x = x;
		this.z = z;
	}

	public TileCoordinate minus(TileCoordinate tileCoordinate)
	{
		return minus(tileCoordinate.x, tileCoordinate.z);
	}

	public TileCoordinate minus(int xi, int zi)
	{
		return plus(-xi, -zi);
	}

	public TileCoordinate plus(TileCoordinate tileCoordinate)
	{
		return plus(tileCoordinate.x, tileCoordinate.z);
	}

	public TileCoordinate plus(int xi, int zi)
	{
		return new TileCoordinate(x + xi, z + zi);
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
		TileCoordinate other = (TileCoordinate) obj;
		if (x != other.x) return false;
		if (z != other.z) return false;
		return true;
	}

	@Override
	public int compareTo(TileCoordinate other)
	{
		int a;

		a = Integer.compare(z, other.z);
		if (a != 0) return a;

		a = Integer.compare(x, other.x);
		if (a != 0) return a;

		return 0;
	}

}
