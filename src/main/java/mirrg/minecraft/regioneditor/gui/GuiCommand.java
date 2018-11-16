package mirrg.minecraft.regioneditor.gui;

import static mirrg.minecraft.regioneditor.gui.SwingUtils.*;

import java.awt.CardLayout;
import java.awt.Dialog.ModalityType;

import javax.swing.JTextArea;

public class GuiCommand extends GuiBase
{

	private String dynmapCommand;

	public GuiCommand(WindowWrapper owner, String dynmapCommand)
	{
		super(owner, "Command", ModalityType.MODELESS);
		this.dynmapCommand = dynmapCommand;
	}

	private JTextArea textArea;

	@Override
	protected void initComponenets()
	{
		windowWrapper.getWindow().setLayout(new CardLayout());

		windowWrapper.getWindow().add(scrollPane(textArea = new JTextArea(dynmapCommand), 600, 600));
	}

}
