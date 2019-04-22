package mirrg.minecraft.regioneditor.data.controller;

import mirrg.boron.util.event.lib.EventProviderRunnable;
import mirrg.minecraft.regioneditor.data.models.LayerModel;

public class LayerController
{

	/**
	 * モデルの状態が変更された場合に呼び出されます。
	 * 同時に複数の変更が行われた場合でも1度だけ呼び出されます。
	 */
	public final EventProviderRunnable epChangedState = new EventProviderRunnable();

	public final TileMapController tileMapController;
	public final RegionTableController regionTableController;

	//

	public LayerModel model;

	public LayerController(LayerModel model)
	{
		this.model = model;
		tileMapController = new TileMapController(model.tileMapModel);
		regionTableController = new RegionTableController(model.regionTableModel);
	}

	public void setModel(LayerModel model)
	{
		tileMapController.setModel(model.tileMapModel);
		regionTableController.setModel(model.regionTableModel);

		this.model = model;
		epChangedState.trigger().run();
	}

}
