package scalaAVGameEngine.world;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Player extends RActor {
	private Set<RoomObject> inventory;

	public Player(String label, int x, int y, int z, int sizeX, int sizeY,
			int sizeZ, Map<String, String> description, Set<String> actions,
			String currentAction, String state) {
		super(label, x, y, z, sizeX, sizeY, sizeZ, description, actions,
				currentAction, state);
		inventory = new HashSet<RoomObject>();
	}

	public void addToInventory(RoomObject item) {
		inventory.add(item);
	}

	public void removeFromInventory(RoomObject item) {
		inventory.remove(item);
	}

	public RoomObject removeFromInventory(String label) {
		if (label != null) {
			for (RoomObject item : inventory) {
				if (item != null && item.getLabel().equalsIgnoreCase(label)) {
					inventory.remove(item);
					return item;
				}
			}
		}
		return null;
	}
	
	public RoomObject popItemFromInventory(String label){
		if(label != null){
			for(RoomObject item : inventory){
				if(item != null && item.getLabel().equalsIgnoreCase(label)){
					return item;
				}
			}
		}
		return null;
	}

	public Set<RoomObject> getInventory() {
		return inventory;
	}

	public void setInventory(Set<RoomObject> inv) {
		inventory = inv;
	}
}
