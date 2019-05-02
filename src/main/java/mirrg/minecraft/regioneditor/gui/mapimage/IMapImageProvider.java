package mirrg.minecraft.regioneditor.gui.mapimage;

import java.awt.Graphics2D;

public interface IMapImageProvider
{

	public void draw(Graphics2D graphics, int srcX, int srcZ, int width, int height);

}
