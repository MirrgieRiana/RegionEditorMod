package mirrg.minecraft.regioneditor.gui;

import java.awt.Component;
import java.awt.Point;
import java.util.Optional;

import mirrg.minecraft.regioneditor.data.controller.LayerController;
import mirrg.minecraft.regioneditor.data.objects.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.objects.TileCoordinate;

public interface IToolContext
{

	public Component getComponent();

	public LayerController getLayerController();

	public int getWidth();

	public int getHeight();

	public TileCoordinate getTileCoordinate(Point point);

	public Point getTilePosition(TileCoordinate tileCoordinate);

	public int getTileSize();

	public Optional<RegionIdentifier> getCurrentRegionIdentifier();

	public void setCurrentRegionIdentifier(Optional<RegionIdentifier> oCurrentRegionIdentifier);

	public int getBrushSize();

	public void repaintTile();

	public void repaintTile(TileCoordinate tileCoordinate);

	public void repaintOverlay();

}
