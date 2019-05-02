package mirrg.minecraft.regioneditor.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import mirrg.boron.util.UtilsFile;
import mirrg.boron.util.string.PercentEncoding;
import mirrg.boron.util.struct.Tuple;
import mirrg.boron.util.suppliterator.ISuppliterator;

/**
 * 1枚の画像のサイズは128x128。8チャンクx8チャンク。
 */
public class DynmapImageLoader
{

	private static final int MEMORY_CACHE_LIMIT = 300;
	private static final long FILE_CACHE_TIME = 3 * 1000 * 60 * 60 * 24;

	private final String templateUrl;
	private final File dirCache;

	public DynmapImageLoader(String templateUrl)
	{
		this.templateUrl = templateUrl;
		this.dirCache = new File("./regioneditor/cache/" + PercentEncoding.encode(templateUrl));
	}

	private Map<Tuple<Integer, Integer>, Tuple<BufferedImage, Long>> map = new HashMap<>();

	public BufferedImage get(int imageX, int imageZ) throws IOException
	{
		Tuple<Integer, Integer> imagePos = new Tuple<>(imageX, imageZ);

		Tuple<BufferedImage, Long> tuple = map.get(imagePos);
		if (tuple != null) {
			// キャッシュにある場合はそれを使う
			// タイムスタンプを更新する
			tuple = new Tuple<>(tuple.x, System.nanoTime());
		} else {
			// キャッシュにない場合はロードしてそれを使う
			tuple = new Tuple<>(load(imageX, imageZ), System.nanoTime());
		}

		// キャッシュを更新する
		map.put(imagePos, tuple);
		while (map.size() > MEMORY_CACHE_LIMIT) {
			map.remove(ISuppliterator.ofIterable(map.entrySet())
				.min(e -> e.getValue().y, Long::compareTo)
				.get().getKey());
		}

		return tuple.x;
	}

	private BufferedImage load(int imageX, int imageZ) throws IOException
	{
		BufferedImage image;

		// キャッシュ場所の算出
		File fileCache = new File(dirCache, imageX + "," + imageZ + ".png");

		// 要求URLの算出
		int x2 = imageX * 4;
		int z2 = imageZ * -4;
		int x1 = Math.floorDiv(x2, 32);
		int z1 = Math.floorDiv(z2, 32);
		String stringUrl = templateUrl;
		stringUrl = stringUrl.replaceAll("\\$\\{x1\\}", "" + x1);
		stringUrl = stringUrl.replaceAll("\\$\\{z1\\}", "" + z1);
		stringUrl = stringUrl.replaceAll("\\$\\{x2\\}", "" + x2);
		stringUrl = stringUrl.replaceAll("\\$\\{z2\\}", "" + z2);
		URL url = new URL(stringUrl);

		// 要求
		image = load(url, fileCache);

		return image;
	}

	private BufferedImage load(URL url, File fileCache) throws IOException
	{

		// ファイルが古いなら無視
		if (fileCache.isFile()) {
			long millis = Instant.now().toEpochMilli() - fileCache.lastModified();
			if (millis > FILE_CACHE_TIME) {
				// 古い
				fileCache.delete();
			}
		}

		// キャッシュが存在しないなら取り寄せる
		if (!fileCache.exists()) {
			try (InputStream in = url.openStream();
				OutputStream out = UtilsFile.getOutputStreamWithMkdirs(fileCache)) {
				byte[] buffer = new byte[4096];
				while (true) {
					int length = in.read(buffer);
					if (length == -1) break;

					out.write(buffer, 0, length);
				}
			}
		}

		// キャッシュが存在するならそれを使う
		if (fileCache.isFile()) {
			return ImageIO.read(fileCache);
		}
		throw new IOException("Unknown exception: (" +
			("File: " + fileCache) + ", " +
			("exists: " + fileCache.exists()) + ", " +
			("isFile: " + fileCache.isFile()) + ", " +
			("isDirectory: " + fileCache.isDirectory()) + ")");
	}

}
