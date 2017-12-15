package com.thecubecast.Data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.io.FileUtils;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class ReadWrite {
	
	static Random rand;
	
	public void init() { //Create the folders that hold everything neatly
		rand = new Random();
		Path path = Paths.get("Saves");
		//Path p3 = Paths.get(URI.create("http:///dev.thecubecast.com/Login.php?User=BLANK"));
		
		if (Files.notExists(path)) {
			new File("Saves").mkdir();
			Common.print("Created 'Saves' folder!");
		}
	}
	
	public static Object[] LoadSettings() {
		//all the code that reads the file
		
		Path SettingsPath = Paths.get("Settings.properties");
		Object[] settup = null;
		
		if (Files.notExists(SettingsPath)) { // runs if the settings file does not exist
			//create new settings file with universal settings that work for everyone 
			Path path = Paths.get("", "Settings.properties");
			ArrayList<String> lines = new ArrayList<String>();
	        lines.add("#Settings");
	        lines.add("\n");
	        lines.add("Agreed:False");
			try {
				Files.write(path, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
			} catch (IOException e) {e.printStackTrace();}
		}
		else {
			//save each setting to a value in the array
			try {
				settup = Files.readAllLines(SettingsPath).toArray();
			} catch (IOException e) {e.printStackTrace();}
			
		}
		return settup; // returns the array containing each value in settings. Settings file format can be dynamically changed to add new values.
		
	}
	
	public boolean CreateSave(String Title) {
		//creates the world folder
		new File("Saves/"+Title).mkdir();
		new File("Saves/"+Title+"/Chunks").mkdir();
		Common.print("Created '"+ Title +"' save!");

		
		//returns true or false depending on whether world files were successfully loaded
		return true;
		//the chunks are loaded independently from the world creation.
	}
	
	//Does the Tiled Serialization
	public boolean CreateWorld(String Save, int width, int height) {
		
		Path checkPath = Paths.get("Saves/" + Save, "");
		if (Files.notExists(checkPath) != true) {
			return false;
		}

		Path path = Paths.get("Saves/" + Save, "map.tmx");
		new File("Saves/"+Save).mkdir();
		new File("Saves/"+Save+"/tileset").mkdir();
		
		File source = new File("WorldGen/untitled.tsx");
		File dest = new File("Saves/" + Save + "");
		File source1 = new File("WorldGen/tileset/packed.atlas");
		File source2 = new File("WorldGen/tileset/packed.png");
		File dest1 = new File("Saves/" + Save + "/tileset");
		try {
		    FileUtils.copyFileToDirectory(source, dest);
		    FileUtils.copyFileToDirectory(source1, dest1);
		    FileUtils.copyFileToDirectory(source2, dest1);
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		ArrayList<String> lines = new ArrayList<String>();
        lines.add("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><map height=\"" + height + "\" nextobjectid=\"10\" orientation=\"orthogonal\" renderorder=\"right-down\" tiledversion=\"1.0.3\" tileheight=\"16\" tilewidth=\"16\" version=\"1.0\" width=\"" + width + "\"><properties><property name=\"atlas\" value=\"tileset/packed.atlas\"/></properties> <Custom playerSpawnX=\"" + (width/2) + "\" playerSpawnY=\"" + (height-11) + "\"  />\r\n" + 
        		" <tileset firstgid=\"1\" source=\"untitled.tsx\"/>\r\n" + 
        		" <layer height=\"" + height + "\" name=\"BACKGROUND\" width=\"" + width + "\">\r\n" + 
        		"  <data encoding=\"csv\">");
        
        for(int y = 0; y <= height; y++) {//	Generates the Sky tiles above the grass
        	String line = "";
        	for(int x = 0; x <= width; x++) { 
        		line = line + "8,";
        	}
        	lines.add(line);
        }        		
    	
        lines.add("</data>\r\n" + 
        		" </layer>\r\n" + 
        		" <layer height=\"" + height + "\" name=\"TILES\" width=\"" + width + "\">\r\n" + 
        		"  <data encoding=\"csv\">");
        
        
        	//Below is the Tiles layer
        	//Below is the Tiles layer
        	
        for(int y = 0; y <= height; y++) {//	Generates the Sky tiles above the grass
        	String line = "";
        	
        	for(int x = 0; x <= width; x++) { 
        		line = line + "95,";
        	}
        	lines.add(line);
        }

        lines.add("</data>\r\n" + //Just wrapping up the file
        		" </layer>\r\n" + 
        		"</map>");
        
		try {
			Files.deleteIfExists(path);
			Files.write(path, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
		} catch (IOException e) {e.printStackTrace();}
		return false;
	}
	
	public boolean saveMap(TiledMap tiledMap, String Save, Player player) {
		
		int width = tiledMap.getProperties().get("width", Integer.class);
		int height = tiledMap.getProperties().get("height", Integer.class);

		Path path = Paths.get("Saves/" + Save, "map.tmx");
		
		ArrayList<String> lines = new ArrayList<String>();
        lines.add("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><map height=\"" + height + "\" nextobjectid=\"10\" orientation=\"orthogonal\" renderorder=\"right-down\" tiledversion=\"1.0.3\" tileheight=\"16\" tilewidth=\"16\" version=\"1.0\" width=\"" + width + "\"><properties><property name=\"atlas\" value=\"tileset/packed.atlas\"/></properties> <Custom playerSpawnX=\"" + (width/2) + "\" playerSpawnY=\"" + (height-11) + "\"  />\r\n" + 
        		" <tileset firstgid=\"1\" source=\"untitled.tsx\"/>\r\n" + 
        		" <layer height=\"" + height + "\" name=\"BACKGROUND\" width=\"" + width + "\">\r\n" + 
        		"  <data encoding=\"csv\">");
        
        TiledMapTileLayer BackLay = (TiledMapTileLayer)tiledMap.getLayers().get(0);
        for(int y = 0; y < height; y++) {//	Generates the Background tiles
        	String line = "";
        	for(int x = 0; x < width; x++) { 
        		int tempTileID = BackLay.getCell(x, y).getTile().getId();
        		line = line + tempTileID + ",";
        	}
        	lines.add(line);
        }        		
        
        
        
        
        
        
        //Splits the file between background and Tiled layer
        lines.add("</data>\r\n" + 
        		" </layer>\r\n" + 
        		" <layer height=\"" + height + "\" name=\"TILES\" width=\"" + width + "\">\r\n" + 
        		"<properties>\r\n" + 
        		"   <property name=\"Cash\" type=\"int\" value=\"" + player.Cash + "\"/>\r\n" + 
        		"   <property name=\"Fuel\" type=\"float\" value=\"" + player.Gas + "\"/>\r\n" + 
        		"   <property name=\"SavedX\" type=\"float\" value=\"" + player.getLocation()[0] + "\"/>\r\n" + //DEFAULT WAS 30
        		"   <property name=\"SavedY\" type=\"float\" value=\"" + player.getLocation()[1] + "\"/>\r\n" +   //DEFAULT IS height-12
        		"  </properties>" + 
        		"  <data encoding=\"csv\">");
        
        
        
        
        
        TiledMapTileLayer FrontLay = (TiledMapTileLayer)tiledMap.getLayers().get("TILES");
        for(int y = 0; y < height; y++) {//	Generates the main tiles
        	String line = "";
        	for(int x = 0; x < width; x++) { 
        		int tempTileID = FrontLay.getCell(x, y).getTile().getId();
        		line = line + tempTileID + ",";
        	}
        	lines.add(line);
        }      
        
        lines.add("</data>\r\n" + //Just wrapping up the file
        		" </layer>\r\n" + 
        		"</map>");
        
		try {
			Files.deleteIfExists(path);
			Files.write(path, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
		} catch (IOException e) {e.printStackTrace();}
		return false;
	}
}