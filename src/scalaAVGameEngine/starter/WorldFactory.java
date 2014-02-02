package scalaAVGameEngine.starter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import scalaAVGameEngine.game.Game;
import scalaAVGameEngine.triggers.CombineTrigger;
import scalaAVGameEngine.triggers.ConvoEndTrigger;
import scalaAVGameEngine.triggers.MovePlayerTrigger;
import scalaAVGameEngine.triggers.MultiTrigger;
import scalaAVGameEngine.triggers.PickUpTrigger;
import scalaAVGameEngine.triggers.Trigger;
import scalaAVGameEngine.triggers.UseItemOnTrigger;
import scalaAVGameEngine.triggers.WorldChangeTrigger;
import scalaAVGameEngine.triggers.UseItemOnTrigger.USE_TYPES;
import scalaAVGameEngine.world.Conversation;
import scalaAVGameEngine.world.ConvoObject;
import scalaAVGameEngine.world.Player;
import scalaAVGameEngine.world.RActor;
import scalaAVGameEngine.world.Room;
import scalaAVGameEngine.world.RoomObject;
import scalaAVGameEngine.world.World;


public class WorldFactory {
	public static final int STATE_SUB = 6;

	public static World createWorld(String path, Game game) {
		World world = new World();
		Document worldDoc = parseXmlFile(path);
		NodeList nList = worldDoc.getElementsByTagName("Room");
		Stack<Room> rooms = new Stack<Room>();
		Player player = null;
		String activeRoom = "";
		if (nList != null && nList.getLength() > 0) {
			for (int i = 0; i < nList.getLength(); i++) {
				rooms.add(createRoom(nList.item(i).getTextContent(), game));
			}
		}
		Room[] roomA = new Room[rooms.size()];
		for (int i = 0; i < roomA.length; i++) {
			roomA[i] = rooms.pop();
		}

		nList = worldDoc.getElementsByTagName("player");
		if (nList != null && nList.getLength() > 0) {
			for (int i = 0; i < nList.getLength(); i++) {
				if (nList.item(i).getNodeName().equalsIgnoreCase("player")) {
					player = createPlayer(nList.item(i));
				}
			}
		}

		nList = worldDoc.getElementsByTagName("activeRoom");
		if (nList != null && nList.getLength() > 0) {
			for (int i = 0; i < nList.getLength(); i++) {
				if (nList.item(i).getNodeName().equalsIgnoreCase("activeRoom")) {
					activeRoom = nList.item(i).getTextContent();
				}
			}
		}

		world.setRooms(roomA, player, activeRoom);
		return world;
	}

	public static Room createRoom(String path, Game game) {
		Document roomDoc = parseXmlFile(path);
		Stack<RActor> actors = null;
		Stack<RoomObject> items = null;
		String[] walkable = new String[2];
		String id = "";
		String walkableStart = "";
		NodeList nList = roomDoc.getElementsByTagName("Actor");
		if (nList != null && nList.getLength() > 0) {
			actors = new Stack<RActor>();
			for (int i = 0; i < nList.getLength(); i++) {
				actors.add(createActor(nList.item(i), game));
			}
		}

		nList = roomDoc.getElementsByTagName("Object");
		if (nList != null && nList.getLength() > 0) {
			items = new Stack<RoomObject>();
			for (int i = 0; i < nList.getLength(); i++) {
				items.add(createObject(nList.item(i), game));
			}
		}

		nList = roomDoc.getElementsByTagName("walkableArea");
		if (nList != null && nList.getLength() > 0) {
			for (int i = 0; i < nList.getLength(); i++) {
				if (nList.item(i).getNodeName()
						.equalsIgnoreCase("walkableArea")) {
					walkable = nList.item(i).getTextContent().split(",");
				}
			}
		}

		nList = roomDoc.getElementsByTagName("walkableStart");
		if (nList != null && nList.getLength() > 0) {
			for (int i = 0; i < nList.getLength(); i++) {
				if (nList.item(i).getNodeName()
						.equalsIgnoreCase("walkableStart")) {
					walkableStart = nList.item(i).getTextContent();
				}
			}
		}

		nList = roomDoc.getElementsByTagName("roomName");
		if (nList != null && nList.getLength() > 0) {
			for (int i = 0; i < nList.getLength(); i++) {
				if (nList.item(i).getNodeName().equalsIgnoreCase("roomName")) {
					id = nList.item(i).getTextContent();
				}
			}
		}
		int[] vPoint = new int[2];
		String sVPoint = getContentFor("v_point",
				roomDoc.getChildNodes().item(0).getChildNodes());
		vPoint[0] = Integer.parseInt(sVPoint.split(",")[0]);
		vPoint[1] = Integer.parseInt(sVPoint.split(",")[1]);

		Room room = new Room(id, new HashSet(actors), new HashSet(items),
				Integer.parseInt(walkable[0]), Integer.parseInt(walkable[1]),
				Integer.parseInt(walkable[2]), parseXYZ(walkableStart));
		room.setVPoint(vPoint);

		return room;
	}

