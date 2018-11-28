package mirrg.minecraft.regioneditor.data;

public interface ITileMapListener
{

	public void onChange();

	public void onChange(TileIndex tileIndex);

}
