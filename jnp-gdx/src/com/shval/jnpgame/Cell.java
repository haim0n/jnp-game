package com.shval.jnpgame;

import static com.shval.jnpgame.Globals.*;

public class Cell {

	private static final String TAG = Cell.class.getSimpleName();
	
	private Bitmap bitmap;	// the actual bitmap
	private final Bitmap fullScaleBitmap;
	private int x;			// the X coordinate (in the board cells matrix)
	private int y;			// the Y coordinate 	"  "
	private int dxFromMilestone;
	private int dyFromMilestone;
	private Speed speed;	// the speed with its directions
	private Jelly jelly;
	private boolean isFixed;
	private int type;
	private boolean scanFlag; // was this cell encountered in current board scanning
	private static final int CELL_SIZE = 100; // 
	private static final float SPEED = 55;
	private int spriteWidth = 0;
	private int spriteHeight = 0;
	private static float scale;

	
	private static int scalePixels(int px) {
		return (int) ((float) px * scale + 0.5f);
	}
	
	public Cell(Bitmap bitmap, int x, int y, Jelly jelly,
			boolean fixed, int type, final float scale) {
		
		Cell.scale = scale;
		this.fullScaleBitmap = Bitmap.createBitmap(bitmap, 
				scalePixels(12 + 2 * 48), scalePixels(12 + 2 * 48),
				scalePixels(48), scalePixels(48));
		this.x = x;
		this.y = y;
		this.jelly = jelly;
		this.isFixed = fixed;
		this.type = type;
		speed = new Speed(0, 0);
	}
	
	public void setResolution(int spriteWidth, int spriteHeight) {
		this.spriteWidth = spriteWidth;
		this.spriteHeight = spriteHeight;
		bitmap = Bitmap.createScaledBitmap(fullScaleBitmap, spriteWidth , spriteHeight , false);			
	}
	
	public void resetScanFlag() {
		scanFlag = false;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public Jelly getJelly() {
		return jelly;
	}

	void setJelly(Jelly newJelly) {
		this.jelly = newJelly;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
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
	
	public void render(Canvas canvas) {
		//canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
		int graphicDx, graphicDy;
		if (speed.getXv() != 0)
			graphicDx = (spriteWidth * dxFromMilestone) / CELL_SIZE;
		else
			graphicDx = 0;
		
		if (speed.getYv() != 0)
			graphicDy = (spriteWidth * dyFromMilestone) / CELL_SIZE;
		else
			graphicDy = 0;
		
		int graphicX = spriteWidth * x + graphicDx;
		int graphicY = spriteHeight * y + graphicDy;
		canvas.drawBitmap(bitmap, graphicX, graphicY, null);
	}

	/**
	 * Method which update the internal state every tick
	 */
	// on every tick, update returns true if reached new milestone (x, y)

	
	boolean update() {
		
		if(!isMoving())
			return false; // no milestone and nothing to update
		
		// update speed & location
		int newDx = dxFromMilestone + (int) speed.getXv();
		int newDy = dyFromMilestone + (int) speed.getYv();
		
		if (true)
			Log.d(TAG, "new (dx, dy) = (" + newDx + ", " + newDy + ")");
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
			Log.d(TAG, "Milestone reached new: (" + x + "," + y + ")");			
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
			Log.e(TAG, "Trying tomove a fixed cell");
		}
		
		// move current cell
		if (isMoving()) {
			// should be here only if falling > 1 blocks
			Log.d(TAG, "Trying to move when already moving - falling >1 blocks?");
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
			vy = SPEED;
			break;
		case UP:
			vy = -SPEED;
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
