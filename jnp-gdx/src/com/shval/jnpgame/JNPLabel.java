package com.shval.jnpgame;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class JNPLabel {

	private static final String TAG = JNPLabel.class.getSimpleName();
	static final int widthPx = 16;
	static final int heightPx = 38;
	private int x;
	private int y;
	private ArrayList<JNPChar> chars;
	private String text;
	
	private class JNPChar {
		int graphicX;
		int graphicY;
		int width;
		int height;
		TextureRegion textureRegion;
		
		JNPChar(TextureRegion textureRegion) {
			this.textureRegion = textureRegion;
		}
		
		void setLocation(int graphicX, int graphicY, int width, int height) {
			this.graphicX = graphicX;
			this.graphicY = graphicY;
			this.width = width;
			this.height = height;
		}
		
		void render(SpriteBatch batch) {
			batch.draw(textureRegion, graphicX, graphicY, width, height);
		}
		
	}
	
	JNPLabel(String textIn, int x, int y) {
		this.text = textIn;
		this.x = x;
		this.y = y;
		Texture fontTexture = Assets.getFontTexture();
		chars = new ArrayList<JNPChar>();
		

		for (int i = 0; i < text.length(); i++) {
			int offsetX, offsetY;
			char c = text.charAt(i);
			
			if ((c >= ' ') && (c <= 'O')) {
				offsetX = (c - ' ') * widthPx;
				offsetY = 0;
			}
			else if ((c >= 'P') && (c <= '~')) {
				offsetX = (c - 'P') * widthPx;
				offsetY = heightPx;
			}
			else {
				offsetX = 0;
				offsetY = 0;
				Gdx.app.error(TAG, "Char " + c + " unsupported");
			}
			Gdx.app.debug(TAG, "Char " + c + " location: " + offsetX + ", " + offsetY);
			chars.add(new JNPChar(new TextureRegion(fontTexture,  offsetX, offsetY, widthPx, heightPx)));
		}
	}
	
	public void setResolution(int cellWidth, int cellHeight) {
		int width = cellWidth / 2;
		int height = cellHeight * 3 / 4;
		int graphicX = x * cellWidth;
		int graphicY = y * cellHeight + cellHeight - height;
		for (JNPChar c: chars) {
			c.setLocation(graphicX, graphicY, width, height);
			graphicX += width;
		}
	}
	
	public void render(SpriteBatch spriteBatch) {
		for (JNPChar c: chars)
			c.render(spriteBatch);
	}
}
