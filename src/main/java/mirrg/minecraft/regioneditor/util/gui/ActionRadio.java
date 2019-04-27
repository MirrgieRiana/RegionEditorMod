package mirrg.minecraft.regioneditor.util.gui;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.function.Consumer;

public class ActionRadio extends ActionToggleBase
{

	private List<ActionRadio> group;

	public ActionRadio(List<ActionRadio> group, Consumer<Boolean> listener)
	{
		this.group = group;
		addPropertyChangeListener(e -> {
			if (e.getPropertyName().equals(SELECTED_KEY)) {
				listener.accept((Boolean) e.getNewValue());
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (!isSelected()) {
			for (ActionRadio actionRadio : group) {
				if (actionRadio.isSelected()) {
					actionRadio.setSelected(false);
				}
			}
			setSelected(true);
		}
	}

	@Override
	public void register()
	{
		group.add(this);
	}

}
