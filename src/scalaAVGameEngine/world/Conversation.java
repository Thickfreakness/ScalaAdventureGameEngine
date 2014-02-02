package scalaAVGameEngine.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import scalaAVGameEngine.game.Game;


public class Conversation {
	private List<ConvoObject> openingConvo;
	private Map<String, List<ConvoObject>> convoMapping;
	private String[] options;
	private String currentChooser;
	private int current = 0;
	private int openLine = 0;
	private String act1 = "";
	private String act2 = "";
	private Game game = null;

	public Conversation(List<ConvoObject> open,
			Map<String, List<ConvoObject>> map, Game game) {
		openingConvo = open;
		convoMapping = map;
		options = new String[convoMapping.keySet().size()];
		int i = 0;
		for (Object o : convoMapping.keySet()) {
			options[i++] = o.toString();
		}
		this.game = game;
	}

	public Conversation(List<ConvoObject> open,
			Map<String, List<ConvoObject>> map, String act1, String act2,
			Game game) {
		openingConvo = open;
		convoMapping = map;
		this.act1 = act1;
		this.act2 = act2;
		options = new String[convoMapping.keySet().size()];
		int i = 0;
		for (Object o : convoMapping.keySet()) {
			options[i++] = o.toString();
		}
		this.game = game;
	}

	public Conversation(Map<String, List<ConvoObject>> map, Game game) {
		convoMapping = map;
		openingConvo = new ArrayList<ConvoObject>();
		openLine = 1;
		options = new String[convoMapping.keySet().size()];
		int i = 0;
		for (Object o : convoMapping.keySet()) {
			options[i++] = o.toString();
		}
		this.game = game;
	}

	public String nextLine() {
		if (openLine < openingConvo.size())
			return openingConvo.get(openLine).nextLine();
		else if (convoMapping.get(currentChooser) != null)
			return convoMapping.get(currentChooser).get(current).nextLine();
		else
			return null;
	}

	public void incrementConvo() {
		if (openLine < openingConvo.size()) {
			if (openingConvo.get(openLine).hasNext())
				openingConvo.get(openLine).incrementConvo();
			else {
				if (openingConvo.get(openLine).getTrigger() != null)
					openingConvo.get(openLine).getTrigger().triggerEvent();
				openingConvo.get(openLine).reset();
				openLine++;
			}
		} else if (convoMapping.get(currentChooser) != null)
			if (convoMapping.get(currentChooser).size() > (current + 1)) {
				if (convoMapping.get(currentChooser).get(current).hasNext())
					convoMapping.get(currentChooser).get(current)
							.incrementConvo();
				else {
					if (convoMapping.get(currentChooser).get(current)
							.getTrigger() != null)
						convoMapping.get(currentChooser).get(current)
								.getTrigger().triggerEvent();
					convoMapping.get(currentChooser).get(current).reset();
					current++;
				}
			} else {
				if (convoMapping.get(currentChooser).get(current)
						.getTrigger() != null)
					convoMapping.get(currentChooser).get(current)
							.getTrigger().triggerEvent();
				currentChooser = null;
			}
		else
			currentChooser = null;
	}

	public String whosTalking() {
		if (openLine < openingConvo.size())
			return openingConvo.get(openLine).getActorID();
		else if (convoMapping.get(currentChooser) != null
				&& current < convoMapping.get(currentChooser).size())
			return convoMapping.get(currentChooser).get(current).getActorID();
		else {
			return null;
		}
	}

	public boolean hasNext() {
		if (openLine < openingConvo.size())
			return true;
		else if (currentChooser != null && !currentChooser.equals("")
				&& convoMapping.get(currentChooser) != null
				&& (current + 1) < convoMapping.get(currentChooser).size()
				&& convoMapping.get(currentChooser).get(current) != null
				&& convoMapping.get(currentChooser).get(current).hasNext()) {
			return true;
		}
		return false;
	}

	public String[] getOptionsAndReset() {
		current = 0;
		currentChooser = "";
		return options;
	}

	public void chooseConvo(String id) {
		if (convoMapping.containsKey(id)) {
			currentChooser = id;
			convoMapping.get(currentChooser).get(current).begin();
		}
	}

	public void chooseConvo(int i) {
		if (i < options.length) {
			currentChooser = options[i];
			convoMapping.get(currentChooser).get(current).begin();
		}
	}

	public String getCurrentEffect() {
		if (openLine < openingConvo.size())
			return openingConvo.get(openLine).getEffect();
		else if (convoMapping.get(currentChooser) != null)
			return convoMapping.get(currentChooser).get(current).getEffect();
		else
			return null;
	}

	public String getAct1() {
		return act1;
	}

	public String getAct2() {
		return act2;
	}

	public void reset() {
		openLine = 0;
		current = 0;
		currentChooser = null;
		for (String key : convoMapping.keySet()) {
			for (int i = 0; i < convoMapping.get(key).size(); i++)
				convoMapping.get(key).get(i).reset();
		}
	}
}
