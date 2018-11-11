package mirrg.minecraft.regioneditor.gui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;

import javax.imageio.ImageIO;

public class FontRenderer
{

	private static BufferedImage imageFont;
	static {
		try {
			imageFont = ImageIO.read(CanvasMap.class.getResourceAsStream("font.png"));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static final int FONT_WIDTH = 5;
	private static final int FONT_HEIGHT = 7;
	private static final int OFFSET_X = 4;

	/**
	 * 指定座標を左上として描画する。
	 */
	public static void drawCharacter(BufferedImage image, char ch, int x, int y, Color color)
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

		for (int xi = 0; xi < FONT_WIDTH; xi++) {
			for (int yi = 0; yi < FONT_HEIGHT; yi++) {
				if (x + xi < 0) continue;
				if (x + xi >= image.getWidth()) continue;
				if (y + yi < 0) continue;
				if (y + yi >= image.getHeight()) continue;

				int s = imageFont.getRGB(sx * FONT_WIDTH + xi, sy * FONT_HEIGHT + yi);
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
	public static void drawString(BufferedImage image, String string, int x, int y, Color color)
	{
		for (int i = 0; i < string.length(); i++) {
			drawCharacter(
				image,
				string.charAt(i),
				x - FONT_WIDTH / 2 - OFFSET_X / 2 * (string.length() - 1) + i * OFFSET_X,
				y,
				color);
		}
	}

}
