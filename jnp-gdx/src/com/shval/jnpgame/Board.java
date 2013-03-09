package com.shval.jnpgame;

import com.shval.jnpgame.BoardConfig;
import static com.shval.jnpgame.Globals.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class Board {
	
	private static final String TAG = Board.class.getSimpleName();
	private final int ROWS, COLS;
	private Cell cells[][];
	private boolean stable;
	int spriteWidth;
	int spriteHeight;
	int boardWidth;
	int boardHeight;
	BoardConfig config;
	Sprite bgSprite;
	
	public Board(int level) {
		config = new BoardConfig(level);
		this.ROWS = config.ROWS;
		this.COLS = config.COLS;
		
		this.stable = true;
		
		cells = new Cell[COLS][ROWS];
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				cells[x][y] = createCell(x, y);
			}
		}
		
		Texture texture = config.getBgTexture(level);
		bgSprite = new Sprite(texture);
	}

	private Cell createCell(int x, int y) {
		int type = config.getType(x, y);
		if (type == NONE)
			return null;
		TextureRegion textureR = config.getTextureRegion(x, y);
		boolean fixed = config.isFixed(x, y);
		Jelly jelly = null;
		if (type != WALL) {
			jelly = new Jelly(this);
		}
		Cell cell = new Cell(textureR, x, y, jelly, fixed, type);
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

		// not necessarily square
		// TODO: try using floats
		this.spriteWidth = boardWidth / COLS + 1;
		this.spriteHeight = boardHeight / ROWS + 1;
		
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		
		Gdx.app.debug(TAG, "Sprite size = " + spriteWidth + " x " + spriteHeight);
		Gdx.app.debug(TAG, "Board size = " + boardWidth + " x " + boardHeight);
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				Cell cell = cells[x][y];
				if (cell != null)
					cell.setResolution(spriteWidth, spriteHeight);
			}
		}
	}
		
	public void start() {
		attemptMerge(true); // try merging blacks also 
		updateBoardPhysics();
	}

	public Cell getCell(int x, int y) {
		if (x < 0 || x >= COLS || y < 0 || y >= ROWS) {
			/* error */
			Gdx.app.debug(TAG, "Out of scope");
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
			Gdx.app.debug(TAG, "attemptSlide: Out of scope");
			return false;
		}
				
		cell = cells[x][y];
		// if no cell return
		if (cell == null)
			return false;
		
		return attemptMove(dir, cell);
	}
	
	void render(SpriteBatch spriteBatch) {
		spriteBatch.draw(bgSprite, 0, 0, boardWidth, boardHeight);
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				Cell cell = cells[x][y];
				if (cell == null)
					continue;
				cell.render(spriteBatch);
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
		Gdx.app.debug(TAG, "Attempt to move (" + cell.getX() + ", " + cell.getY() + ") in direction: " + dir);
		resetAllScanFlags(); // reset all cells scan flag
		if(cell.canMove(dir)) {
			resetAllScanFlags();
			stable = false;
			cell.move(dir);
			return true;
		}
		return false;
	}
	
	void update(float delta) {
		
		boolean isMilestone;
		//Gdx.app.debug(TAG, "Updating game state. delta = " + delta);
		
		if (stable)
			return;
		
		isMilestone = false;
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				Cell cell = cells[x][y];
				if (cell == null)
					continue;
				isMilestone |= cell.update(delta);
			}
		}
		
		if(!isMilestone)
			return;

		// new milestone (N.Z) is reached - update board
		Gdx.app.debug(TAG, "Reached milestone. Rebuilding board");
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
				Gdx.app.debug(TAG, "You win!");
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