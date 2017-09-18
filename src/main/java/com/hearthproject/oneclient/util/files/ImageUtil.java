package com.hearthproject.oneclient.util.files;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hearthproject.oneclient.util.logging.OneClientLogging;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class ImageUtil {

	private static final Font font = new Font("Arial Black", Font.BOLD, 80);
	public static final Cache<String, Image> IMAGE_CACHE = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();

	public static int SIZE = 192;

	public static void createIcon(String text, File image) {
		BufferedImage bufferedImage = new BufferedImage(SIZE, SIZE,
			BufferedImage.TYPE_INT_RGB);
		Graphics graphics = bufferedImage.getGraphics();
		graphics.setColor(new Color(0x28, 0x28, 0x28));
		graphics.fillRect(0, 0, SIZE, SIZE);
		graphics.setColor(Color.WHITE);
		graphics.setFont(font);
		Rectangle rect = getStringBounds(graphics, text, 0, 0);
		graphics.drawString(text, SIZE / 2 - (int) (rect.getWidth() / 2), SIZE / 2 + ((int) rect.getHeight() / 2));
		try {
			ImageIO.write(bufferedImage, "png", image);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Rectangle getStringBounds(Graphics g2, String str,
	                                         float x, float y) {
		FontRenderContext frc = ((Graphics2D) g2).getFontRenderContext();
		GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
		return gv.getPixelBounds(null, x, y);
	}

	public static Image openImage(File file) {
		if (file == null || !file.exists()) {
			return null;
		}
		Image image = null;
		try {
			image = new Image(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			OneClientLogging.error(e);
		}
		return image;
	}

	public static Image openImage(InputStream stream) {
		return new Image(stream);
	}

	public static Image openCachedImage(File file, String name) {
		OneClientLogging.logger.debug("Opening Image : {}", file);
		if (file == null || !file.exists()) {
			return null;
		}
		Image image = IMAGE_CACHE.getIfPresent(file.getName());
		if (image == null) {
			try {
				image = new Image(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				OneClientLogging.error(e);
			}
			if (image == null)
				return null;
			IMAGE_CACHE.put(name, image);
		}
		return image;
	}

	public static Image openCachedImage(File file) {
		return openCachedImage(file, file.getName());
	}

	public static Image openCachedImage(InputStream inputStream, String name) {
		OneClientLogging.logger.debug("Opening Image : {}", name);
		Image image = IMAGE_CACHE.getIfPresent(name);
		if (image == null) {
			image = new Image(inputStream);
			IMAGE_CACHE.put(name, image);
		}
		try {
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

}
