// GameState that tests new mechanics.

package com.thecubecast.GameStates;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.XmlWriter;
import com.thecubecast.Data.Achievement;
import com.thecubecast.Data.Common;
import com.thecubecast.Data.GameStateManager;
import com.thecubecast.Data.Player;
import com.thecubecast.Graphics.MenuState;
import com.thecubecast.Data.KeysDown;

public class PlayState extends GameState {
	
	//The Menu states
	private int OldState;
	private int currentState;
	OrthographicCamera cameraGui;

	Vector3 position;
	long last_time;
	int deltaTime;

    SpriteBatch guiBatch;
    
    //HUD Elements
    boolean menuOpen = false;
    int tics = 0;
    boolean flashfuel = false;
    List<Achievement> Achievements = new ArrayList<Achievement>();
    List<Achievement> MoneyFeedback = new ArrayList<Achievement>();
    
    //Controls
    List<KeysDown> KeysDw = new ArrayList<KeysDown>();
    
    OrthographicCamera camera;
    
    TiledMap tiledMap;
    TiledMapTileLayer groundLay;
    TiledMapRenderer tiledMapRenderer;

	public PlayState(GameStateManager gsm) {
		super(gsm);
	}
	
	public void AddAchievement(String text, int IconID, float Time, float Durration, boolean Anim) {
		Achievement temp = new Achievement(text, IconID, Time,  Durration, Anim);
		Achievements.add(Achievements.size(), temp);
		Common.print("Added Achievement: " + text);
	}
	
	public void init() {
		
		//gsm.Rwr.CreateWorld(gsm.ChosenSave, 200, 200);
		
		 // tiledMap = new AtlasTmxMapLoader().load("Saves/Save2/MegaMiner_FirstMap.tmx");
        tiledMap = new AtlasTmxMapLoader().load("Saves/Test/map.tmx");
        //tiledMap.getTileSets().getTileSet(0).getTile(1).getTextureRegion().getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap , 5f);
        groundLay = (TiledMapTileLayer)tiledMap.getLayers().get(1);
		
        int SavedCash = (Integer) groundLay.getProperties().get("Cash");
        float SavedGas = (Float) groundLay.getProperties().get("Fuel");
        float SavedX = (Float) groundLay.getProperties().get("SavedX");
        float SavedY = (Float) groundLay.getProperties().get("SavedY");
        
        Common.print("SavedCash: " + SavedCash);
        		
		//gsm.Audio.playMusic("Wind", true);
		
		guiBatch = new SpriteBatch();
	        
        camera = new OrthographicCamera();
        camera.setToOrtho(false,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        
        //camera.position.set((player.getLocation()[0]*80)+40, (player.getLocation()[1]*80)+40, camera.position.z);
  		position = camera.position;
        
  		last_time = System.nanoTime();
	}
	
	public void update() {
		handleInput();
		
		long time = System.nanoTime();
	    deltaTime = (int) ((time - last_time) / 1000000);
	    last_time = time;
	    
		camera.update();
		
	}
	
	public void draw(SpriteBatch g, int width, int height, float Time) {
		RenderCam();
		
		g.begin();
		g.setProjectionMatrix(camera.combined);
			
		g.end();

		//Overlay Layer
		guiBatch.begin();
		//guiBatch.setProjectionMatrix(cameraGui.combined);
		 
	    if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) { //KeyHit
	    	int TileID;
	    	
	    	gsm.Cursor = 2;
	   
	    	Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
			camera.unproject(pos);
			
	    	if (groundLay.getCell(Common.roundDown(pos.x)/80, Common.roundDown(pos.y)/80).getTile() != null) {
	    		TileID = groundLay.getCell(Common.roundDown(pos.x)/80, Common.roundDown(pos.y)/80).getTile().getId();
	    		camera.project(pos);
	    		gsm.Render.HUDDescr(guiBatch, Common.roundDown(pos.x)+15, Common.roundDown(pos.y)-15, "X: " + Common.roundDown(pos.x/80) + " Y: "+ Common.roundDown(pos.y/80));
	    		gsm.Render.HUDDescr(guiBatch, Common.roundDown(pos.x)+15, Common.roundDown(pos.y)-30, "" + TileID);
	    	} else {
	    		gsm.Render.HUDDescr(guiBatch, Common.roundDown(pos.x)+15, Common.roundDown(pos.y)-15, "X: " + Common.roundDown(pos.x/80) + " Y: "+ Common.roundDown(pos.y/80));
	    		camera.project(pos);
	    	}
		} else {
			gsm.Cursor = 0;
		}
	    
	    guiBatch.end();
	}
	
	public void RenderCam() {
		camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
	}
	
