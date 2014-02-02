package scalaAVGameEngine.world;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import android.util.Log;

public class RActor extends RoomObject {
	private Set<String> actions;
	private String currentAction;
	private Vector<int[]> route;
	private Map<String, Conversation> convos;
	private static final float SPLIT = 50000f;
	private float delta = 0;

	public enum Directions {
		LEFT, RIGHT, UP, DOWN
	}

	private Directions currentDirecton = Directions.DOWN;

	public RActor(String label, int x, int y, int z, int sizeX, int sizeY,
			int sizeZ, Map<String, String> description, Set<String> actions,
			String currentAction, String state) {
		super(label, x, y, z, sizeX, sizeY, sizeZ, description, state);
		this.actions = actions;
		this.currentAction = currentAction;
	}

	public RActor(String label, int x, int y, int z, int sizeX, int sizeY,
			int sizeZ, Map<String, String> description, Set<String> actions,
			String currentAction, String state, Map<String, Conversation> convos) {
		super(label, x, y, z, sizeX, sizeY, sizeZ, description, state);
		this.actions = actions;
		this.currentAction = currentAction;
		this.convos = convos;
	}

	public String getAction() {
		return currentAction;
	}

	public void setAction(String action) {
		if (actions.contains(action)) {
			currentAction = action;
		} else {
			System.out
					.println("Error: Action "
							+ action
							+ " not contained in possible action set, animation may not be available");
		}
	}

	/**
	 * Returns true if at end of route, returns false otherwise
	 * 
	 * @return
	 */
	public boolean step() {
		if (route == null || route.isEmpty())
			return true;
		int[] xyz = route.remove(route.size() - 1);
		if (xyz[0] > getX())
			setCurrentDirecton(Directions.RIGHT);
		else if (xyz[0] == getX() && xyz[1] > getY())
			setCurrentDirecton(Directions.DOWN);
		else
			setCurrentDirecton(Directions.LEFT);
		if (xyz != null) {
			setX(xyz[0]);
			setY(xyz[1]);
			setZ(xyz[2]);
			if (route.isEmpty())
				return true;
		}
		return false;
	}

	public int[] nextStep() {
		if (route != null && route.size() > 0)
			return route.get(route.size() - 1);
		else {
			int[] pos = { x, y, z };
			return pos;
		}
	}

	public void setRoute(Vector<int[]> route) {
		this.route = route;
	}

	public void setRoute(Room.Route route) {
		this.route = route.getCurrent();
		Collections.reverse(this.route);
	}

	public Directions getCurrentDirecton() {
		return currentDirecton;
	}

	public void setCurrentDirecton(Directions dir) {
		currentDirecton = dir;
	}

	public void turnRight() {
		switch (currentDirecton) {
		case LEFT:
			currentDirecton = Directions.UP;
		case RIGHT:
			currentDirecton = Directions.DOWN;
		case UP:
			currentDirecton = Directions.RIGHT;
		case DOWN:
			currentDirecton = Directions.LEFT;
		}
	}

	public void turnLeft() {
		switch (currentDirecton) {
		case LEFT:
			currentDirecton = Directions.DOWN;
		case RIGHT:
			currentDirecton = Directions.UP;
		case UP:
			currentDirecton = Directions.LEFT;
		case DOWN:
			currentDirecton = Directions.RIGHT;
		}
	}

	public void update(float time) {
		delta = delta + time;
		if (delta > SPLIT) {
			delta = 0;
			step();
		}
	}

	public Conversation getConvo() {
		return convos.get(state);
	}

	public boolean action() {
		return false;
	}

	public boolean isWalking() {
		if (route != null && route.size() > 0)
			return true;
		else
			return false;
	}

	public double[] getFudgedPosition() {
		double[] ret = { x, y, z };
		if (route != null && route.size() > 0) {
			int[] next = nextStep();
			double pcnt = delta / SPLIT;
			double diff = 0;
			for (int i = 0; i < ret.length; i++) {
				diff = next[i] - ret[i];
				ret[i] += diff * pcnt;
			}
		}
		return ret;
	}

}
