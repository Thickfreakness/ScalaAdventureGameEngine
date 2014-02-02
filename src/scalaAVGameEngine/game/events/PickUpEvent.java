package scalaAVGameEngine.game.events;

import scalaAVGameEngine.game.Game;
import scalaAVGameEngine.world.RoomObject;

public class PickUpEvent implements GameEvent {

	Game game;
	String label;
	public PickUpEvent(Game game, String label){
		this.game = game;
		this.label = label;
	}
	
	public void processEvent() {
//		RoomObject item = game.getCurrentRoom().removeObject(label);
//		if(item != null){
//			game.getPlayer().addToInventory(item);
//		}
	}

	public boolean complete() {
//		if(game.getPlayer().popItemFromInventory(label) != null){
			return true;
//		}else
//			return false;
	}
	
	public boolean running(){
		return false;
	}

}
