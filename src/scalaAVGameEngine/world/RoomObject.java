package scalaAVGameEngine.world;

import java.util.Map;

import scalaAVGameEngine.triggers.Trigger;


public class RoomObject {
	protected int x;
	protected int y;
	protected int z;
	protected int sizeX;
	protected int sizeY;
	protected int sizeZ;
	protected Map<String,String> description;
	protected String label;
	protected String state = "";
	private Trigger onUse = null;
	private Map<String,Trigger> onCombine = null;

	public RoomObject(String label, int x, int y, int z,
			int sizeX, int sizeY, int sizeZ, Map<String,String> description, String state){
		this.label = label;
		this.description = description;
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.state = state;
	}
	
	public RoomObject(String label, int x, int y, int z,
			int sizeX, int sizeY, int sizeZ, Map<String,String> description,Trigger onUse,
			Map<String,Trigger> onCombine, String state){
		this.label = label;
		this.description = description;
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.onUse = onUse;
		this.onCombine = onCombine;
		this.state = state;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getDescription() {
		return description.get(state);
	}

	public String getLabel() {
		return label;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public int getxSize() {
		return sizeX;
	}

	public void setxSize(int sizeX) {
		this.sizeX = sizeX;
	}

	public int getySize() {
		return sizeY;
	}

	public void setySize(int sizeY) {
		this.sizeY = sizeY;
	}
	
	public int getzSize(){
		return sizeZ;
	}
	
	public void setzSize(int sizeZ){
		this.sizeZ = sizeZ;
	}
	
	public void update(float time){
		
	}
	
	public boolean action(){
		if(onUse != null){
			onUse.triggerEvent();
			onUse = null; 
			return true;
		}else
			return false;
	}
	
	public boolean action(String label){
		for(String id : onCombine.keySet()){
			if(id.equalsIgnoreCase(label)){
				onCombine.get(id).triggerEvent();
				return true;
			}
		}
		return  false;
	}
	
	public void removeTrigger(String label){
		if(onCombine.get(label) != null)
			onCombine.remove(label);
	}
	
	public void addTrigger(String label, Trigger trigger){
		onCombine.put(label, trigger);
	}
	
	public String getState(){
		return state;
	}
	
	public void setState(String i){
		state = i;
	}
	
	public double[] getFudgedPosition(){
		double[] ret =  {x,y,z};
		return ret;
	}
	
}
