package scalaAVGameEngine.graphics.twodee;

import java.util.Map;

import scalaAVGameEngine.graphics.Pixmap;


public class ImageStore {

	private static Map<String, TextureHolder> table;

	public ImageStore(Map<String, TextureHolder> table){
		this.table = table;
	}
	
	public Pixmap getImage(String label){
		if(table.get(label) != null)
			return table.get(label).getImage();
		else
			return null;
	}

}
