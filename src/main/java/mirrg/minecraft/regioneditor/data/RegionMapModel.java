package mirrg.minecraft.regioneditor.data;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import mirrg.boron.util.struct.ImmutableArray;
import mirrg.boron.util.struct.Tuple;

public class RegionMapModel
{

	public final RegionTableModel regionTableModel = new RegionTableModel();
	public final TileMap tileMap = new TileMap();

	public ImmutableArray<Area> getAreas()
	{
		ArrayList<Area> areas = new ArrayList<>();

		Set<TileIndex> visited = new HashSet<>();

		TileBoundingBox tileBoundingBox = tileMap.getBoundingBox();
		int minX = tileBoundingBox.min.x;
		int minZ = tileBoundingBox.min.z;
		int maxX = tileBoundingBox.max.x;
		int maxZ = tileBoundingBox.max.z;
		for (int z = minZ; z <= maxZ; z++) { // 左上から順に見ていき、
			for (int x = minX; x <= maxX; x++) {
				TileIndex tileIndex = new TileIndex(x, z);
				Optional<RegionIdentifier> oRegionIdentifier = tileMap.get(tileIndex);
				if (oRegionIdentifier.isPresent()) { // 空白地ではなく、
					RegionIdentifier regionIdentifier = oRegionIdentifier.get();
					if (!visited.contains(tileIndex)) { // 未訪問の場合、

						// 一続きのマスをすべて訪問する
						areas.add(new Area(
							new RegionEntry(regionIdentifier, regionTableModel.get(regionIdentifier)),
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
		Optional<RegionIdentifier> regionType = tileMap.get(tileIndex);

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
			&& tileMap.get(tileIndex).equals(regionType)
			&& !visited.contains(tileIndex);
	}

	private static class Node
	{

		public final TileIndex tileIndex;
		public final Node parent;

		public Node left = null;
		public Node right = null;
		public Node up = null;
		public Node down = null;

		public Node(TileIndex tileIndex, Node parent)
		{
			this.tileIndex = tileIndex;
			this.parent = parent;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("[" + tileIndex.toString() + "]");
			if (left != null) sb.append(left.toString());
			if (right != null) sb.append(right.toString());
			if (up != null) sb.append(up.toString());
			if (down != null) sb.append(down.toString());
			return sb.toString();
		}

		public ImmutableArray<TileIndex> getOutline()
		{
			List<TileIndex> tileIndices = new ArrayList<>();

			if (up != null) {
				up.addOutlineFromDown(tileIndices);
			} else {
				tileIndices.add(tileIndex.plus(1, 0));
			}
			if (right != null) {
				right.addOutlineFromLeft(tileIndices);
			} else {
				tileIndices.add(tileIndex.plus(1, 1));
			}
			if (down != null) {
				down.addOutlineFromUp(tileIndices);
			} else {
				tileIndices.add(tileIndex.plus(0, 1));
			}
			if (left != null) {
				left.addOutlineFromRight(tileIndices);
			} else {
				tileIndices.add(tileIndex.plus(0, 0));
			}

			while (simplify1(tileIndices)) {

			}

			while (simplify2(tileIndices)) {

			}

			return ImmutableArray.ofIterable(tileIndices);
		}

		/**
		 * リストに対して最初に見つかった1か所だけUターンを除去する。
		 *
		 * @return Uターンが見つかった場合に真
		 */
		private boolean simplify1(List<TileIndex> tileIndices)
		{
			// [4,3],[4,2],[4,3] のような場合に [4,3] にまとめる

			for (int i = 2; i < tileIndices.size(); i++) { // 添え字2以上のすべての頂点において、
				if (tileIndices.get(i - 2).equals(tileIndices.get(i))) { // 2個前の頂点と同一の座標であった場合、

					// 1個前とそこの要素を削除する
					tileIndices.remove(i - 0);
					tileIndices.remove(i - 1);

					return true;
				}
			}

			return false;
		}

		/**
		 * リストに対して最初に見つかった1か所だけ同じ方向への頂点を除去する。
		 *
		 * @return Uターンが見つかった場合に真
		 */
		private boolean simplify2(List<TileIndex> tileIndices)
		{
			// [4,3],[4,4],[4,5] のような場合に [4,3],[4,5] にまとめる

			for (int i = 2; i < tileIndices.size(); i++) { // 添え字2以上のすべての頂点において、

				TileIndex a1 = tileIndices.get(i - 2);
				TileIndex a2 = tileIndices.get(i - 1);
				TileIndex a3 = tileIndices.get(i - 0);

				// 隣合った辺の向きが同じならば、
				if (a1.x == a2.x && a2.x == a3.x) {

					// 1個前の要素を削除する
					tileIndices.remove(i - 1);

					return true;
				} else if (a1.z == a2.z && a2.z == a3.z) {

					// 1個前の要素を削除する
					tileIndices.remove(i - 1);

					return true;
				}

			}

			return false;
		}

		private void addOutlineFromDown(List<TileIndex> tileIndices)
		{
			if (left != null) {
				left.addOutlineFromRight(tileIndices);
			} else {
				tileIndices.add(tileIndex.plus(0, 0));
			}
			if (up != null) {
				up.addOutlineFromDown(tileIndices);
			} else {
				tileIndices.add(tileIndex.plus(1, 0));
			}
			if (right != null) {
				right.addOutlineFromLeft(tileIndices);
			} else {
				tileIndices.add(tileIndex.plus(1, 1));
			}
		}

		private void addOutlineFromLeft(List<TileIndex> tileIndices)
		{
			if (up != null) {
				up.addOutlineFromDown(tileIndices);
			} else {
				tileIndices.add(tileIndex.plus(1, 0));
			}
			if (right != null) {
				right.addOutlineFromLeft(tileIndices);
			} else {
				tileIndices.add(tileIndex.plus(1, 1));
			}
			if (down != null) {
				down.addOutlineFromUp(tileIndices);
			} else {
				tileIndices.add(tileIndex.plus(0, 1));
			}
		}

		private void addOutlineFromUp(List<TileIndex> tileIndices)
		{
			if (right != null) {
				right.addOutlineFromLeft(tileIndices);
			} else {
				tileIndices.add(tileIndex.plus(1, 1));
			}
			if (down != null) {
				down.addOutlineFromUp(tileIndices);
			} else {
				tileIndices.add(tileIndex.plus(0, 1));
			}
			if (left != null) {
				left.addOutlineFromRight(tileIndices);
			} else {
				tileIndices.add(tileIndex.plus(0, 0));
			}
		}

		private void addOutlineFromRight(List<TileIndex> tileIndices)
		{
			if (down != null) {
				down.addOutlineFromUp(tileIndices);
			} else {
				tileIndices.add(tileIndex.plus(0, 1));
			}
			if (left != null) {
				left.addOutlineFromRight(tileIndices);
			} else {
				tileIndices.add(tileIndex.plus(0, 0));
			}
			if (up != null) {
				up.addOutlineFromDown(tileIndices);
			} else {
				tileIndices.add(tileIndex.plus(1, 0));
			}
		}

	}

}
