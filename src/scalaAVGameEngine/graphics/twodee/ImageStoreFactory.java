package scalaAVGameEngine.graphics.twodee;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import scalaAVGameEngine.graphics.Graphics;
import scalaAVGameEngine.graphics.Graphics.PixmapFormat;
import scalaAVGameEngine.starter.WorldFactory;


public class ImageStoreFactory {

	private static Map<String, TextureHolder> store = new HashMap<String, TextureHolder>();

	public static ImageStore buildImageStore(String room, Graphics g) {
		buildFromFile(room, g);
		return new ImageStore(store);
	}

	private static void buildFromFile(String path, Graphics g) {
		Document imageDoc = WorldFactory.parseXmlFile(path);
		NodeList nodes = imageDoc.getChildNodes();
		buildFromFile(nodes, g);
	}

	private static void buildFromFile(NodeList nodes, Graphics g) {
		if (nodes.getLength() < 2)
			nodes = nodes.item(0).getChildNodes();
		String name = "";

		for (int i = 0; i < nodes.getLength(); i++) {
			if(nodes.item(i).hasChildNodes())
				buildFromFile(nodes.item(i).getChildNodes(), g);
			name = nodes.item(i).getNodeName();
			if (name.startsWith("img_")) {
				store.put(name.substring(4),
						buildSimpleHolder(nodes.item(i).getTextContent(), g));
			} else if (name.startsWith("ani_")) {
				NodeList subList = nodes.item(i).getChildNodes();
				String subName = "";
				String location = "";
				int[] grid = new int[2];
				int imageNo = 0;
				for (int j = 0; j < subList.getLength(); j++) {
					subName = subList.item(j).getNodeName();
					if (subName.equalsIgnoreCase("location"))
						location = subList.item(j).getTextContent();
					else if (subName.equalsIgnoreCase("grid"))
						grid = WorldFactory.parseXY(subList.item(j)
								.getTextContent());
					else if (subName.equalsIgnoreCase("number"))
						imageNo = Integer.parseInt(subList.item(j)
								.getTextContent());
				}
				store.put(name.substring(4),
						buildAnimationHolder(location, imageNo, grid, g));
			} else if (name.equalsIgnoreCase("room")) {
				buildFromFile(nodes.item(i).getTextContent(), g);
			} else if (name.equalsIgnoreCase("actor")
					|| name.equalsIgnoreCase("player")
					|| name.equalsIgnoreCase("object")) {
				NodeList subList = nodes.item(i).getChildNodes();
				for (int j = 0; j < subList.getLength(); j++) {
					if (subList.item(j).getNodeName().equalsIgnoreCase("xml"))
						buildFromFile(subList.item(j).getTextContent(), g);
				}
			}
		}
	}
	
	private static TextureHolder buildSimpleHolder(String location, Graphics g) {
		return new SimpleTextureHolder(g.newPixmap(location,
				PixmapFormat.ARGB8888));
	}

	private static TextureHolder buildAnimationHolder(String location,
			int images, int[] grid, Graphics g) {
		return new SimpleTextureHolder(g.newAnimationPixmap(location,
				PixmapFormat.ARGB8888, grid, images));
	}

}
