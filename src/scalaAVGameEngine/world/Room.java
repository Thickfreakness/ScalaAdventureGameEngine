package scalaAVGameEngine.world;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.util.Log;

public class Room {
	private volatile Map<String, RoomObject> objects = new Hashtable<String, RoomObject>(); // ID
	// =>
	// Object
	private boolean[][][] walkable; // X,Y,Z walkable grid - Z in discrete
									// layers, x,y continuous (ish)
	private String id;
	public static final int[] grid = { 214, 120, 10 }; // World dimensions
	private int[] vPoint = { 0, 0 };
	public static final int[] DIMENSIONS = { 214, 120, 10 };

	public Room(String id, Set<RoomObject> actors, /* Actor player, */
			Set<RoomObject> items, int walkX, int walkY, int walkZ,
			int[] walkableStart) {
		for (RoomObject actor : actors)
			objects.put(actor.getLabel(), actor);
		for (RoomObject item : items)
			objects.put(item.getLabel(), item);

		this.id = id;
		walkable = new boolean[walkX + walkableStart[0]][walkY
				+ walkableStart[1]][walkZ + walkableStart[2]]; // Bump by start
																// values
		initWalk(walkableStart);
	}

	/**
	 * Fix possible null when checking grid is null
	 * 
	 * @param ob
	 * @return
	 */
	public boolean addToRoom(RoomObject ob) {
		if (inWalkArea(ob.getX(), ob.getY(), ob.getZ())
				&& walkable[ob.getX()][ob.getY()][ob.getZ()]) {
			setWalk(ob.getX(), ob.getY(), ob.getZ(), ob.getxSize(),
					ob.getySize(), ob.getzSize(), false);
			objects.put(ob.getLabel(), ob);
			return true;
		}
		return false;
	}

	private void initWalk(int[] bump) {
		int[] xyz = new int[3];
		RoomObject ob = null;
		for (int i = bump[0]; i < walkable.length; i++) {
			for (int j = bump[1]; j < walkable[i].length; j++) {
				for (int k = bump[2]; k < walkable[i][j].length; k++) {
					walkable[i][j][k] = true;
				}
			}
		}
		for (String key : objects.keySet()) {
			ob = objects.get(key);
			xyz[0] = ob.getX();
			xyz[1] = ob.getY();
			xyz[2] = ob.getZ();

			if (inWalkArea(xyz[0], xyz[1], xyz[2])) {
				setWalk(ob.getX(), ob.getY(), ob.getZ(), ob.getxSize(),
						ob.getySize(), ob.getzSize(), false);
			}
		}
	}

	public RoomObject getAtXYZ(int[] xyz) {
		RoomObject ob = null;
		int[] ijk = new int[3];
		for (String key : objects.keySet()) {
			ob = objects.get(key);
			if (ijk[0] <= xyz[0] && xyz[0] <= ijk[0] + ob.getxSize()
					&& ijk[1] <= xyz[1] && xyz[1] <= ijk[1] + ob.getySize()
					&& ijk[2] <= xyz[2] && xyz[2] <= ijk[2] + ob.getzSize()) {
				return ob;
			}
		}
		return null;
	}

	public RoomObject getAtXY(int x, int y) {
		RoomObject ob = null;
		for (String key : objects.keySet()) {
			ob = objects.get(key);
			if (ob.getX() <= x && x <= ob.getX() + ob.getxSize()
					&& ob.getY() <= y && y <= ob.getY() + ob.getySize()) {
				return ob;
			}
		}
		return null;
	}

	private void setWalk(int i, int j, int k, int sizeI, int sizeJ, int sizeK,
			boolean flag) {
		for (int x = i; x < sizeI; x++) {
			for (int y = j; y < sizeJ; y++) {
				for (int z = k; z < sizeK; z++) {
					if (inWalkArea(x, y, z)) {
						walkable[x][y][z] = flag;
					}
				}
			}
		}
	}

	/**
	 * @TODO DOESN'T IMPLIMENT SIZE
	 * @param x
	 * @param y
	 * @param z
	 * @param label
	 * @return
	 */
	public boolean moveObject(int x, int y, int z, String label) {
		RoomObject ob = null;
		for (String key : objects.keySet()) {
			ob = objects.get(key);
			if (ob.getLabel().equalsIgnoreCase(label)) {
				if (inWalkArea(x, y, z) && walkable[x][y][z] == true) {
					walkable[ob.getX()][ob.getY()][ob.getZ()] = true;
					ob.setX(x);
					ob.setY(y);
					ob.setZ(z);
					walkable[x][y][z] = false;
					return true;
				}
				break;
			}
		}
		return false;
	}

