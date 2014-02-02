package scalaAVGameEngine.triggers;

import scalaAVGameEngine.game.Game;
import scalaAVGameEngine.starter.WorldFactory;

public class CombineTrigger extends Trigger {

	String thisItem = "";
	String partner = "";
	String destination = "";
	
	public CombineTrigger(Game game, String thisItem, String partner, String destination){
		super(game,thisItem);
		this.thisItem = thisItem;
		this.partner = partner;
		this.destination = destination;
	}
	@Override
	public void triggerEvent() {
//		game.getPlayer().removeFromInventory(thisItem);
//		game.getPlayer().removeFromInventory(partner);
//		game.getPlayer().getInventory().add(WorldFactory.createObject(destination,game));
	}

	@Override
	public boolean triggerable(String compare) {
		if(compare.equalsIgnoreCase(partner)){
			return true;
		}
		else
			return false;
	}

}
