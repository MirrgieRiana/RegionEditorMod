package mirrg.minecraft.regioneditor.gui;

import static mirrg.minecraft.regioneditor.gui.SwingUtils.*;

import java.awt.CardLayout;
import java.awt.Dialog.ModalityType;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.JTextArea;

import mirrg.boron.util.struct.ImmutableArray;
import mirrg.minecraft.regioneditor.data.Area;

public class GuiCommand extends GuiBase
{

	private String dynmapCommand;
	private Optional<Consumer<List<String>>> oSender;

	public GuiCommand(WindowWrapper owner, ImmutableArray<Area> list, Optional<Consumer<List<String>>> oSender)
	{
		super(owner, "Command", ModalityType.MODELESS);

		StringBuilder sb = new StringBuilder();
		sb.append("=====");
		sb.append("\n");
		list.forEach((area, i) -> {
			sb.append("\n");
			sb.append(area.vertexes.stream()
				.map(a -> a.x + "\t" + a.z)
				.collect(Collectors.joining("\n")));
			sb.append("\n");
		});

		this.dynmapCommand = sb.toString();
		this.oSender = oSender;
	}

	private JTextArea textArea;

	@Override
	protected void initComponenets()
	{
		windowWrapper.getWindow().setLayout(new CardLayout());

		windowWrapper.getWindow().add(scrollPane(textArea = new JTextArea(dynmapCommand), 600, 600));
	}

}
