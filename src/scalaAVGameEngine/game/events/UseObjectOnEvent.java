package scalaAVGameEngine.game.events;

import scalaAVGameEngine.world.RoomObject;

public class UseObjectOnEvent implements GameEvent {

	RoomObject source;
	RoomObject actor;
	boolean complete = false;
	
	public UseObjectOnEvent(RoomObject source, RoomObject actor){
		this.source = source;
		this.actor = actor;
	}
	
	public void processEvent() {
		source.action(actor.getLabel());
		complete = true;
	}

	public boolean complete() {
		return complete;
	}
	
	public boolean running(){
		return false;
	}

}
