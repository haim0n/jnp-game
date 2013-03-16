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
	private float x;
	private float y;
	private ArrayList<JNPChar> chars;
	private String text;
	private Texture frameTexture;
	private int cellWidth;
	private int cellHeight;
	
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
	
	JNPLabel(String textIn, float x, float y, boolean showFrame) {
		this.text = textIn;
		this.x = x;
		this.y = y;
		Texture fontTexture = Assets.getFontTexture();
		if (showFrame)
			frameTexture = Assets.getMsgFrameTexture();
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
				offsetY = 0 + heightPx;
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
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		int width = cellWidth / 3;
		int height = cellHeight * 3 / 4;
		int graphicX = (int) (x * (float) cellWidth);
		int graphicY = (int) (y * (float) cellHeight + (float) cellHeight / 8 );
		for (JNPChar c: chars) {
			c.setLocation(graphicX, graphicY, width, height);
			graphicX += width;
		}
	}
	
	public void render(SpriteBatch spriteBatch) {
		//Gdx.app.debug(TAG, "Rendering - " + text);
		if (frameTexture != null) {
			spriteBatch.draw(frameTexture,
					(x - 1) * cellWidth, (y - 0.5f )* cellHeight,
					text.length() * cellWidth/3 + cellWidth * 2, cellHeight * 2);
		}
		for (JNPChar c: chars)
			c.render(spriteBatch);
	}
}
