package scalaAVGameEngine.triggers;

import scalaAVGameEngine.game.Game;

public class ConvoEndTrigger extends Trigger {

	public ConvoEndTrigger(Game game, String string){
		super(game, string);
	}
	@Override
	public void triggerEvent() {
		game.endConversation();
	}

	@Override
	public boolean triggerable(String compare) {
		return true;
	}

}
