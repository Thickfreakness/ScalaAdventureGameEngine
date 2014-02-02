package scalaAVGameEngine.triggers;

import scalaAVGameEngine.game.Game;
import scalaAVGameEngine.starter.WorldFactory;

public class WorldChangeTrigger extends Trigger {

	public WorldChangeTrigger(Game game, String id) {
		super(game, id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void triggerEvent() {
//		game.stateChange(WorldFactory.createWorld(id, game));
	}

	/**
	 * @TODO Another if world idle type check
	 */
	public boolean triggerable(String compare) {
		return true;
	}

}
