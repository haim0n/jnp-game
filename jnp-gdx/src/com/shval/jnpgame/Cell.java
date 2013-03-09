package com.shval.jnpgame;

import static com.shval.jnpgame.Globals.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Cell {

	private static final String TAG = Cell.class.getSimpleName();
	
	private Texture rawTexture;	// the actual bitmap
	private TextureRegion textureRegions[][]; // each cell composed of 2 x 2 texture regions
	
	private int x;			// the X coordinate (in the board cells matrix)
	private int y;			// the Y coordinate 	"  "
	private float dxFromMilestone;
	private float dyFromMilestone;
	private Speed speed;	// the speed with its directions
	private Jelly jelly;
	private boolean isFixed;
	private int type;
	private boolean scanFlag; // was this cell encountered in current board scanning
	private static final int CELL_SIZE = 100; // 
	private static final float SPEED = 500;
	private int spriteWidth;
	private int spriteHeight;
	private boolean isResolutionSet = false;
	
	public Cell(Texture rawTexture, int x, int y, Jelly jelly,
			boolean fixed, int type) {
		
		this.rawTexture = rawTexture;
		textureRegions = new TextureRegion[2][2];

		textureRegions[0][0] = new TextureRegion(rawTexture, 8 + 1 * 48, 8 + 4 * 48 + 48 / 2, 48 / 2, 48 / 2);
		textureRegions[0][1] = new TextureRegion(rawTexture, 8 + 1 * 48, 8 + 0 * 48, 48 / 2, 48 / 2);				

		textureRegions[1][0] = new TextureRegion(rawTexture, 8 + 3 * 48 + 48 / 2, 8 + 4 * 48 + 48 / 2, 48 / 2, 48 / 2);
		textureRegions[1][1] = new TextureRegion(rawTexture, 8 + 3 * 48 + 48 / 2, 8 + 0 * 48, 48 / 2, 48 / 2);				

		this.x = x;
		this.y = y;
		this.jelly = jelly;
		this.isFixed = fixed;
		this.type = type;
		speed = new Speed(0, 0);
		
	}
	
	public void setResolution(int spriteWidth, int spriteHeight) {
		// set width only on first call
		if (!isResolutionSet) {
			this.spriteWidth = spriteWidth;
			this.spriteHeight = spriteHeight;
			isResolutionSet = true;
		}
		
	}
	
	public void resetScanFlag() {
		scanFlag = false;
	}
	
	public Jelly getJelly() {
		return jelly;
	}

	void setJelly(Jelly newJelly) {
		this.jelly = newJelly;
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public void render(SpriteBatch spriteBatch) {
		//canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
		int graphicDx, graphicDy;
		if (speed.getXv() != 0)
			graphicDx = (spriteWidth * (int) dxFromMilestone) / CELL_SIZE;
		else
			graphicDx = 0;
		
		if (speed.getYv() != 0)
			graphicDy = (spriteHeight * (int) dyFromMilestone) / CELL_SIZE;
		else
			graphicDy = 0;
		
		int graphicX = spriteWidth * x + graphicDx;
		int graphicY = spriteHeight * y + graphicDy;
		if(false) {
			Gdx.app.debug(TAG, "(" + x + ", " + y + "): dx, dy = " + dxFromMilestone + ", " + dyFromMilestone);
			Gdx.app.debug(TAG, "(" + x + ", " + y + "): dx, dy = " + dxFromMilestone + ", " + dyFromMilestone);
			Gdx.app.debug(TAG, "(" + x + ", " + y + "): Gdx, Gdy = " + graphicDx + ", " + graphicDy);
			Gdx.app.debug(TAG, "(" + x + ", " + y + "): rendering at (" + graphicX + ", " + graphicY + ")");
		}
		spriteBatch.draw(textureRegions[0][0], graphicX, graphicY,
				spriteWidth / 2, spriteHeight / 2);
		
		spriteBatch.draw(textureRegions[0][1], graphicX, graphicY + spriteHeight / 2,
				spriteWidth / 2, spriteHeight / 2);
		
		spriteBatch.draw(textureRegions[1][0], graphicX + spriteWidth / 2, graphicY,
				spriteWidth / 2, spriteHeight / 2);
		
		spriteBatch.draw(textureRegions[1][1], graphicX + spriteWidth / 2, graphicY + spriteHeight / 2,
				spriteWidth / 2, spriteHeight / 2);
	}

	/**
	 * Method which update the internal state every tick
	 */
	// on every tick, update returns true if reached new milestone (x, y)

	
	boolean update(float delta) {
		
		if(!isMoving())
			return false; // no milestone and nothing to update
		
		// update speed & location
		float newDx = dxFromMilestone + (speed.getXv() * delta);
		float newDy = dyFromMilestone + (speed.getYv() * delta);
		
		if (false)
			Gdx.app.debug(TAG, "(" + x + ", " + y + "): new (dx, dy) = (" + newDx + ", " + newDy + ")");
		// milestone reached?
		boolean isMilestone = false;
		
		if (Math.abs(newDx) >= CELL_SIZE) {
			isMilestone = true;
			if (newDx > 0)
				x = x + 1;
			else
				x = x - 1;
		}
		
		if (Math.abs(newDy) >= CELL_SIZE) {
			isMilestone = true;
			if (newDy > 0)
				y = y + 1;
			else
				y = y - 1;
		}	 
		
		if (isMilestone) {
			Gdx.app.debug(TAG, "Milestone reached new: (" + x + "," + y + ")");			
			dxFromMilestone = 0;
			dyFromMilestone = 0;
		} else {
			dxFromMilestone  = newDx;
			dyFromMilestone  = newDy;
		}
		
		return isMilestone;
	}

	/**

	 * Handles the {@link MotionEvent.ACTION_DOWN} event. If the event happens on the 
	 * bitmap surface then the touched state is set to <code>true</code> otherwise to <code>false</code>
	 * @param eventX - the event's X coordinate
	 * @param eventY - the event's Y coordinate
	 */
	public void handleActionDown(int eventX, int eventY) {};
	
	public boolean canMove(int dir) {
		if (isFixed)
			return false;
		if (scanFlag)
			return true;
		scanFlag = true;
		return jelly.canMove(dir);
	}
	
	public boolean getIsFixed() {
		return isFixed;
	}
	
	public void move(int dir) {
		if (scanFlag)
			return;
		scanFlag = true;
		
		if(isFixed) {
			Gdx.app.error(TAG, "Trying to move a fixed cell");
		}
		
		// move current cell
		if (isMoving()) {
			// should be here only if falling > 1 blocks
			Gdx.app.debug(TAG, "Trying to move when already moving - falling >1 blocks?");
		}
		
		float vx = 0, vy = 0;
		switch (dir) {
		case LEFT:
			vx = -SPEED;
			break;
		case RIGHT:		
			vx = SPEED;
			break;
		case DOWN:
			vy = -SPEED;
			break;
		case UP:
			vy = SPEED;
			break;
		default:
			// should never be here
			return;
		}
		speed.set(vx, vy);
		
		// move the whole parent jelly
		jelly.move(dir);
	}

	void stop() {
		speed.setXv(0);
		speed.setYv(0);
	}
	
	void stopHorizontal() {
		speed.setXv(0);
	}

	void stopVertical() {
		speed.setYv(0);
	}
	
	
	boolean isMoving() {
		return (speed.getXv() != 0 || speed.getYv() != 0);
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
