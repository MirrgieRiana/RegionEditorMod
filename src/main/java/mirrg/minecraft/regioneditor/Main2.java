package mirrg.minecraft.regioneditor;

import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import mirrg.boron.swing.UtilsComponent;
import mirrg.boron.util.i18n.I18n;
import mirrg.boron.util.struct.ImmutableArray;
import mirrg.boron.util.struct.Tuple;
import mirrg.boron.util.suppliterator.ISuppliterator;
import mirrg.minecraft.regioneditor.gui.guis.GuiBase;

public class Main2
{

	static int x = 0;
	static int z = 0;
	static int size = 5;

	public static void main(String[] args) throws Exception
	{

		new GuiBase(null, new I18n(), "a", ModalityType.MODELESS) {
			ImmutableArray<ImmutableArray<ImageIcon>> imageIcons = ISuppliterator.rangeClosed(0, size - 1)
				.map(x -> ISuppliterator.rangeClosed(0, size - 1)
					.map(z -> new ImageIcon())
					.toImmutableArray())
				.toImmutableArray();
			ImmutableArray<ImmutableArray<JLabel>> labels = imageIcons.suppliterator()
				.map(x -> x.suppliterator()
					.map(ii -> new JLabel(ii))
					.toImmutableArray())
				.toImmutableArray();

			@Override
			protected void initComponenets()
			{
				windowWrapper.getContentPane().setBackground(Color.gray);

				windowWrapper.getContentPane().setLayout(UtilsComponent.createGroupLayout(windowWrapper.getContentPane(), labels.suppliterator()
					.map(x -> x.toArray(JLabel[]::new))
					.toArray(JLabel[][]::new), 1));
				windowWrapper.getContentPane().setFocusable(true);
				windowWrapper.getContentPane().addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e)
					{
						if (e.getKeyCode() == KeyEvent.VK_LEFT) {
							x--;
						}
						if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
							x++;
						}
						if (e.getKeyCode() == KeyEvent.VK_UP) {
							z--;
						}
						if (e.getKeyCode() == KeyEvent.VK_DOWN) {
							z++;
						}
						updateImages();
					}
				});

				updateImages();
			}

			private void updateImages()
			{
				ISuppliterator.rangeClosed(-size / 2, size / 2)
					.forEach(xi -> ISuppliterator.rangeClosed(-size / 2, size / 2)
						.forEach(zi -> {
							try {
								imageIcons.get(zi + size / 2).get(xi + size / 2).setImage(load(Tuple.of(x + xi, z + zi)));
								labels.get(zi + size / 2).get(xi + size / 2).repaint();
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}));
			}
		}.show();

	}

	private static Map<Tuple<Integer, Integer>, BufferedImage> map = new HashMap<>();

	private static BufferedImage load(Tuple<Integer, Integer> pos) throws Exception
	{
		BufferedImage image = map.get(pos);
		if (image != null) {
			return image;
		}

		image = load(pos.x, pos.y);

		map.put(pos, image);

		return image;
	}

	private static BufferedImage load(int x, int z) throws Exception
	{
		int x2 = x * 4;
		int z2 = z * -4;
		URL url = new URL("http://mimi2.f5.si:17026/tiles/world/flat/" + Math.floorDiv(x2, 32) + "_" + Math.floorDiv(z2, 32) + "/zz_" + x2 + "_" + z2 + ".png?1556325681500");
		return ImageIO.read(url.openStream());
	}

}
