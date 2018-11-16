package mirrg.minecraft.regioneditor.data;

import java.util.ArrayList;
import java.util.List;

import mirrg.minecraft.regioneditor.util.ImmutableArray;

public class Node
{

	public final ChunkPosition chunkPosition;
	public final Node parent;

	public Node left = null;
	public Node right = null;
	public Node up = null;
	public Node down = null;

	public Node(ChunkPosition chunkPosition, Node parent)
	{
		this.chunkPosition = chunkPosition;
		this.parent = parent;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[" + chunkPosition.toString() + "]");
		if (left != null) sb.append(left.toString());
		if (right != null) sb.append(right.toString());
		if (up != null) sb.append(up.toString());
		if (down != null) sb.append(down.toString());
		return sb.toString();
	}

	public ImmutableArray<ChunkPosition> getOutline()
	{
		List<ChunkPosition> chunkPositions = new ArrayList<>();

		if (up != null) {
			up.addOutlineFromDown(chunkPositions);
		} else {
			chunkPositions.add(chunkPosition.plus(1, 0));
		}
		if (right != null) {
			right.addOutlineFromLeft(chunkPositions);
		} else {
			chunkPositions.add(chunkPosition.plus(1, 1));
		}
		if (down != null) {
			down.addOutlineFromUp(chunkPositions);
		} else {
			chunkPositions.add(chunkPosition.plus(0, 1));
		}
		if (left != null) {
			left.addOutlineFromRight(chunkPositions);
		} else {
			chunkPositions.add(chunkPosition.plus(0, 0));
		}

		while (simplify1(chunkPositions)) {

		}

		while (simplify2(chunkPositions)) {

		}

		return ImmutableArray.fromIterable(chunkPositions);
	}

	/**
	 * リストに対して最初に見つかった1か所だけUターンを除去する。
	 *
	 * @return Uターンが見つかった場合に真
	 */
	private boolean simplify1(List<ChunkPosition> chunkPositions)
	{
		// [4,3],[4,2],[4,3] のような場合に [4,3] にまとめる

		for (int i = 2; i < chunkPositions.size(); i++) { // 添え字2以上のすべての頂点において、
			if (chunkPositions.get(i - 2).equals(chunkPositions.get(i))) { // 2個前の頂点と同一の座標であった場合、

				// 1個前とそこの要素を削除する
				chunkPositions.remove(i - 0);
				chunkPositions.remove(i - 1);

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
	private boolean simplify2(List<ChunkPosition> chunkPositions)
	{
		// [4,3],[4,4],[4,5] のような場合に [4,3],[4,5] にまとめる

		for (int i = 2; i < chunkPositions.size(); i++) { // 添え字2以上のすべての頂点において、

			ChunkPosition a1 = chunkPositions.get(i - 2);
			ChunkPosition a2 = chunkPositions.get(i - 1);
			ChunkPosition a3 = chunkPositions.get(i - 0);

			// 隣合った辺の向きが同じならば、
			if (a1.x == a2.x && a2.x == a3.x) {

				// 1個前の要素を削除する
				chunkPositions.remove(i - 1);

				return true;
			} else if (a1.z == a2.z && a2.z == a3.z) {

				// 1個前の要素を削除する
				chunkPositions.remove(i - 1);

				return true;
			}

		}

		return false;
	}

	private void addOutlineFromDown(List<ChunkPosition> chunkPositions)
	{
		if (left != null) {
			left.addOutlineFromRight(chunkPositions);
		} else {
			chunkPositions.add(chunkPosition.plus(0, 0));
		}
		if (up != null) {
			up.addOutlineFromDown(chunkPositions);
		} else {
			chunkPositions.add(chunkPosition.plus(1, 0));
		}
		if (right != null) {
			right.addOutlineFromLeft(chunkPositions);
		} else {
			chunkPositions.add(chunkPosition.plus(1, 1));
		}
	}

	private void addOutlineFromLeft(List<ChunkPosition> chunkPositions)
	{
		if (up != null) {
			up.addOutlineFromDown(chunkPositions);
		} else {
			chunkPositions.add(chunkPosition.plus(1, 0));
		}
		if (right != null) {
			right.addOutlineFromLeft(chunkPositions);
		} else {
			chunkPositions.add(chunkPosition.plus(1, 1));
		}
		if (down != null) {
			down.addOutlineFromUp(chunkPositions);
		} else {
			chunkPositions.add(chunkPosition.plus(0, 1));
		}
	}

	private void addOutlineFromUp(List<ChunkPosition> chunkPositions)
	{
		if (right != null) {
			right.addOutlineFromLeft(chunkPositions);
		} else {
			chunkPositions.add(chunkPosition.plus(1, 1));
		}
		if (down != null) {
			down.addOutlineFromUp(chunkPositions);
		} else {
			chunkPositions.add(chunkPosition.plus(0, 1));
		}
		if (left != null) {
			left.addOutlineFromRight(chunkPositions);
		} else {
			chunkPositions.add(chunkPosition.plus(0, 0));
		}
	}

	private void addOutlineFromRight(List<ChunkPosition> chunkPositions)
	{
		if (down != null) {
			down.addOutlineFromUp(chunkPositions);
		} else {
			chunkPositions.add(chunkPosition.plus(0, 1));
		}
		if (left != null) {
			left.addOutlineFromRight(chunkPositions);
		} else {
			chunkPositions.add(chunkPosition.plus(0, 0));
		}
		if (up != null) {
			up.addOutlineFromDown(chunkPositions);
		} else {
			chunkPositions.add(chunkPosition.plus(1, 0));
		}
	}

}
