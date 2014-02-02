package scalaAVGameEngine.game.events;

import scalaAVGameEngine.game.Game;

public class ThoughtEvent implements GameEvent {

	String thought ;
	Game game;
	boolean complete = false;
	
	public ThoughtEvent(String thought, Game game){
		this.thought = thought;
		this.game = game;
	}
	
	public void processEvent() {
		game.sayThought(thought);
		complete = true;
	}

	public boolean complete() {
		return complete;
	}
	
	public boolean running(){
		return false;
	}

}
