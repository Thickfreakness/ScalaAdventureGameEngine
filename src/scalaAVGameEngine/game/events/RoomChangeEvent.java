package scalaAVGameEngine.game.events;

import scalaAVGameEngine.game.Game;

public class RoomChangeEvent implements GameEvent {

	Game game;
	String destination;
	boolean complete = false;
	
	public RoomChangeEvent(Game game, String destination){
		this.game = game;
		this.destination = destination;
	}
	
	public void processEvent() {
		game.getWorld().setActiveRoom(destination);
		complete = true;
	}

	public boolean complete() {
		return complete;
	}
	
	public boolean running(){
		return false;
	}

}
