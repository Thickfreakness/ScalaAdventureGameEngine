package scalaAVGameEngine.world;

import java.util.List;

import scalaAVGameEngine.game.events.GameEvent;
import scalaAVGameEngine.triggers.Trigger;


public class ConvoObject {
	private String id;
	private List<String> convo;
	private int position = 0;
	private String effect = "";
	private Trigger trigger = null;

	public ConvoObject(String id, List<String> convo, String effect) {
		this.id = id;
		this.convo = convo;
		this.effect = effect;
	}
	
	public ConvoObject(String id, List<String> convo, String effect, Trigger trigger) {
		this.id = id;
		this.convo = convo;
		this.effect = effect;
		this.trigger = trigger;
	}

	public void begin(){
		position = 0;
	}
	
	public String nextLine() {
		if(position < convo.size())
			return convo.get(position);
		return "";
	}

	public String getActorID() {
		return id;
	}

	public boolean hasNext() {
		return (position+1 < convo.size());
	}
	
	public String getEffect(){
		return effect;
	}
	
	public void incrementConvo(){
		position++;
	}
	
	public void reset(){
		position = 0;
	}
	
	public Trigger getTrigger(){
		return trigger;
	}
}
