package mirrg.minecraft.regioneditor.data;

public final class ChunkPosition implements Comparable<ChunkPosition>
{

	public final int x;
	public final int z;

	public ChunkPosition(int x, int z)
	{
		this.x = x;
		this.z = z;
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
		ChunkPosition other = (ChunkPosition) obj;
		if (x != other.x) return false;
		if (z != other.z) return false;
		return true;
	}

	@Override
	public int compareTo(ChunkPosition other)
	{
		int a;

		a = z - other.z;
		if (a != 0) return a;

		a = x - other.x;
		if (a != 0) return a;

		return 0;
	}

}
