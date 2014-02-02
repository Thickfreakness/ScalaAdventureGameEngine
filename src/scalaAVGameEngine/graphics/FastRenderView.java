package scalaAVGameEngine.graphics;

import java.util.List;

import scalaAVGameEngine.io.Input.TouchEvent;
import scalaAVGameEngine.starter.GameActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class FastRenderView extends SurfaceView implements Runnable {
	GameActivity activity;
	Bitmap framebuffer;
	Thread renderThread = null;
	SurfaceHolder holder;
	volatile boolean running = false;
	// private static double scaleX = 1;
	private static double scaleY = 1;

	public FastRenderView(GameActivity game, Bitmap framebuffer) {
		super(game);
		this.activity = game;
		this.framebuffer = framebuffer;
		this.holder = getHolder();
	}

	public void resume() {
		if (!running) {
			running = true;
			if(renderThread == null || !renderThread.isAlive()){
				renderThread = new Thread(this, "RENDER THREAD");
				renderThread.start();
			}
		}
	}

	public void run() {
		Rect dstRect = new Rect();
		long time = System.nanoTime();
		while (!holder.getSurface().isValid()) {
			try {
				Thread.sleep(100);
				if(!running) return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Canvas canvas = holder.lockCanvas();
		Rect ret = canvas.getClipBounds();
		// scaleX = ((double) ret.width()) / ((double) framebuffer.getWidth());
//		scaleY = ((double) ret.height()) / ((double) framebuffer.getHeight());
		holder.unlockCanvasAndPost(canvas);
		while (running) {
//			if (!holder.getSurface().isValid()) {
//				try {
//					Thread.sleep(100);
//					if(running == false)
//						return;
//				} catch (Exception e) {
//
//				}
//				continue;
//			}

			float deltaTime = (System.nanoTime() - time) / 1000;
			time = System.nanoTime();
			handleEvents();
			activity.getCurrentScreen().update(deltaTime);

			canvas = holder.lockCanvas();

			activity.getCurrentScreen().present(deltaTime);

			canvas.getClipBounds(dstRect);
			canvas.drawBitmap(framebuffer, null, dstRect, null);
			holder.unlockCanvasAndPost(canvas);

//			try {
//				// Sleep
////				deltaTime = System.nanoTime() - time;
////				// Log.d("Thread", "Delta = " + deltaTime);
////				if (deltaTime < 1666)
////					Thread.sleep((long) deltaTime); // Aiming for pseudo 60 fps
//			} catch (InterruptedException e) {
//				Log.d("The Controller", "Sleep interputed");
//			}
		}
	}

	private void handleEvents() {
		List<TouchEvent> events = activity.getInput().getTouchEvents();
		synchronized (events) {
			for (TouchEvent event : events) {
				event.x = (event.x / scaleY);
				event.y = (event.y / scaleY);
				activity.getCurrentScreen().handleEvent(event);
			}
		}
	}

	public void pause() {
		running = false;
		while (true) {
			try {
				renderThread.join();
				break;
			} catch (InterruptedException e) {
				// retry
			}
		}
	}
}