	public static RoomObject createObject(Node item, Game game) {
		String label = "";
		Map<String, String> description = new HashMap<String, String>();
		int[] xyz = new int[3];
		int[] size = new int[3];
		Document objectDoc = null;
		NodeList children = item.getChildNodes();
		String title = "";
		Trigger onuse = null;
		HashMap<String, Trigger> triggers = new HashMap<String, Trigger>();
		String startState = "";
		for (int i = 0; i < children.getLength(); i++) {
			title = children.item(i).getNodeName();
			if (title.equalsIgnoreCase("id"))
				label = children.item(i).getTextContent();
			else if (title.equalsIgnoreCase("position"))
				xyz = parseXYZ(children.item(i).getTextContent());
			else if (title.equalsIgnoreCase("xml"))
				objectDoc = parseXmlFile(children.item(i).getTextContent());
			else if (title.equalsIgnoreCase("description"))
				description.put("***", children.item(i).getTextContent());
			else if (title.equalsIgnoreCase("startState"))
				startState = children.item(i).getTextContent();
		}

		NodeList states = objectDoc.getChildNodes().item(0).getChildNodes(); // SHITE
		// WAY
		// OF
		// DOING
		// IT!
		for (int a = 0; a < states.getLength(); a++) {
			if (states.item(a).getNodeType() == Node.ELEMENT_NODE) {
				String state = states.item(a).getNodeName()
						.substring(STATE_SUB);
				children = states.item(a).getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					title = children.item(i).getNodeName();
					if (title.equalsIgnoreCase("description"))
						description.put(state, children.item(i)
								.getTextContent());
					else if (title.equalsIgnoreCase("size"))
						size = parseXYZ(children.item(i).getTextContent());
					else if (title.equalsIgnoreCase("on_use")) {
						onuse = createTrigger(children.item(i).getChildNodes()
								.item(1), game); // UPDATE SHITE
					} else if (title.equalsIgnoreCase("on_combine")) {
						for (int j = 0; j < children.item(i).getChildNodes()
								.getLength(); j++) {
							if (children.item(i).getChildNodes().item(j)
									.getNodeType() == Node.ELEMENT_NODE) {
								Trigger trig = createTrigger(children.item(i)
										.getChildNodes().item(j), game);
								String combiner = "";
								if (trig instanceof UseItemOnTrigger)
									combiner = ((UseItemOnTrigger) trig)
											.getEffector();
								else if (trig instanceof MultiTrigger) {
									for (Trigger t1 : ((MultiTrigger) trig)
											.getTriggers()) {
										if (t1 instanceof UseItemOnTrigger)
											combiner = ((UseItemOnTrigger) t1)
													.getEffector();
									}
								}
								if (!combiner.equals(""))
									triggers.put(combiner, trig);
							}
						}
					}
				}
			}
		}
		RoomObject it = null;
		if (onuse != null || triggers != null)
			it = new RoomObject(label, xyz[0], xyz[1], xyz[2], size[0],
					size[1], size[2], description, onuse, triggers, startState);
		else
			it = new RoomObject(label, xyz[0], xyz[1], xyz[2], size[0],
					size[1], size[2], description, startState);
		return it;
	}

	public static RActor createActor(Node item, Game game) {
		String label = "";
		String startState = "";
		Map<String, String> description = new HashMap<String, String>();
		int[] xyz = new int[3];
		int[] size = new int[3];
		String[] actionsA = new String[0];
		String currentAction = "";
		NodeList children = item.getChildNodes();
		String title = "";
		Document actorDoc = null;
		Set actions = new HashSet<String>();
		Map<String,Conversation> convos = new HashMap<String, Conversation>();
		
		for (int i = 0; i < children.getLength(); i++) {
			title = children.item(i).getNodeName();
			if (title.equalsIgnoreCase("id"))
				label = children.item(i).getTextContent();
			if (title.equalsIgnoreCase("action"))
				currentAction = children.item(i).getTextContent();
			if (title.equalsIgnoreCase("position"))
				xyz = parseXYZ(children.item(i).getTextContent());
			if (title.equalsIgnoreCase("xml"))
				actorDoc = parseXmlFile(children.item(i).getTextContent());
			else if (title.equalsIgnoreCase("startState"))
				startState = children.item(i).getTextContent();
		}

		NodeList states = actorDoc.getElementsByTagName("Actor").item(0)
				.getChildNodes();

		for (int a = 0; a < states.getLength(); a++) {
			if (states.item(a).getNodeType() == Node.ELEMENT_NODE) {
				String state = states.item(a).getNodeName()
						.substring(STATE_SUB);
				children = states.item(a).getChildNodes();

				for (int i = 0; i < children.getLength(); i++) {
					title = children.item(i).getNodeName();
					if (title.equalsIgnoreCase("Actions"))
						actionsA = children.item(i).getTextContent().split(",");
					if (title.equalsIgnoreCase("size"))
						size = parseXYZ(children.item(i).getTextContent());
					if (title.equalsIgnoreCase("description"))
						description.put(state, children.item(i)
								.getTextContent());
					if(title.equalsIgnoreCase("conversation"))
						convos.put(state, createConversation(children.item(i).getTextContent(),game));
				}

				for (String act : actionsA) {
					actions.add(act);
				}
			}
		}

		RActor actor = new RActor(label, xyz[0], xyz[1], xyz[2], size[0],
				size[1], size[2], description, actions, currentAction,
				startState,convos);
		return actor;
	}

	public static Player createPlayer(Node item) {
		String label = "";
		String startState = "";
		Map<String, String> description = new HashMap<String, String>();
		int[] xyz = new int[3];
		int[] size = new int[3];
		String[] actionsA = new String[0];
		String currentAction = "";
		String title = "";
		Document actorDoc = null;
		Set actions = new HashSet<String>();
		NodeList children = item.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			title = children.item(i).getNodeName();
			if (title.equalsIgnoreCase("id"))
				label = children.item(i).getTextContent();
			if (title.equalsIgnoreCase("action"))
				currentAction = children.item(i).getTextContent();
			if (title.equalsIgnoreCase("position"))
				xyz = parseXYZ(children.item(i).getTextContent());
			if (title.equalsIgnoreCase("xml"))
				actorDoc = parseXmlFile(children.item(i).getTextContent());
			else if (title.equalsIgnoreCase("startState"))
				startState = children.item(i).getTextContent();
			// if (title.equalsIgnoreCase("description"))
			// description.put(state, children.item(i).getTextContent());
		}

		NodeList states = actorDoc.getElementsByTagName("Actor").item(0)
				.getChildNodes();

		for (int a = 0; a < states.getLength(); a++) {
			if (states.item(a).getNodeType() == Node.ELEMENT_NODE) {
				String state = states.item(a).getNodeName()
						.substring(STATE_SUB);
				children = states.item(a).getChildNodes();

				for (int i = 0; i < children.getLength(); i++) {
					title = children.item(i).getNodeName();
					if (title.equalsIgnoreCase("Actions"))
						actionsA = children.item(i).getTextContent().split(",");
					if (title.equalsIgnoreCase("size"))
						size = parseXYZ(children.item(i).getTextContent());
					if (title.equalsIgnoreCase("description"))
						description.put(state, children.item(i)
								.getTextContent());
				}

				for (String act : actionsA) {
					actions.add(act);
				}
			}
		}
		Player player = new Player(label, xyz[0], xyz[1], xyz[2], size[0],
				size[1], size[2], description, actions, currentAction,
				startState);
		return player;
	}

	public static Trigger createTrigger(Node item, Game game) {
		Trigger trig = null;
		String id = item.getNodeName().substring(8);
		NodeList children = item.getChildNodes();
		if (id.equalsIgnoreCase("combine")) {
			trig = new CombineTrigger(game, getContentFor("item1", children),
					getContentFor("item2", children), getContentFor("result",
							children));
		} else if (id.equalsIgnoreCase("movePlayer")) {
			trig = new MovePlayerTrigger(game, getContentFor("destination",
					children));
		} else if (id.equalsIgnoreCase("multi")) {
			LinkedList<Trigger> triggers = new LinkedList<Trigger>();
			for (int i = 0; i < children.getLength(); i++) {
				if (children.item(i).getNodeName().startsWith("trigger_")) {
					triggers.add(createTrigger(children.item(i), game));
				}
			}
			trig = new MultiTrigger(game, getContentFor("id", children),
					triggers);
		} else if (id.equalsIgnoreCase("pickUp")) {
			trig = new PickUpTrigger(game, getContentFor("item", children),
					getContentFor("thought", children));
		} else if( id.equalsIgnoreCase("ConvoEnd")){
			trig = new ConvoEndTrigger(game, "");
		} else if (id.equalsIgnoreCase("UseItemOn")) {
			UseItemOnTrigger.USE_TYPES type = USE_TYPES.OBJECT_PICKUP;
			String sType = getContentFor("type", children);
			String newState = "";
			if (sType.equalsIgnoreCase("STATE_SET")) {
				type = USE_TYPES.STATE_SET;
				newState = getContentFor("new_state", children);
			} else if (sType.equalsIgnoreCase("OBJECT_REMOVE"))
				type = USE_TYPES.OBJECT_REMOVE;
			else if (sType.equalsIgnoreCase("OBJECT_PICKUP"))
				type = USE_TYPES.OBJECT_PICKUP;
			else if (sType.equalsIgnoreCase("OBJECT_PICKUP_AND_DISPOSE"))
				type = USE_TYPES.OBJECT_PICKUP_AND_DISPOSE;
			else if (sType.equalsIgnoreCase("STATE_SET_AND_DISPOSE")) {
				type = USE_TYPES.STATE_SET_AND_DISPOSE;
				newState = getContentFor("new_state", children);
			}

			if (newState.equals("")) {
				trig = new UseItemOnTrigger(game, getContentFor("item",
						children), getContentFor("effector", children), type);
			} else {
				trig = new UseItemOnTrigger(game, getContentFor("item",
						children), getContentFor("effector", children), type,
						newState);
			}
		} else if (id.equalsIgnoreCase("worldChange")) {
			trig = new WorldChangeTrigger(game, getContentFor("path", children));
		}

		return trig;
	}

	public static String getContentFor(String id, NodeList children) {
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equalsIgnoreCase(id))
				return children.item(i).getTextContent();
		}
		return "";
	}

	public static int[] parseXYZ(String xyz) {
		String[] array = xyz.split(",");
		int[] returner = new int[3];
		returner[0] = Integer.parseInt(array[0].trim());
		returner[1] = Integer.parseInt(array[1].trim());
		returner[2] = Integer.parseInt(array[2].trim());

		return returner;
	}

	public static int[] parseXY(String xy) {
		String[] array = xy.split(",");
		int[] returner = new int[2];
		returner[0] = Integer.parseInt(array[0].trim());
		returner[1] = Integer.parseInt(array[1].trim());

		return returner;
	}

	public static Document parseXmlFile(String xml) {
		// get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;
		try {

			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			dom = db.parse(GameActivity.myContext.getAssets().open(xml));
			// dom = db.parse(xml);
			if (dom != null)
				return dom;

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return null;
	}

	public static List<ConvoObject> createConvoList(Node node, Game game) {
		ArrayList<ConvoObject> list = new ArrayList<ConvoObject>();
		NodeList children = node.getChildNodes();

		String effect = "";
		String id = "";
		ArrayList<String> lines = new ArrayList<String>();
		Trigger trig = null;
		
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				String type = children.item(i).getNodeName();
				if (type.equalsIgnoreCase("effect"))
					effect = children.item(i).getTextContent();
				else if (type.equalsIgnoreCase(id)) {
					lines.add(children.item(i).getTextContent());
				} else if (id.equals("")) {
					id = type;
					lines.add(children.item(i).getTextContent());
				} else if (type.contains("trigger")){
					trig = createTrigger(children.item(i), game);
				}else {
					list.add(new ConvoObject(id, lines, effect,trig));
					id = type;
					lines = new ArrayList<String>();
					lines.add(children.item(i).getTextContent());
				}
			}
		}
		list.add(new ConvoObject(id, lines, effect,trig));
		return list;
	}

	public static Conversation createConversation(Node node, Game game) {
		String act1 = getContentFor("ActorA", node.getChildNodes());
		String act2 = getContentFor("ActorB", node.getChildNodes());
		NodeList list = node.getChildNodes();
		List<ConvoObject> intro = null;
		Map<String,String> options = null;
		Map<String,List<ConvoObject>> bulk = new HashMap<String,List<ConvoObject>>();
		
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				if (list.item(i).getNodeName().equalsIgnoreCase("intro")) {
					intro = createConvoList(list.item(i),game);
				} else if(list.item(i).getNodeName().equalsIgnoreCase("options")){
					options = createOptions(list.item(i));
				} else if(list.item(i).getNodeName().equalsIgnoreCase("bulk")){
					NodeList bulkNodes = list.item(i).getChildNodes();
					for(int j = 0 ; j < bulkNodes.getLength() ; j++){
						if(bulkNodes.item(j).getNodeType() == Node.ELEMENT_NODE){
							bulk.put(options.get(bulkNodes.item(j).getNodeName()),
									createConvoList(bulkNodes.item(j),game));
						}
					}
				}
			}
		}

		return new Conversation(intro, bulk, act1, act2, game);
	}
	
	private static Map<String,String> createOptions(Node node){
		HashMap<String,String> options = new HashMap<String,String>();
		NodeList children = node.getChildNodes();

		for(int i = 0 ; i < children.getLength() ; i++){
			if(children.item(i).getNodeType() == Node.ELEMENT_NODE){
				options.put(children.item(i).getNodeName(), children.item(i).getTextContent());
			}
		}
		
		return options;
	}

	public static Conversation createConversation(String path, Game game) {
		Document doc = parseXmlFile(path);
		return createConversation(doc.getChildNodes().item(0), game);
	}

	public static RActor createActor(String path, Game game) {
		Document doc = parseXmlFile(path);
		return createActor(doc.getChildNodes().item(0), game);
	}

	public static Player createPlayer(String path) {
		Document doc = parseXmlFile(path);
		return createPlayer(doc.getChildNodes().item(0));
	}

	public static RoomObject createObject(String path, Game game) {
		Document doc = parseXmlFile(path);
		return createObject(doc.getChildNodes().item(0), game);
	}

}
