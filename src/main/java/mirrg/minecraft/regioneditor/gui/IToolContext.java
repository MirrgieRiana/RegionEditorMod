package mirrg.minecraft.regioneditor.gui;

import java.awt.Component;
import java.awt.Point;
import java.util.Optional;

import mirrg.minecraft.regioneditor.data.MapData;
import mirrg.minecraft.regioneditor.data.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.TilePosition;

public interface IToolContext
{

	public Component getComponent();

	public MapData getMapData();

	public TilePosition getTilePosition(Point point);

	public Optional<RegionIdentifier> getCurrentRegionIdentifier();

	public void setCurrentRegionIdentifier(Optional<RegionIdentifier> oRegionIdentifierCurrent);

	public void repaintTile();

	public void repaintTile(TilePosition tilePosition);

	public void repaintOverlay();

}
