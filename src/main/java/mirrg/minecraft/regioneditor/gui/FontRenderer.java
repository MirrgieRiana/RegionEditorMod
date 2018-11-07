package mirrg.minecraft.regioneditor.gui;

import java.awt.Graphics;
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
	public static void drawCharacter(Graphics g, char ch, int x, int y)
	{
		int sx;
		int sy;
		if ('0' <= ch && ch <= '9') {
			sx = ch - '0';
			sy = 0;
		} else {
			sx = 0;
			sy = 0;
		}

		g.drawImage(
			imageFont,
			x,
			y,
			x + FONT_WIDTH,
			y + FONT_HEIGHT,
			sx * FONT_WIDTH,
			sy * FONT_HEIGHT,
			sx * FONT_WIDTH + FONT_WIDTH,
			sy * FONT_HEIGHT + FONT_HEIGHT,
			null);
	}

	/**
	 * 指定座標を上端中心として描画する。
	 */
	public static void drawString(Graphics g, String string, int x, int y)
	{
		for (int i = 0; i < string.length(); i++) {
			drawCharacter(
				g,
				string.charAt(i),
				x - FONT_WIDTH / 2 - OFFSET_X / 2 * (string.length() - 1) + i * OFFSET_X,
				y);
		}
	}

}
