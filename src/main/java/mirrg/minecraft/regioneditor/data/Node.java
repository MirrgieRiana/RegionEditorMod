package mirrg.minecraft.regioneditor.data;

import java.util.ArrayList;
import java.util.List;

import mirrg.boron.util.struct.ImmutableArray;

public class Node
{

	public final TilePosition tilePosition;
	public final Node parent;

	public Node left = null;
	public Node right = null;
	public Node up = null;
	public Node down = null;

	public Node(TilePosition tilePosition, Node parent)
	{
		this.tilePosition = tilePosition;
		this.parent = parent;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[" + tilePosition.toString() + "]");
		if (left != null) sb.append(left.toString());
		if (right != null) sb.append(right.toString());
		if (up != null) sb.append(up.toString());
		if (down != null) sb.append(down.toString());
		return sb.toString();
	}

	public ImmutableArray<TilePosition> getOutline()
	{
		List<TilePosition> tilePositions = new ArrayList<>();

		if (up != null) {
			up.addOutlineFromDown(tilePositions);
		} else {
			tilePositions.add(tilePosition.plus(1, 0));
		}
		if (right != null) {
			right.addOutlineFromLeft(tilePositions);
		} else {
			tilePositions.add(tilePosition.plus(1, 1));
		}
		if (down != null) {
			down.addOutlineFromUp(tilePositions);
		} else {
			tilePositions.add(tilePosition.plus(0, 1));
		}
		if (left != null) {
			left.addOutlineFromRight(tilePositions);
		} else {
			tilePositions.add(tilePosition.plus(0, 0));
		}

		while (simplify1(tilePositions)) {

		}

		while (simplify2(tilePositions)) {

		}

		return ImmutableArray.ofIterable(tilePositions);
	}

	/**
	 * リストに対して最初に見つかった1か所だけUターンを除去する。
	 *
	 * @return Uターンが見つかった場合に真
	 */
	private boolean simplify1(List<TilePosition> tilePositions)
	{
		// [4,3],[4,2],[4,3] のような場合に [4,3] にまとめる

		for (int i = 2; i < tilePositions.size(); i++) { // 添え字2以上のすべての頂点において、
			if (tilePositions.get(i - 2).equals(tilePositions.get(i))) { // 2個前の頂点と同一の座標であった場合、

				// 1個前とそこの要素を削除する
				tilePositions.remove(i - 0);
				tilePositions.remove(i - 1);

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
	private boolean simplify2(List<TilePosition> tilePositions)
	{
		// [4,3],[4,4],[4,5] のような場合に [4,3],[4,5] にまとめる

		for (int i = 2; i < tilePositions.size(); i++) { // 添え字2以上のすべての頂点において、

			TilePosition a1 = tilePositions.get(i - 2);
			TilePosition a2 = tilePositions.get(i - 1);
			TilePosition a3 = tilePositions.get(i - 0);

			// 隣合った辺の向きが同じならば、
			if (a1.x == a2.x && a2.x == a3.x) {

				// 1個前の要素を削除する
				tilePositions.remove(i - 1);

				return true;
			} else if (a1.z == a2.z && a2.z == a3.z) {

				// 1個前の要素を削除する
				tilePositions.remove(i - 1);

				return true;
			}

		}

		return false;
	}

	private void addOutlineFromDown(List<TilePosition> tilePositions)
	{
		if (left != null) {
			left.addOutlineFromRight(tilePositions);
		} else {
			tilePositions.add(tilePosition.plus(0, 0));
		}
		if (up != null) {
			up.addOutlineFromDown(tilePositions);
		} else {
			tilePositions.add(tilePosition.plus(1, 0));
		}
		if (right != null) {
			right.addOutlineFromLeft(tilePositions);
		} else {
			tilePositions.add(tilePosition.plus(1, 1));
		}
	}

	private void addOutlineFromLeft(List<TilePosition> tilePositions)
	{
		if (up != null) {
			up.addOutlineFromDown(tilePositions);
		} else {
			tilePositions.add(tilePosition.plus(1, 0));
		}
		if (right != null) {
			right.addOutlineFromLeft(tilePositions);
		} else {
			tilePositions.add(tilePosition.plus(1, 1));
		}
		if (down != null) {
			down.addOutlineFromUp(tilePositions);
		} else {
			tilePositions.add(tilePosition.plus(0, 1));
		}
	}

	private void addOutlineFromUp(List<TilePosition> tilePositions)
	{
		if (right != null) {
			right.addOutlineFromLeft(tilePositions);
		} else {
			tilePositions.add(tilePosition.plus(1, 1));
		}
		if (down != null) {
			down.addOutlineFromUp(tilePositions);
		} else {
			tilePositions.add(tilePosition.plus(0, 1));
		}
		if (left != null) {
			left.addOutlineFromRight(tilePositions);
		} else {
			tilePositions.add(tilePosition.plus(0, 0));
		}
	}

	private void addOutlineFromRight(List<TilePosition> tilePositions)
	{
		if (down != null) {
			down.addOutlineFromUp(tilePositions);
		} else {
			tilePositions.add(tilePosition.plus(0, 1));
		}
		if (left != null) {
			left.addOutlineFromRight(tilePositions);
		} else {
			tilePositions.add(tilePosition.plus(0, 0));
		}
		if (up != null) {
			up.addOutlineFromDown(tilePositions);
		} else {
			tilePositions.add(tilePosition.plus(1, 0));
		}
	}

}
