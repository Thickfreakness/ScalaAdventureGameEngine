package scalaAVGameEngine.io;

import java.util.List;

import scalaAVGameEngine.io.Input.TouchEvent;

import android.view.View.OnTouchListener;


public interface TouchHandler extends OnTouchListener {
    public boolean isTouchDown(int pointer);
    
    public int getTouchX(int pointer);
    
    public int getTouchY(int pointer);
    
    public List<TouchEvent> getTouchEvents();
}
