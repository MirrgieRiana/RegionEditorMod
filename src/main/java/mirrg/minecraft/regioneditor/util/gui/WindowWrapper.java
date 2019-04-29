package mirrg.minecraft.regioneditor.util.gui;

import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.WindowConstants;

public final class WindowWrapper
{

	public final WindowWrapper parent;
	public final JFrame frame;
	public final JDialog dialog;

	private WindowWrapper(WindowWrapper parent, JFrame frame, JDialog dialog)
	{
		this.parent = parent;
		this.frame = frame;
		this.dialog = dialog;
	}

	//

	public static WindowWrapper fromFrame(WindowWrapper parent, JFrame frame)
	{
		return new WindowWrapper(parent, frame, null);
	}

	public static WindowWrapper fromDialog(WindowWrapper parent, JDialog dialog)
	{
		return new WindowWrapper(parent, null, dialog);
	}

	public Window getWindow()
	{
		return frame != null ? frame : dialog;
	}

	public Container getContentPane()
	{
		return frame != null ? frame.getContentPane() : dialog.getContentPane();
	}

	public JRootPane getRootPane()
	{
		return frame != null ? frame.getRootPane() : dialog.getRootPane();
	}

	public void setContentPane(Container contentPane)
	{
		if (frame != null) {
			frame.setContentPane(contentPane);
		} else {
			dialog.setContentPane(contentPane);
		}
	}

	public void setJMenuBar(JMenuBar menuBar)
	{
		if (frame != null) {
			frame.setJMenuBar(menuBar);
		} else if (dialog != null) {
			dialog.setJMenuBar(menuBar);
		}
	}

	//

	public static WindowWrapper createFrame(String title)
	{
		JFrame frame = new JFrame(title);
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		return fromFrame(null, frame);
	}

	public static WindowWrapper createWindow(WindowWrapper owner, String title, ModalityType modalityType)
	{
		if (owner == null) {
			return createFrame(title);
		} else if (owner.frame != null) {
			JDialog dialog = new JDialog(owner.frame, title, modalityType);
			dialog.setLocationByPlatform(true);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			return fromDialog(owner, dialog);
		} else {
			JDialog dialog = new JDialog(owner.dialog, title, modalityType);
			dialog.setLocationByPlatform(true);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			return fromDialog(owner, dialog);
		}
	}

}