	/**
	 * A check to avoid null pointers
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private boolean inWalkArea(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0 && walkable.length > x
				&& walkable[x].length > y && walkable[x][y].length > z) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if route available, false otherwise
	 * 
	 * @param actor
	 * @param x
	 * @param z
	 * @return
	 */
	public boolean setActorRouteTo(String label, int x, int y, int z) {
		int[] end = { x, y, z };
		int[] start = new int[3];
		if (objects.get(label) != null && objects.get(label) instanceof RActor) {
			RActor actor = (RActor) objects.get(label);
			if (!inWalkArea(x, y, z) || !walkable[x][y][z])
				end = getNearPoint(x, y, z);
			if (end == null)
				return false;
			Route route;
			start[0] = actor.getX();
			start[1] = actor.getY();
			start[2] = actor.getZ();
			route = getAStarRoute(start, end);

			actor.setRoute(route);
			return true;
		}
		return false;
	}

	private ConcurrentLinkedQueue<int[]> getRoute(int[] start, int[] end) {
		ConcurrentLinkedQueue<int[]> route = new ConcurrentLinkedQueue<int[]>();
		route.add(start);
		int[] next = start.clone();
		do {
			next = getBestNext(route.size(), next, end, route);
			route.add(next);
		} while (!comparePoints(next, end));
		return route;
	}

	private Route getAStarRoute(int[] start, int[] end) {
		Route theRoute = new Route(start, getEstCost(start, end));
		Map<String, Route> closedSet = new HashMap<String, Route>();
		Map<String, Route> openSet = new HashMap<String, Route>();
		openSet.put(pointToString(start),
				new Route(start, getEstCost(start, end)));
		while (!openSet.isEmpty()) {
			theRoute = getLowestFScore(openSet, end);
			int[] currPoint = theRoute.getCurrentEndPoint();
			if (currPoint[0] == end[0] && currPoint[1] == end[1]
					&& currPoint[2] == end[2])
				return theRoute;
			openSet.remove(pointToString(theRoute.getCurrentEndPoint()));
			closedSet.put(pointToString(theRoute.getCurrentEndPoint()),
					theRoute);

			Map<String, Route> neighbours = getNeighbours(theRoute, end);
			for (String point : neighbours.keySet()) {
				double tent_g = theRoute.getCurrentCost() + 1;
				if (closedSet.containsKey(point)
						&& tent_g >= closedSet.get(point).getCurrentCost())
					continue;
				if (!openSet.containsKey(point)
						|| (closedSet.containsKey(point) && tent_g < closedSet
								.get(point).getCurrentCost())) {
					openSet.put(point, neighbours.get(point));
				}
			}

		}
		return theRoute;
	}

	private String pointToString(int[] point) {
		String returner = "";
		returner += point[0] + ";";
		returner += point[1] + ";";
		returner += point[2] + ";";
		return returner;
	}

	private int[] stringToPoint(String key) {
		String[] temp = key.split(";");
		int[] returner = new int[3];
		returner[0] = Integer.parseInt(temp[0]);
		returner[1] = Integer.parseInt(temp[1]);
		returner[2] = Integer.parseInt(temp[2]);
		return returner;
	}

	private Route getLowestFScore(Map<String, Route> set, int[] end) {
		Route low = null;
		double lowF = -1;
		// for (Route point : set) {
		for (String key : set.keySet()) {
			Route point = set.get(key);
			if (low == null) {
				low = point;
				lowF = point.getCurrentCost()
						+ getEstCost(point.getCurrentEndPoint(), end);
				continue;
			}
			double tmp = point.getCurrentCost()
					+ getEstCost(point.getCurrentEndPoint(), end);
			if (tmp < lowF) {
				low = point;
				lowF = tmp;
			}
		}
		return low;
	}

