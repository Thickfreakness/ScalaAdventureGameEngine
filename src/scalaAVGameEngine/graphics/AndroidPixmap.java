package scalaAVGameEngine.graphics;

import scalaAVGameEngine.graphics.Graphics.PixmapFormat;
import android.graphics.Bitmap;
import android.graphics.Rect;


public class AndroidPixmap implements Pixmap {
    private Bitmap bitmap;
    private PixmapFormat format;
    
    public AndroidPixmap(Bitmap bitmap, PixmapFormat format) {
        this.bitmap = bitmap;
        this.format = format;
    }

    public int getWidth() {
        return bitmap.getWidth();
    }

    public int getHeight() {
        return bitmap.getHeight();
    }

    public PixmapFormat getFormat() {
        return format;
    }

    public void dispose() {
        bitmap.recycle();
    }

	public Rect getCurrentRect(int i) {
		Rect ret = new Rect();
		ret.left = 0;
		ret.right = getWidth();
		ret.top = 0;
		ret.bottom = getHeight();
		return ret;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}      
}
