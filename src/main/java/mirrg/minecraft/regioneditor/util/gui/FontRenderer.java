package mirrg.minecraft.regioneditor.util.gui;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class FontRenderer
{

	private BitMapFont font;

	public FontRenderer(BitMapFont font)
	{
		this.font = font;
	}

	/**
	 * 指定座標を左上として描画する。
	 */
	public void drawCharacter(BufferedImage image, char ch, int x, int y, Color color)
	{
		int sx;
		int sy;

		if (!(' ' <= ch && ch <= '~')) {
			ch = '?';
		}
		sx = ch & 0xF;
		sy = ((ch >> 4) & 0xF) - 2;

		int br = color.getRed();
		int bg = color.getGreen();
		int bb = color.getBlue();

		for (int xi = 0; xi < font.width; xi++) {
			for (int yi = 0; yi < font.height; yi++) {
				if (x + xi < 0) continue;
				if (x + xi >= image.getWidth()) continue;
				if (y + yi < 0) continue;
				if (y + yi >= image.getHeight()) continue;

				int s = font.image.getRGB(sx * font.width + xi, sy * font.height + yi);
				int d = image.getRGB(x + xi, y + yi);

				int sa = (s >> 24) & 0xff;
				int sr = ((s >> 16) & 0xff) * br / 255;
				int sg = ((s >> 8) & 0xff) * bg / 255;
				int sb = ((s >> 0) & 0xff) * bb / 255;
				int dr = (d >> 16) & 0xff;
				int dg = (d >> 8) & 0xff;
				int db = (d >> 0) & 0xff;

				int cr = (sr * sa + dr * (255 - sa)) / 255;
				int cg = (sg * sa + dg * (255 - sa)) / 255;
				int cb = (sb * sa + db * (255 - sa)) / 255;

				int c = (cr << 16) | (cg << 8) | (cb << 0);

				image.setRGB(x + xi, y + yi, c);
			}
		}

	}

	/**
	 * 指定座標を上端中心として描画する。
	 */
	public void drawString(BufferedImage image, String string, int x, int y, Color color)
	{
		for (int i = 0; i < string.length(); i++) {
			drawCharacter(
				image,
				string.charAt(i),
				x - font.width / 2 - font.offsetX / 2 * (string.length() - 1) + i * font.offsetX,
				y,
				color);
		}
	}

}
