package mirrg.minecraft.regioneditor.data.controller;

import mirrg.boron.util.event.lib.EventProviderRunnable;
import mirrg.minecraft.regioneditor.data.models.RegionTableModel;

public class RegionTableController
{

	/**
	 * モデルの状態が変更された場合に呼び出されます。
	 * 同時に複数の変更が行われた場合でも1度だけ呼び出されます。
	 */
	public final EventProviderRunnable epChangedState = new EventProviderRunnable();

	//

	public RegionTableModel model;

	public RegionTableController(RegionTableModel model)
	{
		this.model = model;
	}

	public void setModel(RegionTableModel model)
	{
		this.model = model;
		epChangedState.trigger().run();
	}

}
