package scalaAVGameEngine.triggers;

import scalaAVGameEngine.game.Game;
import scalaAVGameEngine.game.events.MoveActorEvent;

public class MovePlayerTrigger extends Trigger {

	public MovePlayerTrigger(Game game, String id) {
		super(game,id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void triggerEvent() {
		String[] xyzS = id.split(",");
		int[] xyz = new int[3];
		if(xyzS.length == 2){
			xyz[0] = Integer.parseInt(xyzS[0]);
			xyz[1] = Integer.parseInt(xyzS[1]);
			xyz[2] = Integer.parseInt(xyzS[2]);
			
			game.addToEventQueue(new MoveActorEvent(id,
					game, xyz[0], xyz[1], xyz[2]));
		}
	}

	/**
	 * @TODO Possible location check etc?
	 */
	public boolean triggerable(String compare) {
		return true;
	}

}
