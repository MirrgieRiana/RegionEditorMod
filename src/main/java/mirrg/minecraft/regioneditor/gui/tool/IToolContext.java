package mirrg.minecraft.regioneditor.gui.tool;

import java.awt.Component;
import java.awt.Point;
import java.util.Optional;

import mirrg.minecraft.regioneditor.data.controller.LayerController;
import mirrg.minecraft.regioneditor.data.objects.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.objects.TileCoordinate;

public interface IToolContext
{

	public Component getComponent();

	public String localize(String unlocalizedString);

	public LayerController getLayerController();

	/**
	 * 描画領域の幅です。
	 */
	public int getWidth();

	/**
	 * 描画領域の高さです。
	 */
	public int getHeight();

	/**
	 * 描画領域上の指定座標のタイル座標を取得します。
	 */
	public TileCoordinate getTileCoordinate(Point point);

	/**
	 * 指定タイル座標の描画領域上の座標を取得します。
	 */
	public Point getTilePosition(TileCoordinate tileCoordinate);

	/**
	 * タイルの描画上の1辺の大きさです。
	 */
	public int getTileSize();

	public Optional<RegionIdentifier> getTileCurrent();

	public void setTileCurrent(Optional<RegionIdentifier> tileCurrent);

	public int getBrushSize();

	/**
	 * 描画領域全体を再描画します。
	 */
	public void repaintTile();

	/**
	 * 指定の1個のタイルが影響を与える範囲を再描画します。
	 */
	public void repaintTile(TileCoordinate tileCoordinate);

	public void repaintOverlay();

}