	private Map<String, Route> getNeighbours(Route currentRoute, int[] end) {
		int[] point = currentRoute.getCurrentEndPoint();
		Map<String, Route> neighbours = new HashMap<String, Route>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					// Check not adding start point
					if (i == 0 && j == 0 && k == 0)
						continue;
					if (inWalkArea(point[0] + i, point[1] + j, point[2] + k)
							&& walkable[point[0] + i][point[1] + j][point[2]
									+ k]) {
						int[] tmp = new int[3];
						tmp[0] = point[0] + i;
						tmp[1] = point[1] + j;
						tmp[2] = point[2] + k;
						if (!currentRoute.inRoute(tmp))
							neighbours.put(pointToString(tmp), new Route(
									currentRoute, tmp, getEstCost(tmp, end)));
					}
				}
			}
		}
		return neighbours;
	}

	private boolean comparePoints(int[] a, int[] b) {
		if (a[0] == b[0] && a[1] == b[1])
			return true;
		else
			return false;
	}

	private double getEstCost(int[] start, int[] end) {
		int diffX = end[0] - start[0];
		int diffY = end[1] - start[1];
		int diffZ = end[2] - start[2];
		// return Math.abs(diffX) + Math.abs(diffY) + Math.abs(diffZ);
		return Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2)
				+ Math.pow(diffZ, 2));
	}

	private int[] getBestNext(int currentCost, int[] current, int[] end,
			ConcurrentLinkedQueue<int[]> currentRoute) {
		// int[] xz = new int[2];
		// int total = walkable.length;
		// int[] best = new int[2];
		// int estimate = 0;
		// for (int x = -1; x < 2; x++) {
		// for (int z = -1; z < 2; z++) {
		// xz[0] = x + current[0];
		// xz[1] = z + current[1];
		// if (!currentRoute.contains(xz) && inWalkArea(xz[0], xz[1])
		// && walkable[xz[0]][xz[1]]) {
		// estimate = getEstCost(xz, end) + currentCost;
		// if (estimate < total) {
		// best = xz;
		// total = estimate;
		// }
		// }
		// }
		// }
		int[] best = current.clone();

		if (current[0] > end[0])
			best[0]--;
		else if (current[0] < end[0])
			best[0]++;

		if (current[1] > end[1])
			best[1]--;
		else if (current[1] < end[1])
			best[1]++;

		if (current[2] > end[2])
			best[2]--;
		else if (current[2] < end[2])
			best[2]++;

		return best;
	}

	private int[] getNearPoint(int x, int y, int z) {
		int[] point = { x, y, z };
		if (inWalkArea(x, y, z) && getAtXYZ(point) != null) {
			x = getAtXYZ(point).getX();
			y = getAtXYZ(point).getY();
			z = getAtXYZ(point).getZ();

			x = x - 2;
			return getNearPoint(x, y, z);
		} else if(!inWalkArea(x, y, z)){
			if (x > walkable.length) {
				x = walkable.length - 1;
			}
			if (y > walkable[walkable.length -1].length) {
				y = walkable[walkable.length -1].length - 1;
			}
			return getNearPoint(x, y, z);
		}
		return point;
	}

	public String getId() {
		return id;
	}

	public Map<String, RoomObject> getObjects() {
		return objects;
	}

	public void update(float time) {
		synchronized (objects) {
			for (String key : objects.keySet()) {
				objects.get(key).update(time);
			}
		}
	}

	public boolean[][][] getWalkable() {
		return walkable;
	}

	public RoomObject getItem(String label) {
		return objects.get(label);
	}

	public RoomObject removeObject(String label) {
		if (objects.get(label) != null) {
			RoomObject ob = objects.get(label);
			setWalk(ob.getX(), ob.getY(), ob.getZ(), ob.getxSize(),
					ob.getySize(), ob.getzSize(), true);
			objects.remove(label);
			return ob;
		}
		return null;
	}

	public int[] getGrid() {
		return grid;
	}

	public void setVPoint(int[] vPoint) {
		this.vPoint = vPoint;
	}

	public int[] getvPoint() {
		return vPoint;
	}

	public class Route {
		Vector<int[]> current = new Vector<int[]>();
		double estimatedCost = -1;

		public Route(Vector<int[]> current, double estimatedCost) {
			this.current = current;
			this.estimatedCost = estimatedCost;
		}

		public Route(int[] begin, double estimate) {
			current.add(begin);
			estimatedCost = estimate;
		}

		public Route(Route old, int[] newPoint, double estimate) {
			try {
				current = new Vector<int[]>(old.current);
				estimatedCost = estimate;
				current.add(newPoint);
			} catch (Exception e) {
				Log.d("Clone Route",
						"Clone prob not supported: " + e.getStackTrace());
			}

		}

		public Vector<int[]> getCurrent() {
			return current;
		}

		public void setCurrent(Vector<int[]> current) {
			this.current = current;
		}

		public int getCurrentCost() {
			return current.size();
		}

		public double getEstimatedCost() {
			return estimatedCost;
		}

		public void setEstimatedCost(double estimatedCost) {
			this.estimatedCost = estimatedCost;
		}

		public double getTotalCost() {
			return getCurrentCost() + estimatedCost;
		}

		public int[] getCurrentEndPoint() {
			if (current.size() > 0)
				return current.get(current.size() - 1);
			else
				return null;
		}

		public void addToRoute(int[] newPoint) {
			current.add(newPoint);
		}

		public boolean inRoute(int[] point) {
			for (int[] step : current) {
				if (step[0] == point[0] && step[1] == point[1]
						&& step[2] == point[2])
					return true;
			}
			return false;
		}
	}

}
