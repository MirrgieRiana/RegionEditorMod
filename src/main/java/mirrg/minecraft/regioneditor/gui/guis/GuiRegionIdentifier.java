package mirrg.minecraft.regioneditor.gui.guis;

import static mirrg.boron.swing.UtilsComponent.*;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.Optional;
import java.util.function.Predicate;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import mirrg.boron.util.i18n.I18n;
import mirrg.minecraft.regioneditor.data.objects.RegionIdentifier;
import mirrg.minecraft.regioneditor.gui.PanelResult;
import mirrg.minecraft.regioneditor.util.gui.ActionBuilder;
import mirrg.minecraft.regioneditor.util.gui.ActionButton;
import mirrg.minecraft.regioneditor.util.gui.WindowWrapper;

public class GuiRegionIdentifier extends GuiBase
{

	private final String countryIdDefault;
	private final String stateIdDefault;

	private ActionButton actionOk;
	private ActionButton actionCancel;

	private JTextField textFieldCountryId;
	private JTextField textFieldStateId;
	private PanelResult panelResult;

	public Optional<Predicate<RegionIdentifier>> oValidator = Optional.empty();

	public Optional<RegionIdentifier> oResult = Optional.empty();

	public GuiRegionIdentifier(WindowWrapper owner, I18n i18n, String countryIdDefault, String stateIdDefault)
	{
		super(owner, i18n, i18n.localize("GuiRegionIdentifier.title"), ModalityType.DOCUMENT_MODAL);
		this.countryIdDefault = countryIdDefault;
		this.stateIdDefault = stateIdDefault;
	}

	@Override
	protected void initComponenets()
	{

		actionOk = new ActionBuilder<>(new ActionButton(e -> {

			RegionIdentifier result = new RegionIdentifier(textFieldCountryId.getText(), textFieldStateId.getText());

			if (oValidator.isPresent() && !oValidator.get().test(result)) {
				return;
			}

			oResult = Optional.of(result);

			windowWrapper.getWindow().dispose();
		}))
			.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0))
			.register(windowWrapper.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW), windowWrapper.getRootPane().getActionMap());
		actionCancel = new ActionBuilder<>(new ActionButton(e -> {
			windowWrapper.getWindow().dispose();
		}))
			.keyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0))
			.register(windowWrapper.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW), windowWrapper.getRootPane().getActionMap());

		windowWrapper.getWindow().setLayout(new CardLayout());
		windowWrapper.getWindow().add(createPanelBorderDown(0,

			createSplitPaneVertical(1,

				createPanelFlow(createPanelMargin(4,
					get(createPanel(), c -> {
						c.setLayout(new FlowLayout());
						c.setLayout(createGroupLayout(c, new Component[][] {
							{ new JLabel(localize("GuiRegionIdentifier.labelCountryId")), textFieldCountryId = get(new JTextField(countryIdDefault, 8), c2 -> {
								c2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
							}) },
							{ new JLabel(localize("GuiRegionIdentifier.labelStateId")), textFieldStateId = get(new JTextField(stateIdDefault, 8), c2 -> {
								c2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
							}) },
						}, 4));
					}))),

				panelResult = new PanelResult(windowWrapper, i18n)

			),

			createPanelFlow(

				createButton(localize("GuiRegionIdentifier.buttonOk"), actionOk),

				createButton(localize("GuiRegionIdentifier.buttonCancel"), actionCancel)

			)

		));

		SwingUtilities.invokeLater(() -> {
			if (!textFieldCountryId.getText().isEmpty()) {
				textFieldStateId.requestFocus();
			}
		});
	}

	public void setText(String string, String detail, Color color)
	{
		panelResult.setText(string, detail, color);
	}

	public void setException(Exception e)
	{
		panelResult.setException(e);
	}

}
