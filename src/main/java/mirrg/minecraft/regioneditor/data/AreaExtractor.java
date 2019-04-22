package mirrg.minecraft.regioneditor.data;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import mirrg.boron.util.struct.ImmutableArray;
import mirrg.boron.util.struct.Tuple;
import mirrg.minecraft.regioneditor.data.models.LayerModel;
import mirrg.minecraft.regioneditor.data.objects.Area;
import mirrg.minecraft.regioneditor.data.objects.RegionEntry;
import mirrg.minecraft.regioneditor.data.objects.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.objects.TileCoordinate;
import mirrg.minecraft.regioneditor.data.objects.TileRectangle;

public class AreaExtractor
{

	private final LayerModel layerModel;

	public AreaExtractor(LayerModel layerModel)
	{
		this.layerModel = layerModel;
	}

	public ImmutableArray<Area> getAreas()
	{
		ArrayList<Area> areas = new ArrayList<>();

		Set<TileCoordinate> visited = new HashSet<>();

		TileRectangle tileRectangle = getBoundingBox();
		for (int z = tileRectangle.min.z; z <= tileRectangle.max.z; z++) { // 左上から順に見ていき、
			for (int x = tileRectangle.min.x; x <= tileRectangle.max.x; x++) {
				TileCoordinate tileCoordinate = new TileCoordinate(x, z);
				Optional<RegionIdentifier> oRegionIdentifier = layerModel.tileMapModel.get(tileCoordinate);
				if (oRegionIdentifier.isPresent()) { // 空白地ではなく、
					RegionIdentifier regionIdentifier = oRegionIdentifier.get();
					if (!visited.contains(tileCoordinate)) { // 未訪問の場合、

						// 一続きのマスをすべて訪問する
						areas.add(new Area(
							new RegionEntry(regionIdentifier, layerModel.regionTableModel.get(regionIdentifier)),
							visitRecursively(tileCoordinate, tileRectangle, visited)));

					}
				}
			}
		}

		return ImmutableArray.ofIterable(areas);
	}

	private TileRectangle getBoundingBox()
	{
		return new Supplier<TileRectangle>() {
			private int minX = 0;
			private int minZ = 0;
			private int maxX = 0;
			private int maxZ = 0;

			@Override
			public TileRectangle get()
			{
				layerModel.tileMapModel.getKeys()
					.forEach(tc -> {
						if (tc.x < minX) minX = tc.x;
						if (tc.z < minZ) minZ = tc.z;
						if (tc.x > maxX) maxX = tc.x;
						if (tc.z > maxZ) maxZ = tc.z;
					});

				return new TileRectangle(minX, minZ, maxX, maxZ);
			}
		}.get();
	}