	public void handleInput() {
		
		Vector3 pos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
		
		//cameraGui.unproject(pos);
		
		gsm.MouseX = (int) pos.x;
		gsm.MouseY = (int) pos.y;
		gsm.MouseClick[1] = (int) pos.x;
		gsm.MouseClick[2] = (int) pos.y;
		gsm.MouseDrag[1] = (int) pos.x;
		gsm.MouseDrag[2] = (int) pos.y;
		
		if(Gdx.input.isTouched()) {
			//camera.translate(-Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
			//camera.update();
			
			if (Gdx.input.getDeltaY() < 0) { // ZOOMS OUT
				
			}
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) { //KeyHit
			menuOpen = !menuOpen;
		}
		
		if (Gdx.input.isKeyPressed(Keys.UP)) { //KeyHit
			camera.translate(0,16);
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN)) { //KeyHit
			camera.translate(0,-16);
		}
		if (Gdx.input.isKeyPressed(Keys.LEFT)) { //KeyHit
			camera.translate(-16,0);
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) { //KeyHit
			camera.translate(16,0);
		}
		
			
		if (Gdx.input.isKeyJustPressed(Keys.F)) { //KeyHit
			//Common.print("Player is facing " + isFacing());
			//if(isFacing() == 74) {
				//AddAchievement("Activated Crate!", 73, gsm.CurrentTime, 1.5f, false);
				
			//}
			//if(isFacing() == 72 || isFacing() == 73) {
			//	AddAchievement("Activated Teleporter!", 6, gsm.CurrentTime, 5f, true);
				//player.setLocation(20, 20);
			//}
		}
	}
	
	public void isOn() { //return an Int
	//	if (groundLay.getCell(Common.roundDown(player.getLocation()[0]), Common.roundDown(player.getLocation()[1])).getTile() != null) {
	//		return (groundLay.getCell(Common.roundDown(player.getLocation()[0]), Common.roundDown(player.getLocation()[1])).getTile().getId());
	//	} else {
	//		return -1;
	//	}
		
	}
	
	public void isFacing() { // Returns an int
		
//		if (player.getLocation()[0]-1 < 0 || player.getLocation()[0]-1 > 100) {
//			return -1;
//		}
//		
//		if (player.getLocation()[1]-1 < 0 || player.getLocation()[1]-1 > 100) {
//			return -1;
//		}
//		
//		if (player.getDirection().equals("up")) {
//			return (groundLay.getCell(Common.roundDown(player.getLocation()[0]), Common.roundDown(player.getLocation()[1])+1).getTile().getId());
//		}
//		else if (player.getDirection().equals("down")) {
//			return (groundLay.getCell(Common.roundDown(player.getLocation()[0]), Common.roundDown(player.getLocation()[1])-1).getTile().getId());	
//		}
//		else if (player.getDirection().equals("left")) {
//			return (groundLay.getCell(Common.roundDown(player.getLocation()[0])-1, Common.roundDown(player.getLocation()[1])).getTile().getId());
//		}
//		else if (player.getDirection().equals("right")) {
//			return (groundLay.getCell(Common.roundDown(player.getLocation()[0])+1, Common.roundDown(player.getLocation()[1])).getTile().getId());
//		}
//		else {
//			return -1;
//		}
		
	}
	
	public void MineTiles() {
		//Checks if the tile the player is on matches an ID
		//if (isOn() != 75) {
			//Common.print("Hey you just got a " + groundLay.getCell(player.getLocation()[0], player.getLocation()[1]).getTile().getId());
		//}
		//if (player.getLocation()[1] < player.MaxY) {
		//	groundLay.getCell(Common.roundDown(player.getLocation()[0]), Common.roundDown(player.getLocation()[1])).setTile(tiledMap.getTileSets().getTile(75));	
		//}
	}
	
	//BELOW IS MENU CODE
	//SHOULD BE SOMEWHAT PORTABLE
	
	public void changeState(int state) {
		OldState = currentState;
		currentState = state;
	}
	
	public void Back() {
		int state = OldState;
		OldState = currentState;
		currentState = state;
	}
	
	public void reSize(SpriteBatch g, int H, int W) {
		float posX = camera.position.x;
		float posY = camera.position.y;
		float posZ = camera.position.z;
		camera.setToOrtho(false);
		camera.position.set(posX, posY, posZ);
		
		Matrix4 matrix = new Matrix4();
		matrix.setToOrtho2D(0, 0, W, H);
		guiBatch.setProjectionMatrix(matrix);
		//cameraGui.setToOrtho(false);
	}
	
	public void IsKeyPressed(Keys Keys) {
		
	}
}