package mirrg.minecraft.regioneditor.gui;

import java.awt.Component;
import java.awt.Point;
import java.util.Optional;

import mirrg.minecraft.regioneditor.data.PossessionMapModel;
import mirrg.minecraft.regioneditor.data.RegionIdentifier;
import mirrg.minecraft.regioneditor.data.TileIndex;

public interface IToolContext
{

	public Component getComponent();

	public PossessionMapModel getPossessionMapModel();

	public int getWidth();

	public int getHeight();

	public TileIndex getTileIndex(Point point);

	public Point getTilePosition(TileIndex tileIndex);

	public int getTileSize();

	public Optional<RegionIdentifier> getCurrentRegionIdentifier();

	public void setCurrentRegionIdentifier(Optional<RegionIdentifier> oCurrentRegionIdentifier);

	public int getBrushSize();

	public void repaintTile();

	public void repaintTile(TileIndex tileIndex);

	public void repaintOverlay();

}
