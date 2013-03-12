package com.shval.jnpgame;


import static com.shval.jnpgame.Globals.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Assets {
	
    public static Texture redTexture;
    public static Texture blueTexture;
    public static Texture greenTexture;
    public static Texture yellowTexture;
    public static Texture blackTexture;
    public static Texture wallTexture;
    public static Texture bgTexture;
    public static Texture buttonsTexture;
    //public static TextureAtlas atlas;  
      
    public static void load () {
        //String textureFile = "data/frogger.txt";
        //atlas = new TextureAtlas(Gdx.files.internal(textureFile), Gdx.files.internal("data"));
        redTexture = new Texture(Gdx.files.internal("data/jelly_red.png"));
        blueTexture = new Texture(Gdx.files.internal("data/jelly_blue.png"));
        redTexture = new Texture(Gdx.files.internal("data/jelly_red.png"));
        greenTexture = new Texture(Gdx.files.internal("data/jelly_green.png"));
        yellowTexture = new Texture(Gdx.files.internal("data/jelly_yellow.png"));
        blackTexture = new Texture(Gdx.files.internal("data/jelly_black.png"));
        wallTexture = new Texture(Gdx.files.internal("data/wall0.png"));
        bgTexture = new Texture(Gdx.files.internal("data/bg00.png"));
        buttonsTexture = new Texture(Gdx.files.internal(("data/button.png")));
    }

    public static Texture getBgTexture(int level) {
    	// TODO: add more BGs
    	switch(level) {
		case 0:
			return bgTexture;
		default:
			return bgTexture;
    	}
    }
    
    public static Texture getTexture (int type) {
    	Texture texture;
    	
		switch(type) {
		case WALL:
			texture = wallTexture;
			break;
		case JELLY_BLUE:
			texture = blueTexture;
			break;
		case JELLY_GREEN:
			texture = greenTexture;
			break;
		case JELLY_RED:
			texture = redTexture;;
			break;
		case JELLY_YELLOW:
			texture = yellowTexture;
			break;
		case NONE:
			texture = null;
			break;

		default:
			texture = blackTexture;
			break;
		}

		if (texture == null)
			return null;
		
		return texture;
    }

	public static Texture getButtonsTexture(int level) {
			return buttonsTexture;
	}  
 
    /*
    public static TextureRegion getFrame (String name, int index) {  
        return atlas.findRegion(name, index);  
    } 
     */
}
