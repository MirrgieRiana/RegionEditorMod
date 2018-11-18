package mirrg.minecraft.regioneditor.gui;

import static mirrg.minecraft.regioneditor.gui.SwingUtils.*;

import java.awt.CardLayout;
import java.awt.Dialog.ModalityType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.JTextArea;

import mirrg.boron.util.struct.ImmutableArray;
import mirrg.boron.util.suppliterator.ISuppliterator;
import mirrg.minecraft.regioneditor.data.Area;
import mirrg.minecraft.regioneditor.data.RegionIdentifier;

public class GuiCommand extends GuiBase
{

	private String dynmapCommand;
	private Optional<Consumer<List<String>>> oSender;

	public GuiCommand(WindowWrapper owner, ImmutableArray<Area> list, Optional<Consumer<List<String>>> oSender)
	{
		super(owner, "Command", ModalityType.MODELESS);

		Map<RegionIdentifier, Integer> areaIds = new HashMap<>();

		StringBuilder sb = new StringBuilder();
		list.forEach((area, i) -> {
			sb.append("/dmarker clearcorners");
			sb.append("\n");
			area.vertexes.stream()
				.forEach(a -> {
					sb.append("/dmarker addcorner " + (a.x * 16) + " 0 " + (a.z * 16) + " world");
					sb.append("\n");
				});
			int areaId = areaIds.compute(area.regionEntry.regionIdentifier, (id2, i2) -> i2 == null ? 0 : i2 + 1);
			sb.append(String.format("/dmarker addarea color:%06x fillcolor:%06x weight:4 opacity:0.8 fillopacity:0.4 label:\"%s\" id:\"%s\"",
				area.regionEntry.regionInfo.countryColor.getRGB() & 0xFFFFFF,
				area.regionEntry.regionInfo.stateColor.getRGB() & 0xFFFFFF,
				area.regionEntry.regionInfo.countryName + "：" + area.regionEntry.regionInfo.stateName,
				area.regionEntry.regionIdentifier.countryNumber + ":" + area.regionEntry.regionIdentifier.stateNumber + ":" + areaId));
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

		windowWrapper.getWindow().add(borderPanelDown(

			scrollPane(textArea = new JTextArea(dynmapCommand), 600, 600),

			flowPanel(

				get(button("Send", e -> {
					if (oSender.isPresent()) {
						oSender.get().accept(ISuppliterator.of(textArea.getText().trim().split("\\n")).toCollection());
					}
				}), c -> {
					c.setEnabled(oSender.isPresent());
				})

			)

		));
	}

}
