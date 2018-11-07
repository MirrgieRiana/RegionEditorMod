package mirrg.minecraft.regioneditor.gui;

public final class ChunkPosition
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

}
