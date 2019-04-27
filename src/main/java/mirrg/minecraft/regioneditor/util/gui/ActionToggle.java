package mirrg.minecraft.regioneditor.util.gui;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class ActionToggle extends ActionToggleBase
{

	public ActionToggle(Consumer<Boolean> listener)
	{
		addPropertyChangeListener(e -> {
			if (e.getPropertyName().equals(SELECTED_KEY)) {
				listener.accept((Boolean) e.getNewValue());
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		setSelected(!isSelected());
	}

}
