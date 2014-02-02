package scalaAVGameEngine.game.events;

public interface GameEvent {
	public void processEvent();
	public boolean complete();
	public boolean running();
}
