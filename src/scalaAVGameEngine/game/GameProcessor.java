package scalaAVGameEngine.game;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import scala.Int;
import scala.Tuple3;
import scala.concurrent.Await;
import scalaAVGameEngine.assertions.AssertState;
import scalaAVGameEngine.game.events.GameEvent;
import scalaAVGameEngine.graphics.GameScreen;
import scalaAVGameEngine.graphics.Screen;
import scalaAVGameEngine.graphics.twodee.ImageStore;
import scalaAVGameEngine.sWorld.ActionHere;
import scalaAVGameEngine.sWorld.AddItem;
import scalaAVGameEngine.sWorld.RoomActor;
import scalaAVGameEngine.sWorld.RoomObject;
import scalaAVGameEngine.sWorld.SeeInventory;
import scalaAVGameEngine.sWorld.World;
import scalaAVGameEngine.starter.GameActivity;
import scalaAVGameEngine.starter.ScWorldFactory;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import akka.util.Timeout;
import android.util.Log;

public class GameProcessor extends UntypedActor implements Game, Runnable {

	World world;
	volatile boolean running = false;
	Thread gameThread = null;
	GameActivity activity;
	Vector<AssertState> asserts;
	volatile ConcurrentLinkedQueue<GameEvent> eventQueue = new ConcurrentLinkedQueue<GameEvent>();
	Screen screen = null;
	boolean conversing = false;
	ActorRef speakingTo = null;
	public static final double FPS_DELTA = 16;
	ImageStore store;

	public GameProcessor(String gameState, GameActivity activity) {
		// world = WorldFactory.createWorld(gameState, this);
		world = ScWorldFactory.createWorld(gameState);
		this.activity = activity;
		asserts = new Vector<AssertState>();
		setStartScreen();
	}

	public void consumeEvents() {
		synchronized (eventQueue) {
			if (eventQueue != null && !eventQueue.isEmpty()) {
				if (eventQueue.peek().running())
					return;
				if (eventQueue.peek().complete()) {
					eventQueue.poll();
					if (!eventQueue.isEmpty()) {
						synchronized (world) {
							eventQueue.peek().processEvent();
						}
					}
				} else {
					synchronized (world) {
						eventQueue.peek().processEvent();
					}
				}
			}
		}
	}

	public ActorRef getPlayer() {
		return world.player();
	}

	public void resume() {
		running = true;
		gameThread = new Thread(this, "GAME THREAD");
		gameThread.start();
	}

	public void pause() {
		running = false;
		while (true) {
			try {
				gameThread.join();
				break;
			} catch (InterruptedException e) {
				// retry
			}
		}
	}

	public void run() {
		long time = System.nanoTime();
		try {
			Thread.sleep((long) FPS_DELTA);
		} catch (Exception e) {

		}
		float deltaTime = 0;
		while (running) {
			deltaTime = (System.nanoTime() - time) / 1000;
			time = System.nanoTime();
			if (!conversing) {
				consumeEvents();
				getAssertStatus();
			} else {
				if (!eventQueue.isEmpty())
					eventQueue.clear(); // Don't want stale events to trigger
										// after a convo
			}
			world.update(deltaTime);

			try {
				// Sleep
				// deltaTime = System.nanoTime() - time;
				// Log.d("Thread", "Delta = " + deltaTime);
				if (deltaTime < FPS_DELTA)
					Thread.sleep((long) (FPS_DELTA - deltaTime)); // Aiming for
																	// pseudo 60
																	// fps
			} catch (InterruptedException e) {
				Log.d("Game Thead", "Sleep interputed");
			}
		}
	}

	public Screen getCurrentScreen() {
		return screen;
	}

	public void setStartScreen() {
		screen = new GameScreen(activity, world.getActiveRoom());
	}

	public void stateChange(World world) {
//		// getPlayer().tell(new SeeInventory(),self());
//		Timeout timeout = new Timeout(100);
//		Set<RoomObject> inv = null;
//		try {
//			inv = (HashSet<RoomObject>) Await.result(
//					Patterns.ask(getPlayer(), new SeeInventory(), timeout),
//					timeout.duration());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		this.world = world;
//		for (RoomObject ob : inv) {
//			getPlayer().tell(new AddItem(ob), self());
//		}
	}

	public boolean getAssertStatus() {
		for (AssertState state : asserts) {
			if (!state.isTrue())
				return false;
		}
		return true;
	}

	public ActorRef getCurrentRoom() {
		return world.getActiveRoom();
	}

	public void addToEventQueue(GameEvent event) {
//		if (event instanceof MoveRoomActorEvent
//				&& eventQueue.peek() instanceof MoveRoomActorEvent)
//			eventQueue.clear();
//		eventQueue.add(event);
	}

	public World getWorld() {
		return world;
	}

	public void action(int[] xyz) {
		world.getActiveRoom().tell(new ActionHere(new Tuple3(xyz[0],xyz[1],xyz[2])), self());
	}

	public void sayThought(String thought) {
		screen.sayThought(thought);
	}

	public void startConversation(ActorRef a) {
//		conversing = true;
//		speakingTo = a;
//		screen.startConversation(a);
//		a.getConvo().reset();
	}

	public void envConversation() {
		conversing = false;
		speakingTo = null;
		screen.endConversation();
	}

	public boolean isConversing() {
		return conversing;
	}

	public String nextLine() {
//		if (speakingTo != null)
//			return speakingTo.getConvo().nextLine();
//		else
			return "";
	}

	public String[] convoOptions() {
//		if (speakingTo != null)
//			return speakingTo.getConvo().getOptionsAndReset();
//		else
			return null;
	}

	public void endConversation() {
//		conversing = false;
//		speakingTo = null;
//		screen.endConversation();
	}

	@Override
	public void onReceive(Object arg0) throws Exception {
		// TODO Auto-generated method stub

	}

}
