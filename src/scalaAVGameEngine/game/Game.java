package scalaAVGameEngine.game;

import scalaAVGameEngine.game.events.GameEvent;
import scalaAVGameEngine.graphics.Screen;
import scalaAVGameEngine.sWorld.Room;
import scalaAVGameEngine.sWorld.RoomActor;
import scalaAVGameEngine.sWorld.World;
import akka.actor.ActorRef;



public interface Game {

	public ActorRef getPlayer();
	
	public Screen getCurrentScreen();

	public void resume();

	public void pause();
	
	public void stateChange(World world);
	
	public ActorRef getCurrentRoom();
	
	public void addToEventQueue(GameEvent event);
	
	public World getWorld();
	
	public void sayThought(String text);
	
	public void startConversation(ActorRef a);
	
	public void endConversation();
	
	public String nextLine();
	
	public String[] convoOptions();
	
	public boolean isConversing();
	
}
