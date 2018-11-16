package mirrg.minecraft.regioneditor.data;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import mirrg.minecraft.regioneditor.util.ImmutableArray;
import net.minecraft.util.Tuple;

public class MapData
{

	public RegionInfoTable regionInfoTable = new RegionInfoTable();
	public RegionMap regionMap = new RegionMap();

	public ImmutableArray<Area> getAreas()
	{
		ArrayList<Area> areas = new ArrayList<>();

		Set<ChunkPosition> visited = new HashSet<>();

		ChunkBoundingBox chunkBoundingBox = regionMap.getBoundingBox();
		int minX = chunkBoundingBox.min.x;
		int minZ = chunkBoundingBox.min.z;
		int maxX = chunkBoundingBox.max.x;
		int maxZ = chunkBoundingBox.max.z;
		for (int z = minZ; z <= maxZ; z++) { // 左上から順に見ていき、
			for (int x = minX; x <= maxX; x++) {
				ChunkPosition chunkPosition = new ChunkPosition(x, z);
				Optional<RegionIdentifier> oRegionIdentifier = regionMap.get(chunkPosition);
				if (oRegionIdentifier.isPresent()) { // 空白地ではなく、
					RegionIdentifier regionIdentifier = oRegionIdentifier.get();
					if (!visited.contains(chunkPosition)) { // 未訪問の場合、

						// 一続きのマスをすべて訪問する
						areas.add(new Area(
							regionInfoTable.get(regionIdentifier),
							visitRecursively(chunkPosition, chunkBoundingBox, visited)));

					}
				}
			}
		}

		return ImmutableArray.fromIterable(areas);
	}

	/**
	 * 起点マスに接続している一つながりの領地をすべて訪問する。
	 */
	private ImmutableArray<ChunkPosition> visitRecursively(
		ChunkPosition chunkPosition,
		ChunkBoundingBox chunkBoundingBox,
		Set<ChunkPosition> visited)
	{

		// 今調査している領地の識別番号（空白地の場合は空白地を調査中）
		Optional<RegionIdentifier> regionType = regionMap.get(chunkPosition);

		if (regionType.isPresent()) {

		}

		// ツリーの生成をしながら訪問を進めていく処理
		Node node = new Node(chunkPosition, null);
		{

			// 訪問予定地
			Deque<Tuple<ChunkPosition, Node>> waitings = new ArrayDeque<>();

			// 起点を最初の訪問予定地に追加する
			waitings.addLast(new Tuple<>(chunkPosition, node));
			visited.add(chunkPosition);

			// 訪問予定地が残っている限り次々と訪問する
			while (!waitings.isEmpty()) {

				// 訪問予定地から除去
				Tuple<ChunkPosition, Node> tuple = waitings.removeFirst();

				// 隣接マスへの訪問を試みる
				tryVisitNeighbor(tuple.getFirst(), chunkBoundingBox, regionType, visited, waitings, tuple.getSecond());

			}

		}

		return node.getOutline();
	}

	/**
	 * 起点マスから四方に
	 * {@link #tryVisit(ChunkPosition, Optional, Set, Deque, Node, BiConsumer)}
	 * を試みる。
	 */
	private void tryVisitNeighbor(
		ChunkPosition chunkPosition,
		ChunkBoundingBox chunkBoundingBox,
		Optional<RegionIdentifier> regionType,
		Set<ChunkPosition> visited,
		Deque<Tuple<ChunkPosition, Node>> waitings,
		Node parent)
	{
		tryVisit(chunkPosition.plus(0, -1), chunkBoundingBox, regionType, visited, waitings, parent, (p, n) -> p.up = n);
		tryVisit(chunkPosition.plus(1, 0), chunkBoundingBox, regionType, visited, waitings, parent, (p, n) -> p.right = n);
		tryVisit(chunkPosition.plus(0, 1), chunkBoundingBox, regionType, visited, waitings, parent, (p, n) -> p.down = n);
		tryVisit(chunkPosition.plus(-1, 0), chunkBoundingBox, regionType, visited, waitings, parent, (p, n) -> p.left = n);
	}

	/**
	 * そのマスが同じ領地で未訪問なら訪問予定地に追加する。
	 */
	private void tryVisit(
		ChunkPosition chunkPosition,
		ChunkBoundingBox chunkBoundingBox,
		Optional<RegionIdentifier> regionType,
		Set<ChunkPosition> visited,
		Deque<Tuple<ChunkPosition, Node>> waitings,
		Node parent,
		BiConsumer<Node, Node> setter)
	{
		if (canVisit(chunkPosition, chunkBoundingBox, regionType, visited)) {

			// ぶら下がるノードを生成
			Node node = new Node(chunkPosition, parent);
			setter.accept(parent, node);

			// 訪問予定地に追加
			waitings.addLast(new Tuple<>(chunkPosition, node));
			visited.add(chunkPosition);

		}
	}

	private boolean canVisit(
		ChunkPosition chunkPosition,
		ChunkBoundingBox chunkBoundingBox,
		Optional<RegionIdentifier> regionType,
		Set<ChunkPosition> visited)
	{
		return chunkBoundingBox.contains(chunkPosition)
			&& regionMap.get(chunkPosition).equals(regionType)
			&& !visited.contains(chunkPosition);
	}

}
