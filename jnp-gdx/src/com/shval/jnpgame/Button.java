package com.shval.jnpgame;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Button {
	private static final String TAG = Button.class.getSimpleName();
	private ArrayList<Sprite> offSpriteList;
	private ArrayList<Sprite> onSpriteList;
	private ArrayList<Sprite> curSpriteList;
	private Sprite icon; 					// this one is optional - provide it to draw buttons with icons
	private String caption;					// button text
	private boolean isPressed;
	private float onTime; 					// how long does the button remains pressed, before it pops back
	private int width; 					// 0 - means circular button, >0 --> oval button
	
	private int x, y;  						// lower left corner
	private int type;
	
	private Texture allButtonsTexture;
	private Texture allButtonIconsTexture;
	
	// button types - keep those subsequent
	public static final int BLACK_BG_BLUE_FRAME  = 100;
	public static final int BLUE_BG_BLACK_FRAME  = 101;
	public static final int BLACK_BG_BROWN_FRAME = 102;
	public static final int BROWN_BG_BROWN_FRAME = 103;

	// button icons
	public static final int ICON_NONE 			= 0;
	public static final int ICON_ARROW_LEFT  	= 1;
	public static final int ICON_ARROW_RIGHT 	= 2;
	public static final int ICON_ARROW_UP 		= 3;
	public static final int ICON_ARROW_DOWN 	= 4;
	public static final int ICON_ARROW_CIRC  	= 5;
	
	public Button(int x, int y, int width, int height,int type, int icon) {
		isPressed = false;
		this.x = x;
		this.y = y;
		this.width = width;
		this.type = type;
		initButtonSprite(x, y, type);
		setIconType(icon);
		curSpriteList = offSpriteList;
		Gdx.app.debug(TAG, "x" + x + ",y" + y);
	}
	
	public void setIconType(int icon) {
		switch (icon) {
		case ICON_ARROW_CIRC:
			this.icon = new Sprite(allButtonIconsTexture, 87, 8, 114-87, 35-8);
			//this.icon.setX()
			break;
		case ICON_NONE:
		default:
				this.icon = null;
				break;
		}
		
		//this.icon = icon;
	}
	
	private void initButtonSprite(int x, int y, int buttonType) {
		offSpriteList = new ArrayList<Sprite>();
		allButtonsTexture = Assets.getButtonsTexture();
		allButtonIconsTexture = Assets.getButtonIconsTexture();
		
		switch (buttonType) {
		case BLACK_BG_BLUE_FRAME:
			// lower part
			Sprite sprite = new Sprite(allButtonsTexture, 4, 100, 251 - 4, 28 - 4);
			sprite.setX(x);
			sprite.setY(y);
			offSpriteList.add(sprite);

			// upper part
			sprite = new Sprite(allButtonsTexture, 4, 4, 251 - 4, 28 - 4);
			sprite.setX(x);
			sprite.setY(y + sprite.getHeight());
			offSpriteList.add(sprite);

			//sprite.setScale(scaleX, 1);
			break;
		default:
			Gdx.app.error(TAG, "Wrong button type" + buttonType);
			break;
		}
		
	}
	
	public void setOffTextures(ArrayList<Sprite> offSpriteList) {
		this.offSpriteList = offSpriteList;	
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	public void press() {
		isPressed = true;
	}
	
	public void resize(int width, int height) {
		for (Sprite sprite: curSpriteList) {
			//sprite.setScale(width/sprite.getWidth(), height/sprite.getHeight());
		}
	}
	
	// TODO: figure out how to merge several textures into one entity
	public void setPosition(int x, int y) {
		boolean isLowerHalf = true;
		float newY;
		for (Sprite sprite: curSpriteList) {
			if (!isLowerHalf)
				newY  = y + sprite.getHeight();
			else
				newY = y;
			isLowerHalf = false;
			sprite.setPosition(x, newY);
		}
	}
	
	public void render(SpriteBatch spriteBatch) {
		for(Sprite sprite: curSpriteList) {
			sprite.draw(spriteBatch);
		}
	}
}