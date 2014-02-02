package scalaAVGameEngine.triggers;

import scalaAVGameEngine.game.Game;
import scalaAVGameEngine.game.events.PickUpEvent;
import scalaAVGameEngine.game.events.ThoughtEvent;

public class PickUpTrigger extends Trigger {
	private String thought = "";
	public PickUpTrigger(Game game, String id, String thought) {
		super(game,id);
		this.thought = thought;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void triggerEvent() {
		game.addToEventQueue(new PickUpEvent(game, id));
		game.addToEventQueue(new ThoughtEvent(thought, game));
	}

	/**
	 * @TODO Possibly add some sort of "If charecter idle" check here
	 */
	public boolean triggerable(String compare) {
		return true;
	}

}
