package mirrg.minecraft.regioneditor.gui;

import static mirrg.minecraft.regioneditor.gui.SwingUtils.*;

import java.awt.CardLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import mirrg.boron.util.struct.ImmutableArray;
import mirrg.boron.util.struct.Tuple;
import mirrg.boron.util.suppliterator.ISuppliterator;
import mirrg.minecraft.regioneditor.data.Area;
import mirrg.minecraft.regioneditor.data.RegionIdentifier;
import mirrg.minecraft.regioneditor.gui.lang.I18n;

public class GuiCommand extends GuiBase
{

	private ImmutableArray<Area> areas;
	private Optional<Consumer<List<String>>> oSender;
	private Optional<IChatMessageProvider> oChatMessageProvider;

	public GuiCommand(WindowWrapper owner, ImmutableArray<Area> areas, Optional<Consumer<List<String>>> oSender, Optional<IChatMessageProvider> oChatMessageProvider)
	{
		super(owner, I18n.localize("GuiCommand.title"), ModalityType.MODELESS);
		this.areas = areas;
		this.oSender = oSender;
		this.oChatMessageProvider = oChatMessageProvider;
	}

	public static String getCommandAdd(ImmutableArray<Area> list, String set)
	{
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
			sb.append(String.format("/dmarker addarea color:%06x fillcolor:%06x weight:4 opacity:0.8 fillopacity:0.4 label:\"%s\" id:\"%s\" set:\"" + set + "\"",
				area.regionEntry.regionInfo.countryColor.getRGB() & 0xFFFFFF,
				area.regionEntry.regionInfo.stateColor.getRGB() & 0xFFFFFF,
				area.regionEntry.regionInfo.countryName + " / " + area.regionEntry.regionInfo.stateName,
				area.regionEntry.regionIdentifier.countryNumber + "_" + area.regionEntry.regionIdentifier.stateNumber + "_" + areaId));
			sb.append("\n");
		});

		String string = sb.toString();
		return string;
	}

	// 103:5:65: label:"ラベル", set:markers, world:world, corners:{ {34128.0,1632.0} {34288.0,1632.0} {34288.0,1808.0} {34112.0,1808.0} {34112.0,1632.0} }, weight:4, color:a10005, opacity:0.8, fillcolor:a10005, fillopacity:0.4, boost:false, markup:false
	// boolean org.dynmap.markers.impl.MarkerAPIImpl.processListArea(DynmapCore plugin, DynmapCommandSender sender, String cmd, String commandLabel, String[] args)
	// cat dynmap/markers.yml | grep "            '" | perl -ple '$_ =~ /            '\''([^'\'']*)'\'':/; $_ = "/dmarker deletearea id:\"$1\""'
	// /dmarker deletearea id:"111:6:0"
	/*
	 * String msg = m.getMarkerID() + ": label:\"" + m.getLabel() + "\", set:" + m.getMarkerSet().getMarkerSetID()
	 * 	+ ", world:" + m.getWorld() + ", corners:" + ptlist + ", weight:" + m.getLineWeight() + ", color:"
	 * 	+ String.format("%06x", m.getLineColor()) + ", opacity:" + m.getLineOpacity() + ", fillcolor:"
	 * 	+ String.format("%06x", m.getFillColor()) + ", fillopacity:" + m.getFillOpacity() + ", boost:"
	 * 	+ m.getBoostFlag() + ", markup:" + m.isLabelMarkup();
	 */
	private static Pattern pattern = Pattern.compile("\\A(.*): label:\".*\", set:(.*), world:");

	public static String getCommandDeleteAreas(ImmutableArray<String> messages, String setExpected)
	{
		return messages.suppliterator()
			.mapIfNotNull(m -> {
				Matcher matcher = pattern.matcher(m);
				if (matcher.find()) {
					String id = matcher.group(1);
					String set = matcher.group(2);
					return new Tuple<>(id, set);
				}
				return null;
			})
			.filter(t -> {
				if (setExpected.startsWith("/") && setExpected.endsWith("/")) {
					return t.y.matches(setExpected.substring(1, setExpected.length() - 1));
				} else {
					return t.y.equals(setExpected);
				}
			})
			.map(t -> "/dmarker deletearea id:\"" + t.x + "\" set:\"" + t.y + "\"") // /dmarker deletearea id:"$1" set:"$2"
			.join("\n");
	}

	private JTextArea textAreaCommand;
	private JTextField textAreaSet;

	@Override
	protected void initComponenets()
	{
		windowWrapper.getWindow().setLayout(new CardLayout());

		windowWrapper.getWindow().add(borderPanelDown(

			scrollPane(textAreaCommand = new JTextArea(), 600, 600),

			borderPanelDown(

				flowPanel(

					new JLabel(I18n.localize("GuiCommand.label.set")),

					textAreaSet = new JTextField("markers", 10)

				),

				borderPanelDown(

					flowPanel(

						button(I18n.localize("GuiCommand.button.generateCommandAdd"), e -> {
							textAreaCommand.setText(getCommandAdd(areas, textAreaSet.getText()));
						}),

						get(new JButton(I18n.localize("GuiCommand.button.generateCommandDelete")), c -> {
							c.setEnabled(oChatMessageProvider.isPresent());
							if (oChatMessageProvider.isPresent()) {
								c.addMouseListener(new MouseAdapter() {
									@Override
									public void mousePressed(MouseEvent e)
									{
										textAreaCommand.setText("");
										oChatMessageProvider.get().startCapture("/dmarker listareas");
									}

									@Override
									public void mouseReleased(MouseEvent e)
									{
										textAreaCommand.setText(getCommandDeleteAreas(
											oChatMessageProvider.get().stopCapture(),
											textAreaSet.getText()));
									}
								});
							}
						})

					),

					flowPanel(

						get(button(I18n.localize("GuiCommand.button.send"), e -> {
							if (oSender.isPresent()) {
								oSender.get().accept(ISuppliterator.of(textAreaCommand.getText().trim().split("\\n")).toList());
							}
						}), c -> {
							c.setEnabled(oSender.isPresent());
						})

					)

				)

			)

		));
	}

}
