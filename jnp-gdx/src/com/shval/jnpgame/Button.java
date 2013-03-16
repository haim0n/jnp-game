package com.shval.jnpgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Button {
	private static final String TAG = Button.class.getSimpleName();
	private TextureRegion lowerLeftCorner;
	private TextureRegion upperLeftCorner;
	private TextureRegion upperRightCorner;
	private TextureRegion lowerRightCorner;
	
	private TextureRegion icon; 					// this one is optional - provide it to draw buttons with icons
	private String caption;					// button text
	private float width; 						// 1 - means circular button, >1 --> oval button. units are in cells
	private int x, y;  						// lower left corner
	private int type;
	private int cellWidth, cellHeight;
	
	private Texture allButtonsTexture;
	private Texture allButtonIconsTexture;
	private static final int iconSizePx = 42;
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
	public static final int ICON_ARROW_DOWN 	= 4;
	public static final int ICON_ARROW_CIRC  	= 5;
	
	// width in cellSizes
	public Button(int x, int y, float width, int type, int icon) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.width = width;
		initButtonTextures(x, y, type, width);
		setIconType(icon);
		Gdx.app.debug(TAG, "x" + x + ",y" + y);
	}
	
	public void setIconType(int icon) {
		switch (icon) {
		case ICON_ARROW_CIRC:
			this.icon = new TextureRegion(allButtonIconsTexture, 80, 0, iconSizePx, iconSizePx);
			break;
		case ICON_ARROW_RIGHT:
			this.icon = new TextureRegion(allButtonIconsTexture, 80, 0, iconSizePx, iconSizePx);
			break;
		case ICON_NONE:
		default:
				this.icon = null;
				break;
		}
		
		//this.icon = icon;
	}
	
	private void initButtonTextures(int x, int y, int buttonType, float width) {
		allButtonsTexture = Assets.getButtonsTexture();
		allButtonIconsTexture = Assets.getButtonIconsTexture();
		
		int widthPx = (int) (48 * width) - 24;
		int yOffset = buttonType * 128;
		lowerLeftCorner = new TextureRegion(allButtonsTexture,  4, 100 + yOffset, widthPx, 24);
		upperLeftCorner = new TextureRegion(allButtonsTexture,  4, 4 + yOffset, widthPx, 24);
		upperRightCorner = new TextureRegion(allButtonsTexture,  228, 4 + yOffset, 24, 24);
		lowerRightCorner = new TextureRegion(allButtonsTexture,  228, 100 + yOffset, 24, 24);

	}
	
	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	public void setResolution(int cellWidth, int cellHeight) {
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
	}
	
	// TODO: figure out how to merge several textures into one entity
	public void render(SpriteBatch spriteBatch) {
		int graphicX, graphicY;
		graphicX = x * cellWidth;
		graphicY = y * cellHeight;
		
		spriteBatch.draw(lowerLeftCorner, graphicX, graphicY, width * cellWidth - cellWidth/2, cellHeight/2);
		spriteBatch.draw(upperLeftCorner, graphicX, graphicY + cellHeight/2, width * cellWidth - cellWidth/2, cellHeight/2);
		
		spriteBatch.draw(upperRightCorner, graphicX + width * cellWidth - cellWidth/2, graphicY + cellHeight/2, cellWidth/2, cellHeight/2);
		spriteBatch.draw(lowerRightCorner, graphicX + width * cellWidth - cellWidth/2, graphicY, cellWidth/2, cellHeight/2);
		
		if (icon != null) {
			spriteBatch.draw(icon, graphicX + (cellWidth - iconSizePx)/2, graphicY + (cellHeight - iconSizePx)/2, 
					iconSizePx * cellWidth / 48,
					iconSizePx * cellHeight / 48);
		}
		
	}
}