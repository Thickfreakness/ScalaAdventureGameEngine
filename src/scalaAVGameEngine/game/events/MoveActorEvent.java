package scalaAVGameEngine.game.events;

import scalaAVGameEngine.game.Game;


public class MoveActorEvent implements GameEvent {
	
	String actor;
	Game game;
	int x;
	int y;
	int z;
	boolean started = false;
	
	public MoveActorEvent(String actor, Game game, int x, int y, int z){
		this.actor = actor;
		this.game = game;
		this.x = x;
		this.y = y;
		this.z = z;
		
	}
	
	public void processEvent() {
//		game.getCurrentRoom().setActorRouteTo(actor, x, y, z);
		started = true;
	}

	public boolean complete() {
		return (started && !running());
	}

	public boolean running() {
//		return game.getPlayer().isWalking();
		return false;
	}

}
