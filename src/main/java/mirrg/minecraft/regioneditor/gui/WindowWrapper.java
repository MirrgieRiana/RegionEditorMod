package mirrg.minecraft.regioneditor.gui;

import java.awt.Dialog.ModalityType;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public final class WindowWrapper
{

	public final JFrame frame;
	public final JDialog dialog;

	private WindowWrapper(JFrame frame, JDialog dialog)
	{
		this.frame = frame;
		this.dialog = dialog;
	}

	//

	public static WindowWrapper fromFrame(JFrame frame)
	{
		return new WindowWrapper(frame, null);
	}

	public static WindowWrapper fromDialog(JDialog dialog)
	{
		return new WindowWrapper(null, dialog);
	}

	public Window getWindow()
	{
		return frame != null ? frame : dialog;
	}

	//

	public static WindowWrapper createFrame(String title)
	{
		JFrame frame = new JFrame(title);
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		return fromFrame(frame);
	}

	public static WindowWrapper createWindow(WindowWrapper owner, String title, ModalityType modalityType)
	{
		if (owner == null) {
			return createFrame(title);
		} else if (owner.frame != null) {
			JDialog dialog = new JDialog(owner.frame, title, modalityType);
			dialog.setLocationByPlatform(true);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			return fromDialog(dialog);
		} else {
			JDialog dialog = new JDialog(owner.dialog, title, modalityType);
			dialog.setLocationByPlatform(true);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			return fromDialog(dialog);
		}
	}

}
