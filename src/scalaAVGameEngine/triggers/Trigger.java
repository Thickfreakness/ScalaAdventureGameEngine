package scalaAVGameEngine.triggers;

import scalaAVGameEngine.game.Game;

public abstract class Trigger {
	
	protected Game game;
	protected String id;
	
	public Trigger(Game game, String id){
		this.game = game;
		this.id = id;
	}
	
	public abstract void triggerEvent();
	
	public abstract boolean triggerable(String compare);
}
