package mirrg.minecraft.regioneditor.data;

public final class TileIndex implements Comparable<TileIndex>
{

	public final int x;
	public final int z;

	public TileIndex(int x, int z)
	{
		this.x = x;
		this.z = z;
	}

	public TileIndex minus(TileIndex tileIndex)
	{
		return minus(tileIndex.x, tileIndex.z);
	}

	public TileIndex minus(int xi, int zi)
	{
		return plus(-xi, -zi);
	}

	public TileIndex plus(TileIndex tileIndex)
	{
		return plus(tileIndex.x, tileIndex.z);
	}

	public TileIndex plus(int xi, int zi)
	{
		return new TileIndex(x + xi, z + zi);
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
		TileIndex other = (TileIndex) obj;
		if (x != other.x) return false;
		if (z != other.z) return false;
		return true;
	}

	@Override
	public int compareTo(TileIndex other)
	{
		int a;

		a = z - other.z;
		if (a != 0) return a;

		a = x - other.x;
		if (a != 0) return a;

		return 0;
	}

}
