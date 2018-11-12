package mirrg.minecraft.regioneditor.gui;

import static mirrg.minecraft.regioneditor.gui.SwingUtils.*;

import java.awt.CardLayout;
import java.awt.Dialog.ModalityType;

import javax.swing.JLabel;
import javax.swing.JTextArea;

public class GuiCommand extends GuiBase
{

	public GuiCommand(WindowWrapper owner)
	{
		super(owner, "Command", ModalityType.MODELESS);
	}

	private JTextArea textArea;

	@Override
	protected void initComponenets()
	{
		windowWrapper.getWindow().setLayout(new CardLayout());

		windowWrapper.getWindow().add(scrollPane(

			new JLabel("Dummy")

		));
	}

}
