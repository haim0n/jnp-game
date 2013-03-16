package com.shval.jnpgame;


import static com.shval.jnpgame.Globals.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class Assets {
	
    public static Texture redTexture;
    public static Texture blueTexture;
    public static Texture greenTexture;
    public static Texture yellowTexture;
    public static Texture blackTexture;
    
    public static Texture wallTexture0;
    public static Texture wallTexture1;
    public static Texture wallTexture3;
    public static Texture wallTexture5;
    public static Texture wallTexture6;
    
    public static Texture bgTexture0;
    public static Texture bgTexture1;
    public static Texture bgTexture21;
    public static Texture bgTexture22;
    public static Texture bgTexture30;    
    public static Texture bgTexture51;
    public static Texture bgTexture52;
    
    public static Texture buttonsTexture;
    public static Texture smallStarTexture;
    public static Texture buttonIconsTexture;
    public static Texture fontTexture;
    //public static TextureAtlas atlas;  
    
    public static Sound fallSound;
    public static Sound slideSound;
    public static Sound mergeStartSound;
    public static Sound mergeFinishSound;
    public static Sound buttonSound;
    public static Sound winSound;
    
      
    public static void load () {
    	
        //String textureFile = "data/frogger.txt";
        //atlas = new TextureAtlas(Gdx.files.internal(textureFile), Gdx.files.internal("data"));
        redTexture = new Texture(Gdx.files.internal("data/jelly_red.png"));
        blueTexture = new Texture(Gdx.files.internal("data/jelly_blue.png"));
        redTexture = new Texture(Gdx.files.internal("data/jelly_red.png"));
        greenTexture = new Texture(Gdx.files.internal("data/jelly_green.png"));
        yellowTexture = new Texture(Gdx.files.internal("data/jelly_yellow.png"));
        blackTexture = new Texture(Gdx.files.internal("data/jelly_black.png"));
        
        wallTexture0 = new Texture(Gdx.files.internal("data/wall0.png"));
        wallTexture1 = new Texture(Gdx.files.internal("data/wall1.png"));
        wallTexture3 = new Texture(Gdx.files.internal("data/wall3.png"));
        wallTexture5 = new Texture(Gdx.files.internal("data/wall5.png"));
        wallTexture6 = new Texture(Gdx.files.internal("data/wall6.png"));
        
        bgTexture0 = new Texture(Gdx.files.internal("data/bg00.png"));
        bgTexture1 = new Texture(Gdx.files.internal("data/bg01.png"));
        bgTexture21 = new Texture(Gdx.files.internal("data/bg21.png"));
        bgTexture22 = new Texture(Gdx.files.internal("data/bg22.png"));
        bgTexture30 = new Texture(Gdx.files.internal("data/bg30.png"));
        bgTexture51 = new Texture(Gdx.files.internal("data/bg51.png"));
        bgTexture52 = new Texture(Gdx.files.internal("data/bg52.png"));
        
        buttonsTexture = new Texture(Gdx.files.internal(("data/button.png")));
        smallStarTexture = new Texture(Gdx.files.internal(("data/small_star.png")));
        buttonIconsTexture = new Texture(Gdx.files.internal(("data/bi.png")));
        fontTexture = new Texture(Gdx.files.internal(("data/font.png")));
        
        fallSound = Gdx.audio.newSound(Gdx.files.internal("data/0.wav"));
        slideSound = Gdx.audio.newSound(Gdx.files.internal("data/1.wav"));
        mergeStartSound = Gdx.audio.newSound(Gdx.files.internal("data/3.wav"));
        mergeFinishSound = Gdx.audio.newSound(Gdx.files.internal("data/2.wav"));
        winSound = Gdx.audio.newSound(Gdx.files.internal("data/10.wav"));
        buttonSound = Gdx.audio.newSound(Gdx.files.internal("data/12.wav"));
        
    }

    public static Texture getBgTexture(int index) {
    	// TODO: add more BGs
    	switch(index) {
		case 0:
			return bgTexture0;
		case 1:
			return bgTexture1;
		case 21:
			return bgTexture21;
		case 22:
			return bgTexture22;
		case 30:
			return bgTexture30;			
		case 51:
			return bgTexture51;			
		case 52:
			return bgTexture52;			
			
		default:
			Gdx.app.error("Assets", "Invalid backgroud texture " + index);
			return null;
    	}
    }
    
    private static Texture getWallTexture(int level) {
    	
    	Texture texture;
    	
		switch(level) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
			texture = wallTexture0;
			break;
			
		case 5:
		case 6:
		case 7:
		case 8:
		case 13:
		case 14:
		case 15:
		case 16:			
			texture = wallTexture3;
			break;

		case 9:
		case 10:
		case 11:
		case 12:
		case 17:
		case 18:
		case 19:
		case 20:
			texture = wallTexture1;
			break;
			
		default:
			Gdx.app.error("Assets", "Invalid wall texture for level " + level);
			texture = blackTexture;
			break;
		}
		
		return texture;
    }
    
    
    public static Texture getTexture (int type, int level) {
    	Texture texture;
    	
		switch(type) {
		case WALL:
			texture = getWallTexture(level);
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
			Gdx.app.debug("Assets", "Defauld jelly texture black for type " + type);
			texture = blackTexture;
			break;
		}

		if (texture == null) {
			return null;
		}
		
		return texture;
    }

	public static Texture getButtonsTexture() {
			return buttonsTexture;
	}

	public static Texture getButtonIconsTexture() {
		return buttonIconsTexture;
	}

	public static Texture getSmallStarTexture() {
		return smallStarTexture;
	}

	public static Sound getSound(int soundID) {
		switch (soundID) {
		case (SOUND_FALL):
			return fallSound;
		case (SOUND_SLIDE):
			return slideSound;
		case (SOUND_MERGE_START):
			return mergeStartSound;
		case (SOUND_MERGE_FINISH):
			return mergeFinishSound;
		case (SOUND_WIN):
			return winSound;
		case (SOUND_BUTTON):
			return buttonSound;
		default: 
			Gdx.app.error("Assets", "Invalid sound ID " + soundID);
			return null;
		}
	}

	public static Texture getFontTexture() {
		return fontTexture;
	}  
}
