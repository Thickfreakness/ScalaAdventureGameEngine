package scalaAVGameEngine.graphics;

import scalaAVGameEngine.graphics.twodee.ImageStore;
import android.graphics.Rect;


public interface Graphics {
    public static enum PixmapFormat {
        ARGB8888, ARGB4444, RGB565
    }

    public Pixmap newPixmap(String fileName, PixmapFormat format);
    
    public Pixmap newAnimationPixmap(String fileName, PixmapFormat format, int[] grid, int images);

    public void clear(int color);

    public void drawPixel(int x, int y, int color);

    public void drawLine(int x, int y, int x2, int y2, int color);

    public void drawRect(int x, int y, int width, int height, int color);
    
    public void drawRectNoFill(int x, int y, int width, int height, int color);

    public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY,
            int srcWidth, int srcHeight);
    
    public void drawPixmap(Pixmap pixmap, int x, int y, Rect dest);

    public void drawPixmap(Pixmap pixmap, int x, int y);
    
    public void drawThisPixmap(Pixmap pixmap, int x, int y, int current);

    public int getWidth();

    public int getHeight();
    
    public ImageStore getStore();
    
    public void updateStore(String path);
    
    public void drawText(String text, int x, int y, int size, int colour);
    
    public void drawThisPixmap(Pixmap pixmap, int x, int y, int width, int height, int current);
}
