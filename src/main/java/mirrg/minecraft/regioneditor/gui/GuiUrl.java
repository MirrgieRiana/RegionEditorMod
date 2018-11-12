package mirrg.minecraft.regioneditor.gui;

import static mirrg.minecraft.regioneditor.gui.SwingUtils.*;

import java.awt.CardLayout;
import java.awt.Dialog.ModalityType;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class GuiUrl extends GuiBase
{

	public GuiUrl(WindowWrapper owner)
	{
		super(owner, "Url", ModalityType.DOCUMENT_MODAL);
	}

	private JTextField textField;
	private JLabel label;

	public boolean ok = false;
	public URI uri = null;
	public URL url = null;

	@Override
	protected void initComponenets()
	{
		windowWrapper.getWindow().setLayout(new CardLayout());

		windowWrapper.getWindow().add(borderPanelDown(

			borderPanelDown(

				scrollPane(

					textField = new JTextField(30)

				),

				flowPanel(

					label = new JLabel("...")

				)

			),

			flowPanel(

				button("OK", e -> {
					try {
						uri = new URI(textField.getText());
						url = new URL(textField.getText());
						ok = true;
						windowWrapper.getWindow().setVisible(false);
					} catch (URISyntaxException | MalformedURLException e1) {
						label.setText(e1.getMessage());
					}
				}),

				button("Cancel", e -> {
					windowWrapper.getWindow().setVisible(false);
				})

			)

		));
	}

}
