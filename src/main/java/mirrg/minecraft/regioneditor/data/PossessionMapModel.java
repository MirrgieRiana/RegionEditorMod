package mirrg.minecraft.regioneditor.data;

public class PossessionMapModel
{

	private PossessionMap possessionMap;
	public final RegionTableModel regionTableModel;
	public final TileMapModel tileMapModel;

	public PossessionMapModel(PossessionMap possessionMap)
	{
		this.possessionMap = possessionMap;
		regionTableModel = new RegionTableModel(possessionMap.regionTable);
		tileMapModel = new TileMapModel(possessionMap.tileMap);
	}

	public IPossessionMapReader getDataReader()
	{
		return possessionMap;
	}

	public void setData(PossessionMap possessionMap)
	{
		this.possessionMap = possessionMap;
		regionTableModel.setData(possessionMap.regionTable);
		tileMapModel.setData(possessionMap.tileMap);
	}

}
