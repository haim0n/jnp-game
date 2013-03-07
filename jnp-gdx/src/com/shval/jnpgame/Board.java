package com.shval.jnpgame;

import static com.shval.jnpgame.Globals.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Board {
	
	private static final String TAG = Board.class.getSimpleName();
	private final int ROWS, COLS;
	private Cell cells[][];
	private boolean stable;
	int spriteWidth;
	int spriteHeight;
	float scale;
	private Bitmap rawBg;
	private Bitmap bg;
	
	private Cell createCell(BoardConfig config, int x, int y) {
		int type = config.getType(x, y);
		if (type == NONE)
			return null;
		Bitmap bm = config.getBitmap(x, y);
		boolean fixed = config.isFixed(x, y);
		Jelly jelly = null;
		if (type != WALL) {
			jelly = new Jelly(this);
		}
		Cell cell = new Cell(bm, x, y, jelly, fixed, type, scale);
		if(jelly != null)
			jelly.join(cell);
		return cell;
	}
	
	int getSpriteHeight() {
		return spriteHeight;
	}
	
	int getSpriteWidth() {
		return spriteWidth;
	}
	
	private boolean isWinPosition() {
		// win iff all non-black jellies
		Jelly jellies[] = new Jelly[MAX_JELLY_TYPES];

		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				Cell cell = cells[x][y];
				// we check for NONE although it isn't there
				if (cell == null || cell.getType() == NONE)
					continue;
				
				int type = cell.getType();
				// walls and blacks are not in the game
				if (type == WALL || type == JELLY_BLACK)
					continue;

				if (jellies[type] != null) {
					if (jellies[type] != cell.getJelly())
						return false;
				} else {
					jellies[type] = cell.getJelly();
				}
			}
		}
		return true;
	}
	
	public void setResolution(int boardWidth, int boardHeight) {
		// square
		/*
		int spriteSize = Math.min(boardWidth/COLS, boardHeight/ROWS); 
		this.spriteWidth = spriteSize;
		this.spriteHeight = spriteSize;
		*/
		
		// not necessarily square
		this.spriteWidth = boardWidth/COLS;
		this.spriteHeight = boardHeight/ROWS;
		
		Log.d(TAG, "Sprite size = " + spriteWidth + " x " + spriteHeight);
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				Cell cell = cells[x][y];
				if (cell != null)
					cell.setResolution(spriteWidth, spriteHeight);
			}
		}

		//int w = (int) ( (float) 256 * scale + 0.5f);
		//int h = (int) ( (float) 256 * scale + 0.5f);
		bg = Bitmap.createScaledBitmap(rawBg, boardWidth, boardHeight, false);
	}
	
	public Board(BoardConfig config, MainGamePanel panel, Bitmap rawBg) {

		this.ROWS = config.ROWS;
		this.COLS = config.COLS;
		
		this.stable = true;
		scale = panel.getContext().getResources().getDisplayMetrics().density;
		Log.d(TAG, "scale = " + scale);
		
		cells = new Cell[COLS][ROWS];
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				cells[x][y] = createCell(config, x, y);
			}
		}
		
		this.rawBg = rawBg;
	}
	
	public void start() {
		attemptMerge(true); // try mering blacks also 
		updateBoardPhysics();
	}

	public Cell getCell(int x, int y) {
		if (x < 0 || x >= COLS || y < 0 || y >= ROWS) {
			/* error */
			Log.d(TAG, "Out of scope");
			return null;
		}
		return cells[x][y];
	}
	
	public void resetAllScanFlags () {
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				if (cells[x][y] != null)
					cells[x][y].resetScanFlag();
			}
		}
	}
	
	
	// returns true if moving
	boolean attemptSlide(int dir, int x, int y) {
		Cell cell;

		// dont allow slides while not stable
		if (!stable)
			return false;

		if (x < 0 || x >= COLS || y < 0 || y >= ROWS) {
			// error
			Log.d(TAG, "attemptSlide: Out of scope");
			return false;
		}
				
		cell = cells[x][y];
		// if no cell return
		if (cell == null)
			return false;
		
		return attemptMove(dir, cell);
	}
	
	void render(Canvas canvas) {
		canvas.drawBitmap(bg, 0, 0, null);
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				Cell cell = cells[x][y];
				if (cell == null)
					continue;
				cell.render(canvas);
			}
		}	
	}
		
	private boolean attemptMerge(boolean blacksMerge) {
		// blacksMerge enables us to create black jellies
		// larger than one cell.
		// this is useful 'couse we usually need it on start
		
		boolean merge = false; // used to check for win
		for (int x = 0; x < COLS - 1; x++) {
			for (int y = 0; y < ROWS - 1; y++) {
				Cell cell = cells[x][y];
				
				// moving cell do not merge
				if (cell == null || cell.isMoving())
					continue;
				
				// try to merge black jellies only if 
				// called with blacksMerge == true
				if (!blacksMerge && cell.getType() == JELLY_BLACK)
					continue;
					
				Cell neighbor;
				// try to merge with right neighbor
				neighbor = cells[x+1][y];
				if (neighbor != null && cell.getJelly() != neighbor.getJelly()
							&& cell.getType() == neighbor.getType() && !neighbor.isMoving()) {
					neighbor.getJelly().merge(cell.getJelly());
					merge = true;
				}
				
				// try to merge with down neighbor
				neighbor = cells[x][y+1];
				if (neighbor != null && cell.getJelly() != neighbor.getJelly()
							&& cell.getType() == neighbor.getType() && !neighbor.isMoving()) {
					neighbor.getJelly().merge(cell.getJelly());
					merge = true;
				}
			}
		}
		return merge;
	}
	
	// returns true if moving
	private boolean attemptMove(int dir, Cell cell) {
		Log.d(TAG, "Attempt to move (" + cell.getX() + ", " + cell.getY() + ") in direction: " + dir);
		resetAllScanFlags(); // reset all cells scan flag
		if(cell.canMove(dir)) {
			resetAllScanFlags();
			stable = false;
			cell.move(dir);
			return true;
		}
		return false;
	}
	
	// every tick
	void update() {
		boolean isMilestone;
		//Log.d(TAG, "Updating game state");
		
		if (stable)
			return;
		
		isMilestone = false;
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				Cell cell = cells[x][y];
				if (cell == null)
					continue;
				isMilestone |= cell.update();
			}
		}
		
		if(!isMilestone)
			return;

		// new milestone (N.Z) is reached - update board
		Log.d(TAG, "Reached milestone. Rebuilding board");
		Cell cellsOld[][] = cells;
		cells = new Cell[COLS][ROWS];
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				Cell cell = cellsOld[x][y];
				if (cell == null)
					continue;
				cells[cell.getX()][cell.getY()] = cell;
			}
		}
		
		// trigger board dynamics (not to be confused with on tick update) 
		updateBoardPhysics();
		
		if (attemptMerge(false)) { // don't merge blacks
			if (isWinPosition()) { // check only if something merged
				Log.d(TAG, "You win!");
			}
		}
	}	
	
	private void updateBoardPhysics() {
		boolean allStill = true;
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				Cell cell = cells[x][y];
				if (cell == null)
					continue;
				cell.stopHorizontal();
				// can the cell fall
				if (!attemptMove(DOWN, cell))
					cell.stopVertical();
				else
					allStill = false;
					
			}
		}
		stable = allStill;
	}
	
	
}