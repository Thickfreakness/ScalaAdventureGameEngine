package scalaAVGameEngine.starter;

import scalaAVGameEngine.game.Game;
import scalaAVGameEngine.game.GameProcessor;
import scalaAVGameEngine.graphics.AndroidGraphics;
import scalaAVGameEngine.graphics.FastRenderView;
import scalaAVGameEngine.graphics.Graphics;
import scalaAVGameEngine.graphics.Screen;
import scalaAVGameEngine.io.AndroidFileIO;
import scalaAVGameEngine.io.AndroidInput;
import scalaAVGameEngine.io.FileIO;
import scalaAVGameEngine.io.Input;
import scalaAVGameEngine.sounds.AndroidAudio;
import scalaAVGameEngine.sounds.Audio;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity{
	FastRenderView render;
	Graphics graphics;
    Audio audio;
    Input input;
    FileIO fileIO;
    WakeLock wakeLock;
    protected static Context myContext;
    Screen screen;
    Game game;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
    	 myContext = this;
         requestWindowFeature(Window.FEATURE_NO_TITLE);
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                 WindowManager.LayoutParams.FLAG_FULLSCREEN);

         setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
         boolean isLandscape = true;
         int frameBufferWidth = 1280;
         int frameBufferHeight = 720;
         Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth,
                 frameBufferHeight, Config.ARGB_8888);
         
         float scaleX = (float) frameBufferWidth
                 / getWindowManager().getDefaultDisplay().getWidth();
         float scaleY = (float) frameBufferHeight
                 / getWindowManager().getDefaultDisplay().getHeight();

         render = new FastRenderView(this, frameBuffer);
         graphics = new AndroidGraphics(getAssets(), frameBuffer);
         fileIO = new AndroidFileIO(getAssets());
         audio = new AndroidAudio(this);
         input = new AndroidInput(this, render, scaleX, scaleY);
         game = new GameProcessor("GameLogic/core/World01.xml", this);
         screen = game.getCurrentScreen();
         graphics.updateStore("GameLogic/core/World01.xml");
         setContentView(render);
         
         PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
         wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GLGame");
         
//         render.resume();
    }

    public void onResume() {
        super.onResume();
        wakeLock.acquire();
        render.resume();
        game.resume();
    }

    public void onStart(){
    	super.onStart();
//    	 wakeLock.acquire();
//    	onResume();
//    	game.resume();
    }
    
    public void onRestart(){
    	super.onRestart();
//    	 wakeLock.acquire();
////    	onResume();
//    	game.resume();
//    	render.resume();
    }
    
    public void onPause() {
    	super.onPause();
        wakeLock.release();
        render.pause();
        game.pause();
        if (isFinishing())
            screen.dispose();
        
    }
    
    public void onStop(){
    	super.onStop();
//    	wakeLock.release();
//    	render.pause();
//    	game.pause();
//    	screen.dispose();
    	
    }

    public Input getInput() {
        return input;
    }

    public FileIO getFileIO() {
        return fileIO;
    }

    public Graphics getGraphics() {
        return graphics;
    }

    public Audio getAudio() {
        return audio;
    }

	public void setScreen(Screen screen) {
		this.screen = screen;
	}

	public Screen getCurrentScreen() {
		return screen;
	}
	
	public Game getGame(){
		return game;
	}
}