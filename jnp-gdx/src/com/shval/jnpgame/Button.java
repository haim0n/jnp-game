package com.shval.jnpgame;

import java.util.ArrayList;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Button {
	private ArrayList<Sprite> offSpriteList;
	private ArrayList<Sprite> onTextureList;
	private ArrayList<Sprite> currTextureList;
	private Screen screen;
	private boolean isPressed;
	private float onTime; // how long does the button remains pressed, before it pops back 
	
	public Button(ArrayList<Sprite> onTextureList, ArrayList<Sprite> offSpriteList, Screen screen, float onTime) {
		this.onTextureList = onTextureList;
		this.offSpriteList = offSpriteList;
		this.currTextureList = offSpriteList;
		this.screen = screen;
		isPressed = false;
	}
	
	public void press() {
		isPressed = true;
		currTextureList = onTextureList;
	}
	
	public void render(SpriteBatch spriteBatch) {
		for(Sprite sprite: currTextureList) {
			sprite.draw(spriteBatch);
		}
	}
}