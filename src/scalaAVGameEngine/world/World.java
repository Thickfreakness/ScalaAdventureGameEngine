package scalaAVGameEngine.world;

public class World {
	private Room[] rooms;
	private Player player;
	private String activeRoom;
	public Room[] getRooms() {
		return rooms;
	}

	public void setRooms(Room[] rooms, Player player, String activeRoom) {
		this.rooms = rooms;
		this.player = player;
		this.activeRoom = activeRoom;
		
		for(Room room : rooms){
			if(activeRoom.equalsIgnoreCase(room.getId())){
				room.addToRoom(player);
				break;
			}
		}
	}

	public void update(float time){
		for(Room room : rooms){
			room.update(time);
		}
	}

	public Player getPlayer() {
		return player;
	}

	public String getActiveRoomID() {
		return activeRoom;
	}
	
	public Room getActiveRoom(){
		for(Room room : rooms){
			if(activeRoom.equalsIgnoreCase(room.getId())){
				return room;
			}
		}
		return null;
	}

	public void setActiveRoom(String activeRoom) {
		this.activeRoom = activeRoom;
	}
	
}
