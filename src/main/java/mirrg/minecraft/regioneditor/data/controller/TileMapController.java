package mirrg.minecraft.regioneditor.data.controller;

import mirrg.boron.util.event.lib.EventProviderConsumer;
import mirrg.boron.util.event.lib.EventProviderRunnable;
import mirrg.minecraft.regioneditor.data.models.TileMapModel;
import mirrg.minecraft.regioneditor.data.objects.TileCoordinate;

public class TileMapController
{

	/**
	 * モデルの状態が変更された場合に呼び出されます。
	 * 同時に複数の変更が行われた場合でも1度だけ呼び出されます。
	 */
	public final EventProviderRunnable epChangedState = new EventProviderRunnable();

	/**
	 * 特定のタイルが変更された場合に呼び出されます。
	 * 複数のタイルが変更された場合、すべてのタイルに対して個別に呼び出されます。
	 */
	public final EventProviderConsumer<TileCoordinate> epChangedTileSpecified = new EventProviderConsumer<>();

	/**
	 * 不特定のタイルが変更された可能性がある場合に呼び出されます。
	 * 同時に複数のタイルが変更された場合でも1度だけ呼び出されます。
	 * {@link #epChangedTileSpecified}が呼ばれた場合、こちらは呼ばれません。
	 */
	public final EventProviderRunnable epChangedTileUnspecified = new EventProviderRunnable();

	//

	public TileMapModel model;

	public TileMapController(TileMapModel model)
	{
		this.model = model;
	}

	public void setModel(TileMapModel model)
	{
		this.model = model;
		epChangedState.trigger().run();
	}

}
