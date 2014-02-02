package scalaAVGameEngine.graphics;

import scalaAVGameEngine.graphics.Graphics.PixmapFormat;
import android.graphics.Bitmap;
import android.graphics.Rect;

public class AnimationPixmap implements Pixmap {

	private Bitmap bitmap;
	private PixmapFormat format;
	private int[] grid;
	private int images;
	int current = 0;
	private float split = 500000f;
	private float delta = 0;
	
	public AnimationPixmap(Bitmap bitmap, PixmapFormat format, int[] grid, int images) {
		this.bitmap = bitmap;
		this.format = format;
		this.grid = grid;
		this.images = images;
	}
	
	public int getWidth() {
		return bitmap.getWidth() / grid[0];
	}

	public int getHeight() {
		return bitmap.getHeight() / grid[1];
	}

	public PixmapFormat getFormat() {
		return format;
	}

	public void dispose() {
		bitmap.recycle();
	}
	
	public Rect getCurrentRect(int i){
		Rect ret = new Rect();
		ret.left = (bitmap.getWidth() / grid[0]) * ((int) i / 2) ;
		ret.right = ret.left + getWidth();
		
		if(i % 2 != 0){
			ret.top = getHeight();
			ret.bottom = bitmap.getHeight();
		}else{
			ret.top = 0;
			ret.bottom = getHeight();
		}
		return ret;
	}
	
	public void update(float newDelta){
		delta = delta + newDelta;
		if(delta > split){
			delta = delta - split;
			if(current == images - 1)
				current = 0;
			else
				current++;
		}
	}

	public Bitmap getBitmap() {
		return bitmap;
	}
}
