package scalaAVGameEngine.assertions;

import scalaAVGameEngine.world.RoomObject;

public class ItemStateAssert implements AssertState {

	private RoomObject ob;
	private String state;
	public ItemStateAssert(RoomObject ob, String state){
		this.ob = ob;
		this.state = state;
	}
	
	public boolean isTrue() {
		if(ob.getState().equalsIgnoreCase(state)){
			return true;
		}else
			return false;
	}

}
