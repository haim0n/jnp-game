package com.shval.jnpgame;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Button {
	private static final String TAG = Cell.class.getSimpleName();
	private ArrayList<Sprite> offSpriteList;
	private ArrayList<Sprite> onSpriteList;
	private ArrayList<Sprite> curSpriteList;
	private Sprite icon; 					// this one is optional - provide it to draw buttons with icons
	private String caption;					// button text
	private boolean isPressed;
	private float onTime; 					// how long does the button remains pressed, before it pops back
	private int length; 					// 0 - means circular button, >0 --> oval button
	
	private int x, y;  						// lower left corner
	private int buttonType;
	
	private Texture allButtonsTexture;
	private Texture allButtonIconsTexture;
	
	// button types
	public static final int BUTTON_BLACK_BG_BLUE_FRAME  = 0;
	public static final int BUTTON_BLUE_BG_BLACK_FRAME  = 1;
	public static final int BUTTON_BLACK_BG_BROWN_FRAME = 2;
	public static final int BUTTON_BROWN_BG_BROWN_FRAME = 3;

	// button icons
	public static final int BUTTON_ICON_ARROW_LEFT 	= 0;
	public static final int BUTTON_ICON_ARROW_RIGHT = 1;
	public static final int BUTTON_ICON_ARROW_UP 	= 2;
	public static final int BUTTON_ICON_ARROW_DOWN 	= 3;
	public static final int BUTTON_ICON_ARROW_CIRC  = 4;
	
		
	public Button(int x, int y, int buttonLength, int buttonType) {
		isPressed = false;
		this.x = x;
		this.y = y;
		this.length = buttonLength;
		this.buttonType = buttonType;
		initButtonSprite(x, y, buttonType);
	}
	
	public void setIconType(int buttonIconType) {
		switch (buttonIconType) {
		case BUTTON_ICON_ARROW_CIRC:
			
			break;
		default:
				break;
		}
		
		//this.icon = icon;
	}
	
	private void initButtonSprite(int x, int y, int buttonType) {
		offSpriteList = new ArrayList<Sprite>();
		allButtonsTexture = Assets.getButtonsTexture();
		allButtonIconsTexture = Assests.getButtonIconsTexture();
		
		switch (buttonType) {
		case BUTTON_BLACK_BG_BLUE_FRAME:
			Sprite sprite = new Sprite(allButtonsTexture, 4, 27, 251 - 4, 28 - 4);
			offSpriteList.add(sprite);
			sprite = new Sprite(allButtonsTexture, 4, 100, 251 - 4, 28 - 4);
			offSpriteList.add(sprite);
			sprite.setX(x);
			sprite.setX(y);
			break;
		default:
			Gdx.app.error(TAG, "Wrong button type" + buttonType);
			break;
		}
		
	}
	
	public void setOffTextures(ArrayList<Sprite> offSpriteList) {
		this.offSpriteList = offSpriteList;	
	}

	public void setCaption(String caption)
	{
		this.caption = caption;
	}
	
	public void press() {
		isPressed = true;
	}
	
	public void render(SpriteBatch spriteBatch) {
		for(Sprite sprite: curSpriteList) {
			sprite.draw(spriteBatch);
		}
		if ()
	}
}