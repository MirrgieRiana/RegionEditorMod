package mirrg.minecraft.regioneditor.data;

import java.util.Optional;

import mirrg.boron.util.struct.Tuple;
import mirrg.boron.util.suppliterator.ISuppliterator;

public interface ITileMapReader
{

	public Optional<RegionIdentifier> get(TileIndex tileIndex);

	public ISuppliterator<TileIndex> getKeys();

	public ISuppliterator<Tuple<TileIndex, RegionIdentifier>> getEntries();

	public default TileBoundingBox getBoundingBox()
	{
		int minX = 0;
		int minZ = 0;
		int maxX = 0;
		int maxZ = 0;
		for (TileIndex tileIndex : getKeys()) {
			if (tileIndex.x < minX) minX = tileIndex.x;
			if (tileIndex.x > maxX) maxX = tileIndex.x;
			if (tileIndex.z < minZ) minZ = tileIndex.z;
			if (tileIndex.z > maxZ) maxZ = tileIndex.z;
		}
		return new TileBoundingBox(new TileIndex(minX, minZ), new TileIndex(maxX, maxZ));
	}

}
