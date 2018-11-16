package mirrg.minecraft.regioneditor.data;

public final class ChunkBoundingBox
{

	public final ChunkPosition min;
	public final ChunkPosition max;

	public ChunkBoundingBox(ChunkPosition min, ChunkPosition max)
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
		ChunkBoundingBox other = (ChunkBoundingBox) obj;
		if (!min.equals(other.min)) return false;
		if (!max.equals(other.max)) return false;
		return true;
	}

	public boolean contains(ChunkPosition chunkPosition)
	{
		if (chunkPosition.x < min.x) return false;
		if (chunkPosition.x > max.x) return false;
		if (chunkPosition.z < min.z) return false;
		if (chunkPosition.z > max.z) return false;
		return true;
	}

}
