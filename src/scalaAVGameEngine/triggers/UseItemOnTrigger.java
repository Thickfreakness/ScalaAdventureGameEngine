package scalaAVGameEngine.triggers;

import scalaAVGameEngine.game.Game;

public class UseItemOnTrigger extends Trigger {

	public static enum USE_TYPES {
		STATE_SET, OBJECT_REMOVE, OBJECT_PICKUP, OBJECT_PICKUP_AND_DISPOSE, STATE_SET_AND_DISPOSE
	}

	USE_TYPES type;
	String effector = "";
	String stateSet = "";

	public UseItemOnTrigger(Game game, String item, String effector,
			USE_TYPES type) {
		super(game, item);
		this.effector = effector;
		this.type = type;
	}
	
	public UseItemOnTrigger(Game game, String item, String effector,
			USE_TYPES type, String stateSet) {
		super(game, item);
		this.effector = effector;
		this.type = type;
		this.stateSet = stateSet;
	}

	@Override
	public void triggerEvent() {
//		switch (type) {
//		case STATE_SET:
//			game.getCurrentRoom().getObjects().get(id).setState(stateSet);
//			break;
//		case STATE_SET_AND_DISPOSE:
//			game.getCurrentRoom().getObjects().get(id).setState(stateSet);
//			game.getPlayer().removeFromInventory(effector);
//			break;
//		case OBJECT_REMOVE:
//			game.getCurrentRoom().removeObject(id);
//			break;
//		case OBJECT_PICKUP_AND_DISPOSE:
//			game.getPlayer().addToInventory(
//					game.getCurrentRoom().removeObject(id));
//			game.getPlayer().removeFromInventory(effector);
//			break;
//		case OBJECT_PICKUP:
//			game.getPlayer().addToInventory(
//					game.getCurrentRoom().removeObject(id));
//			break;
//		}
	}

	@Override
	public boolean triggerable(String compare) {
		if (effector.equalsIgnoreCase(effector))
			return true;
		else
			return false;
	}

	public String getEffector(){
		return effector;
	}
	
}