	/**
	 * 起点マスに接続している一つながりの領地をすべて訪問する。
	 */
	private ImmutableArray<TileCoordinate> visitRecursively(
		TileCoordinate tileCoordinate,
		TileRectangle tileRectangle,
		Set<TileCoordinate> visited)
	{

		// 今調査している領地の識別番号（空白地の場合は空白地を調査中）
		Optional<RegionIdentifier> regionType = layerModel.tileMapModel.get(tileCoordinate);

		if (regionType.isPresent()) {

		}

		// ツリーの生成をしながら訪問を進めていく処理
		Node node = new Node(tileCoordinate, null);
		{

			// 訪問予定地
			Deque<Tuple<TileCoordinate, Node>> waitings = new ArrayDeque<>();

			// 起点を最初の訪問予定地に追加する
			waitings.addLast(new Tuple<>(tileCoordinate, node));
			visited.add(tileCoordinate);

			// 訪問予定地が残っている限り次々と訪問する
			while (!waitings.isEmpty()) {

				// 訪問予定地から除去
				Tuple<TileCoordinate, Node> tuple = waitings.removeFirst();

				// 隣接マスへの訪問を試みる
				tryVisitNeighbor(tuple.x, tileRectangle, regionType, visited, waitings, tuple.y);

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
		TileCoordinate tileCoordinate,
		TileRectangle tileRectangle,
		Optional<RegionIdentifier> regionType,
		Set<TileCoordinate> visited,
		Deque<Tuple<TileCoordinate, Node>> waitings,
		Node parent)
	{
		tryVisit(tileCoordinate.plus(0, -1), tileRectangle, regionType, visited, waitings, parent, (p, n) -> p.up = n);
		tryVisit(tileCoordinate.plus(1, 0), tileRectangle, regionType, visited, waitings, parent, (p, n) -> p.right = n);
		tryVisit(tileCoordinate.plus(0, 1), tileRectangle, regionType, visited, waitings, parent, (p, n) -> p.down = n);
		tryVisit(tileCoordinate.plus(-1, 0), tileRectangle, regionType, visited, waitings, parent, (p, n) -> p.left = n);
	}

	/**
	 * そのマスが同じ領地で未訪問なら訪問予定地に追加する。
	 */
	private void tryVisit(
		TileCoordinate tileCoordinate,
		TileRectangle tileBoundingBox,
		Optional<RegionIdentifier> regionType,
		Set<TileCoordinate> visited,
		Deque<Tuple<TileCoordinate, Node>> waitings,
		Node parent,
		BiConsumer<Node, Node> setter)
	{
		if (canVisit(tileCoordinate, tileBoundingBox, regionType, visited)) {

			// ぶら下がるノードを生成
			Node node = new Node(tileCoordinate, parent);
			setter.accept(parent, node);

			// 訪問予定地に追加
			waitings.addLast(new Tuple<>(tileCoordinate, node));
			visited.add(tileCoordinate);

		}
	}

	private boolean canVisit(
		TileCoordinate tileCoordinate,
		TileRectangle tileRectangle,
		Optional<RegionIdentifier> regionType,
		Set<TileCoordinate> visited)
	{
		return tileRectangle.contains(tileCoordinate)
			&& layerModel.tileMapModel.get(tileCoordinate).equals(regionType)
			&& !visited.contains(tileCoordinate);
	}

	private static class Node
	{

		public final TileCoordinate tileCoordinate;
		@SuppressWarnings("unused")
		public final Node parent;

		public Node left = null;
		public Node right = null;
		public Node up = null;
		public Node down = null;

		public Node(TileCoordinate tileCoordinate, Node parent)
		{
			this.tileCoordinate = tileCoordinate;
			this.parent = parent;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(tileCoordinate.toString());
			if (left != null) sb.append(left.toString());
			if (right != null) sb.append(right.toString());
			if (up != null) sb.append(up.toString());
			if (down != null) sb.append(down.toString());
			return sb.toString();
		}

		public ImmutableArray<TileCoordinate> getOutline()
		{
			List<TileCoordinate> tileCoordinates = new ArrayList<>();

			if (up != null) {
				up.addOutlineFromDown(tileCoordinates);
			} else {
				tileCoordinates.add(tileCoordinate.plus(1, 0));
			}
			if (right != null) {
				right.addOutlineFromLeft(tileCoordinates);
			} else {
				tileCoordinates.add(tileCoordinate.plus(1, 1));
			}
			if (down != null) {
				down.addOutlineFromUp(tileCoordinates);
			} else {
				tileCoordinates.add(tileCoordinate.plus(0, 1));
			}
			if (left != null) {
				left.addOutlineFromRight(tileCoordinates);
			} else {
				tileCoordinates.add(tileCoordinate.plus(0, 0));
			}

			while (simplify1(tileCoordinates)) {

			}

			while (simplify2(tileCoordinates)) {

			}

			return ImmutableArray.ofIterable(tileCoordinates);
		}

		/**
		 * リストに対して最初に見つかった1か所だけUターンを除去する。
		 *
		 * @return Uターンが見つかった場合に真
		 */
		private boolean simplify1(List<TileCoordinate> tileCoordinates)
		{
			// [4,3],[4,2],[4,3] のような場合に [4,3] にまとめる

			for (int i = 2; i < tileCoordinates.size(); i++) { // 添え字2以上のすべての頂点において、
				if (tileCoordinates.get(i - 2).equals(tileCoordinates.get(i))) { // 2個前の頂点と同一の座標であった場合、

					// 1個前とそこの要素を削除する
					tileCoordinates.remove(i - 0);
					tileCoordinates.remove(i - 1);

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
		private boolean simplify2(List<TileCoordinate> tileCoordinates)
		{
			// [4,3],[4,4],[4,5] のような場合に [4,3],[4,5] にまとめる

			for (int i = 2; i < tileCoordinates.size(); i++) { // 添え字2以上のすべての頂点において、

				TileCoordinate a1 = tileCoordinates.get(i - 2);
				TileCoordinate a2 = tileCoordinates.get(i - 1);
				TileCoordinate a3 = tileCoordinates.get(i - 0);

				// 隣合った辺の向きが同じならば、
				if (a1.x == a2.x && a2.x == a3.x) {

					// 1個前の要素を削除する
					tileCoordinates.remove(i - 1);

					return true;
				} else if (a1.z == a2.z && a2.z == a3.z) {

					// 1個前の要素を削除する
					tileCoordinates.remove(i - 1);

					return true;
				}

			}

			return false;
		}

		private void addOutlineFromDown(List<TileCoordinate> tileCoordinates)
		{
			if (left != null) {
				left.addOutlineFromRight(tileCoordinates);
			} else {
				tileCoordinates.add(tileCoordinate.plus(0, 0));
			}
			if (up != null) {
				up.addOutlineFromDown(tileCoordinates);
			} else {
				tileCoordinates.add(tileCoordinate.plus(1, 0));
			}
			if (right != null) {
				right.addOutlineFromLeft(tileCoordinates);
			} else {
				tileCoordinates.add(tileCoordinate.plus(1, 1));
			}
		}

		private void addOutlineFromLeft(List<TileCoordinate> tileCoordinates)
		{
			if (up != null) {
				up.addOutlineFromDown(tileCoordinates);
			} else {
				tileCoordinates.add(tileCoordinate.plus(1, 0));
			}
			if (right != null) {
				right.addOutlineFromLeft(tileCoordinates);
			} else {
				tileCoordinates.add(tileCoordinate.plus(1, 1));
			}
			if (down != null) {
				down.addOutlineFromUp(tileCoordinates);
			} else {
				tileCoordinates.add(tileCoordinate.plus(0, 1));
			}
		}

		private void addOutlineFromUp(List<TileCoordinate> tileCoordinates)
		{
			if (right != null) {
				right.addOutlineFromLeft(tileCoordinates);
			} else {
				tileCoordinates.add(tileCoordinate.plus(1, 1));
			}
			if (down != null) {
				down.addOutlineFromUp(tileCoordinates);
			} else {
				tileCoordinates.add(tileCoordinate.plus(0, 1));
			}
			if (left != null) {
				left.addOutlineFromRight(tileCoordinates);
			} else {
				tileCoordinates.add(tileCoordinate.plus(0, 0));
			}
		}

		private void addOutlineFromRight(List<TileCoordinate> tileCoordinates)
		{
			if (down != null) {
				down.addOutlineFromUp(tileCoordinates);
			} else {
				tileCoordinates.add(tileCoordinate.plus(0, 1));
			}
			if (left != null) {
				left.addOutlineFromRight(tileCoordinates);
			} else {
				tileCoordinates.add(tileCoordinate.plus(0, 0));
			}
			if (up != null) {
				up.addOutlineFromDown(tileCoordinates);
			} else {
				tileCoordinates.add(tileCoordinate.plus(1, 0));
			}
		}

	}

}
