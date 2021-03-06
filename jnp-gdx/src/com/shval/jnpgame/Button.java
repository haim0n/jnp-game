package com.shval.jnpgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Button  {
	private static final String TAG = Button.class.getSimpleName();
	private TextureRegion lowerLeftCorner;
	private TextureRegion upperLeftCorner;
	private TextureRegion upperRightCorner;
	private TextureRegion lowerRightCorner;
	
	private TextureRegion icon; 			// this one is optional - provide it to draw buttons with icons
	private JNPLabel caption;				// button text
	private float width; 					// 1 - means circular button, >1 --> oval button. units are in cells
	private float x, y;  						// lower left corner
	private String id;
	private float cellWidth, cellHeight;
	private boolean isEnabled;
	
	private Texture allButtonsTexture;
	private Texture allButtonIconsTexture;
	private static final int iconSizePx = 40;
	// button types - keep those subsequent
	public static final int BLACK_BG_BLUE_FRAME  = 0;
	public static final int BLUE_BG_BLACK_FRAME  = 1;
	public static final int BLACK_BG_BROWN_FRAME = 2;
	public static final int BROWN_BG_BROWN_FRAME = 3;

	// button icons
	public static final int ICON_NONE 			= 0;
	public static final int ICON_ARROW_LEFT  	= 1;
	public static final int ICON_ARROW_RIGHT 	= 2;
	public static final int ICON_ARROW_UP 		= 3;
	public static final int ICON_ARROW_DOWN		= 4;
	public static final int ICON_ARROW_CIRC  	= 5;
		
	// width in cellSizes
	public Button(float x, float y, float width, int type, int icon, String caption, String id) {
		this.x = x;
		this.y = y;
		this.id = id;
		this.width = width;
		this.id = id;
		this.isEnabled = true;
		initButtonTextures(type, width);
		setIconType(icon);
		if (caption != null)
			this.caption = new JNPLabel(caption, x + (width - (float) caption.length() / 3) / 2, y, false);
		Gdx.app.debug(TAG, "x" + x + ", y" + y);
	}
	
	public void setIconType(int icon) {
		int i, j;
		
		switch (icon) {
		case ICON_ARROW_DOWN:
			i = 0;
			j = 0;
			break;
		case ICON_ARROW_UP:
			i = 1;
			j = 0;
			break;
		case ICON_ARROW_CIRC:
			i = 2;
			j = 0; 
			break;
		case ICON_ARROW_LEFT:
			i = 2;
			j = 1; 
			break;
		case ICON_ARROW_RIGHT:
			i = 0;
			j = 2; 
			break;
		case ICON_NONE:
		default:
			this.icon = null;
			return;
		}
		this.icon = new TextureRegion(allButtonIconsTexture, iconSizePx * i, iconSizePx * j, iconSizePx, iconSizePx);		
	}
	
	private void initButtonTextures(int buttonType, float width) {
		allButtonsTexture = Assets.getButtonsTexture();
		allButtonIconsTexture = Assets.getButtonIconsTexture();
		
		int widthPx = (int) (48 * width) - 24;
		int yOffset = buttonType * 128;
		lowerLeftCorner = new TextureRegion(allButtonsTexture,  4, 100 + yOffset, widthPx, 24);
		upperLeftCorner = new TextureRegion(allButtonsTexture,  4, 4 + yOffset, widthPx, 24);
		upperRightCorner = new TextureRegion(allButtonsTexture,  228, 4 + yOffset, 24, 24);
		lowerRightCorner = new TextureRegion(allButtonsTexture,  228, 100 + yOffset, 24, 24);

	}
	
	public void setResolution(int cellWidth, int cellHeight) {
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		if (caption != null)
			caption.setResolution(cellWidth, cellHeight);
		
	}
	
	// TODO: figure out how to merge several textures into one entity
	public void render(SpriteBatch spriteBatch) {
		float graphicX, graphicY;
		graphicX = (x + 0.1f) * cellWidth;
		graphicY = y * cellHeight;
		
		spriteBatch.draw(lowerLeftCorner, graphicX, graphicY, width * cellWidth - cellWidth/2, cellHeight/2);
		spriteBatch.draw(upperLeftCorner, graphicX, graphicY + cellHeight/2, width * cellWidth - cellWidth/2, cellHeight/2);
		
		spriteBatch.draw(upperRightCorner, graphicX + width * cellWidth - cellWidth/2, graphicY + cellHeight/2, cellWidth/2, cellHeight/2);
		spriteBatch.draw(lowerRightCorner, graphicX + width * cellWidth - cellWidth/2, graphicY, cellWidth/2, cellHeight/2);
		if (caption != null) 
			caption.render(spriteBatch);
		if (icon != null) {
			spriteBatch.draw(icon,
					graphicX + (cellWidth * width/2) - iconSizePx * cellWidth / 48/2,
					graphicY + (cellHeight* width/2) - iconSizePx * cellHeight/ 48/2, 
					iconSizePx * cellWidth / 48,
					iconSizePx * cellHeight / 48);
		}
		
	}

	public String getId() {
		return id;
	}

	public boolean isPressed(float eventX, float eventY) {
		Gdx.app.debug(TAG, "Trying to press button " + id);
		Gdx.app.debug(TAG, "EventX/Y = " + eventX + ", " + eventY + " x/y = " + x + ", " + y + " width = " + width);
		if (!isEnabled) {
			Gdx.app.debug(TAG, "Disabled");
			return false;
		}
		if (eventX < x || eventX >= x + width) {
			Gdx.app.debug(TAG, "Out of x scope");
			return false;
		}
		if (eventY < y || eventY >= y + 1) { // TODO: add button height
			Gdx.app.debug(TAG, "Out of y scope");
			return false;
		}
		Gdx.app.debug(TAG, "Pressed");
		return true;
	}

	public void setIsEnabled(boolean b) {
		isEnabled = b;
	}

}
