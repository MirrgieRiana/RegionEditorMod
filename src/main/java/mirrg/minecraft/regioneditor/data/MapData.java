package mirrg.minecraft.regioneditor.data;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import mirrg.boron.util.struct.ImmutableArray;
import mirrg.boron.util.struct.Tuple;

public class MapData
{

	public RegionInfoTable regionInfoTable = new RegionInfoTable();
	public RegionMap regionMap = new RegionMap();

	public ImmutableArray<Area> getAreas()
	{
		ArrayList<Area> areas = new ArrayList<>();

		Set<TileIndex> visited = new HashSet<>();

		TileBoundingBox tileBoundingBox = regionMap.getBoundingBox();
		int minX = tileBoundingBox.min.x;
		int minZ = tileBoundingBox.min.z;
		int maxX = tileBoundingBox.max.x;
		int maxZ = tileBoundingBox.max.z;
		for (int z = minZ; z <= maxZ; z++) { // 左上から順に見ていき、
			for (int x = minX; x <= maxX; x++) {
				TileIndex tileIndex = new TileIndex(x, z);
				Optional<RegionIdentifier> oRegionIdentifier = regionMap.get(tileIndex);
				if (oRegionIdentifier.isPresent()) { // 空白地ではなく、
					RegionIdentifier regionIdentifier = oRegionIdentifier.get();
					if (!visited.contains(tileIndex)) { // 未訪問の場合、

						// 一続きのマスをすべて訪問する
						areas.add(new Area(
							new RegionEntry(regionIdentifier, regionInfoTable.get(regionIdentifier)),
							visitRecursively(tileIndex, tileBoundingBox, visited)));

					}
				}
			}
		}

		return ImmutableArray.ofIterable(areas);
	}

	/**
	 * 起点マスに接続している一つながりの領地をすべて訪問する。
	 */
	private ImmutableArray<TileIndex> visitRecursively(
		TileIndex tileIndex,
		TileBoundingBox tileBoundingBox,
		Set<TileIndex> visited)
	{

		// 今調査している領地の識別番号（空白地の場合は空白地を調査中）
		Optional<RegionIdentifier> regionType = regionMap.get(tileIndex);

		if (regionType.isPresent()) {

		}

		// ツリーの生成をしながら訪問を進めていく処理
		Node node = new Node(tileIndex, null);
		{

			// 訪問予定地
			Deque<Tuple<TileIndex, Node>> waitings = new ArrayDeque<>();

			// 起点を最初の訪問予定地に追加する
			waitings.addLast(new Tuple<>(tileIndex, node));
			visited.add(tileIndex);

			// 訪問予定地が残っている限り次々と訪問する
			while (!waitings.isEmpty()) {

				// 訪問予定地から除去
				Tuple<TileIndex, Node> tuple = waitings.removeFirst();

				// 隣接マスへの訪問を試みる
				tryVisitNeighbor(tuple.x, tileBoundingBox, regionType, visited, waitings, tuple.y);

			}

		}

		return node.getOutline();
	}

	/**
	 * 起点マスから四方に
	 * {@link #tryVisit(TileIndex, Optional, Set, Deque, Node, BiConsumer)}
	 * を試みる。
	 */
	private void tryVisitNeighbor(
		TileIndex tileIndex,
		TileBoundingBox tileBoundingBox,
		Optional<RegionIdentifier> regionType,
		Set<TileIndex> visited,
		Deque<Tuple<TileIndex, Node>> waitings,
		Node parent)
	{
		tryVisit(tileIndex.plus(0, -1), tileBoundingBox, regionType, visited, waitings, parent, (p, n) -> p.up = n);
		tryVisit(tileIndex.plus(1, 0), tileBoundingBox, regionType, visited, waitings, parent, (p, n) -> p.right = n);
		tryVisit(tileIndex.plus(0, 1), tileBoundingBox, regionType, visited, waitings, parent, (p, n) -> p.down = n);
		tryVisit(tileIndex.plus(-1, 0), tileBoundingBox, regionType, visited, waitings, parent, (p, n) -> p.left = n);
	}

	/**
	 * そのマスが同じ領地で未訪問なら訪問予定地に追加する。
	 */
	private void tryVisit(
		TileIndex tileIndex,
		TileBoundingBox tileBoundingBox,
		Optional<RegionIdentifier> regionType,
		Set<TileIndex> visited,
		Deque<Tuple<TileIndex, Node>> waitings,
		Node parent,
		BiConsumer<Node, Node> setter)
	{
		if (canVisit(tileIndex, tileBoundingBox, regionType, visited)) {

			// ぶら下がるノードを生成
			Node node = new Node(tileIndex, parent);
			setter.accept(parent, node);

			// 訪問予定地に追加
			waitings.addLast(new Tuple<>(tileIndex, node));
			visited.add(tileIndex);

		}
	}

	private boolean canVisit(
		TileIndex tileIndex,
		TileBoundingBox tileBoundingBox,
		Optional<RegionIdentifier> regionType,
		Set<TileIndex> visited)
	{
		return tileBoundingBox.contains(tileIndex)
			&& regionMap.get(tileIndex).equals(regionType)
			&& !visited.contains(tileIndex);
	}

}
