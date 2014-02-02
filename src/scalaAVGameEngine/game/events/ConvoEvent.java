package scalaAVGameEngine.game.events;

import scalaAVGameEngine.game.Game;
import scalaAVGameEngine.world.RActor;

public class ConvoEvent implements GameEvent {

	Game game;
	RActor a;
	boolean started = false;
	
	public ConvoEvent(Game game, RActor a){
		this.game = game;
		this.a = a;
	}
	
	public void processEvent() {
//		game.startConversation(a);
		started = true;
	}
	
	public boolean complete(){
		if(!game.isConversing() && started){
			return true;
		}else
			return false;
	}
	
	public boolean running(){
		return started;
	}
}
