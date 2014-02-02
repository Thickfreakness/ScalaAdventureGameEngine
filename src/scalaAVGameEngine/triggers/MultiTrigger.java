package scalaAVGameEngine.triggers;

import java.util.LinkedList;

import scalaAVGameEngine.game.Game;


public class MultiTrigger extends Trigger {

	LinkedList<Trigger> triggers ;
	
	public MultiTrigger(Game game, String id, LinkedList<Trigger> triggers) {
		super(game, id);
		this.triggers = triggers;
	}

	@Override
	public void triggerEvent() {
		for(Trigger trig : triggers){
			trig.triggerEvent();
		}
	}

	@Override
	public boolean triggerable(String compare) {
		for(Trigger trig : triggers){
			if(!trig.triggerable(compare))
				return false;
		}
		return true;
	}
	
	public LinkedList<Trigger> getTriggers(){
		return triggers;
	}

}
