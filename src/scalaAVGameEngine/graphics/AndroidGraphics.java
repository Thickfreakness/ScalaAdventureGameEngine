package scalaAVGameEngine.graphics;

import java.io.IOException;
import java.io.InputStream;

import scalaAVGameEngine.graphics.twodee.ImageStore;
import scalaAVGameEngine.graphics.twodee.ImageStoreFactory;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

public class AndroidGraphics implements Graphics {
	AssetManager assets;
	Bitmap frameBuffer;
	Canvas canvas;
	Paint paint;
	Rect srcRect = new Rect();
	Rect dstRect = new Rect();
	ImageStore store;
	
	public void updateStore(String path){
		store = ImageStoreFactory.buildImageStore(path, this);
	}
	
	public AndroidGraphics(AssetManager assets, Bitmap frameBuffer) {
		this.assets = assets;
		this.frameBuffer = frameBuffer;
		this.canvas = new Canvas(frameBuffer);
		this.paint = new Paint();
	}

	public Pixmap newPixmap(String fileName, PixmapFormat format) {
		Config config = null;
		if (format == PixmapFormat.RGB565)
			config = Config.RGB_565;
		else if (format == PixmapFormat.ARGB4444)
			config = Config.ARGB_4444;
		else
			config = Config.ARGB_8888;

		Options options = new Options();
		options.inPreferredConfig = config;

		InputStream in = null;
		Bitmap bitmap = null;
		try {
			in = assets.open(fileName);
			bitmap = BitmapFactory.decodeStream(in);
			if (bitmap == null)
				throw new RuntimeException("Couldn't load bitmap from asset '"
						+ fileName + "'");
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load bitmap from asset '"
					+ fileName + "'");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}

		if (bitmap.getConfig() == Config.RGB_565)
			format = PixmapFormat.RGB565;
		else if (bitmap.getConfig() == Config.ARGB_4444)
			format = PixmapFormat.ARGB4444;
		else
			format = PixmapFormat.ARGB8888;

		return new AndroidPixmap(bitmap, format);
	}
	
	public Pixmap newAnimationPixmap(String fileName, PixmapFormat format, int[] grid, int images) {
		Config config = null;
		if (format == PixmapFormat.RGB565)
			config = Config.RGB_565;
		else if (format == PixmapFormat.ARGB4444)
			config = Config.ARGB_4444;
		else
			config = Config.ARGB_8888;

		Options options = new Options();
		options.inPreferredConfig = config;

		InputStream in = null;
		Bitmap bitmap = null;
		try {
			in = assets.open(fileName);
			bitmap = BitmapFactory.decodeStream(in);
			if (bitmap == null)
				throw new RuntimeException("Couldn't load bitmap from asset '"
						+ fileName + "'");
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load bitmap from asset '"
					+ fileName + "'");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}

		if (bitmap.getConfig() == Config.RGB_565)
			format = PixmapFormat.RGB565;
		else if (bitmap.getConfig() == Config.ARGB_4444)
			format = PixmapFormat.ARGB4444;
		else
			format = PixmapFormat.ARGB8888;

		return new AnimationPixmap(bitmap, format, grid, images);
	}

	public void clear(int color) {
		canvas.drawRGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8,
				(color & 0xff));
	}

	public void drawPixel(int x, int y, int color) {
		paint.setColor(color);
		canvas.drawPoint(x, y, paint);
	}

	public void drawLine(int x, int y, int x2, int y2, int color) {
		paint.setColor(color);
		canvas.drawLine(x, y, x2, y2, paint);
	}

	public void drawRect(int x, int y, int width, int height, int color) {
		paint.setColor(color);
		paint.setStyle(Style.FILL);
		canvas.drawRect(x, y, x + width - 1, y + height - 1, paint);
	}
	
	public void drawRectNoFill(int x, int y, int width, int height, int color) {
		paint.setColor(color);
		paint.setStyle(Style.STROKE);
		canvas.drawRect(x, y, x + width - 1, y + height - 1, paint);
	}

	public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY,
			int srcWidth, int srcHeight) {
		srcRect.left = srcX;
		srcRect.top = srcY;
		srcRect.right = srcX + srcWidth - 1;
		srcRect.bottom = srcY + srcHeight - 1;
		
		dstRect.left = x;
		dstRect.top = y;
		dstRect.right = x + srcWidth - 1;
		dstRect.bottom = y + srcHeight - 1;

		canvas.drawBitmap(pixmap.getBitmap(), srcRect, dstRect,
				null);
	}

	public void drawPixmap(Pixmap pixmap, int x, int y) {
		canvas.drawBitmap(pixmap.getBitmap(), x, y, null);
	}

	public int getWidth() {
		return frameBuffer.getWidth();
	}

	public int getHeight() {
		return frameBuffer.getHeight();
	}

	public void drawThisPixmap(Pixmap pixmap, int x, int y, int current) {
		dstRect.left = x;
		dstRect.top = y;
		dstRect.right = x + pixmap.getWidth();
		dstRect.bottom = y + pixmap.getHeight();
		
		canvas.drawBitmap(pixmap.getBitmap(), pixmap.getCurrentRect(current), dstRect,
				null);
	}
	
	public void drawThisPixmap(Pixmap pixmap, int x, int y, int width, int height, int current) {
		dstRect.left = x;
		dstRect.top = y;
		dstRect.right = x + width ;
		dstRect.bottom = y + height;
		
		canvas.drawBitmap(pixmap.getBitmap(), pixmap.getCurrentRect(current), dstRect,
				null);
	}

	public ImageStore getStore() {
		return store;
	}

	public void drawText(String text, int x, int y, int size,int colour) {
		paint.setTextSize(size);
		paint.setColor(colour);
		paint.setFakeBoldText(true);
		canvas.drawText(text, x, y, paint);
	}

	public void drawPixmap(Pixmap pixmap, int x, int y, Rect dest) {
		srcRect.left = 0;
		srcRect.top = 0;
		srcRect.right = pixmap.getWidth() - 1;
		srcRect.bottom = pixmap.getHeight() - 1;
		
		canvas.drawBitmap(pixmap.getBitmap(), srcRect, dest,
				null);
	}

}
