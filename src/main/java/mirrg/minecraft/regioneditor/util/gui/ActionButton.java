package mirrg.minecraft.regioneditor.util.gui;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class ActionButton extends ActionBase
{

	private Consumer<ActionEvent> listener;

	public ActionButton(Consumer<ActionEvent> listener)
	{
		this.listener = listener;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		listener.accept(e);
	}

}
