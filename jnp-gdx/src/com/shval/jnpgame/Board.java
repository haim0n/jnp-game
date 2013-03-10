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
	
	// dummy "out of scope" cell, make it a wall
	static final Cell outOfScopeCell = new Cell(null, 0, 0 ,null , WALL, NONE);
	
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

	public int getRows() {
		return ROWS;
	}

	public int getCols() {
		return COLS;
	}
	
	private Cell createCell(int x, int y) {
		int type = config.getType(x, y);
		int anchoredTo = config.getAncoredTo(x, y);
		if (type == NONE)
			return null;
		Texture texture = config.getTexture(x, y);
		//boolean fixed = config.isFixed(x, y);
		Jelly jelly = null;
		jelly = new Jelly(this);
		
		Cell cell = new Cell(texture, x, y, jelly, type, anchoredTo);
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
		this.spriteWidth = (int) Math.ceil((float)boardWidth / (float)COLS);
		this.spriteHeight = (int) Math.ceil((float)boardHeight / (float)ROWS);
		
		// make sprite w/h even
		this.spriteWidth += this.spriteWidth % 2;
		this.spriteHeight += this.spriteHeight % 2;
			
		
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
			//Gdx.app.debug(TAG, "Out of scope");
			return outOfScopeCell;
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
		
	private boolean isNeighbour(Cell cell1, int dir, Cell cell2) {
		if (cell1 == null || cell2 == null)
			return false;
		
		int dx = 0, dy = 0;
		switch (dir) {
		case LEFT:
			dx = -1;
			break;
		case RIGHT:		
			dx = 1;
			break;
		case DOWN:
			dy = -1;
			break;
		case UP:
			dy = 1;
			break;
		default:
			// should never be here
			Gdx.app.error(TAG, "isNeighbor(...): Invalid direction");
			return false;
		}
		
		return ((cell1.getX() + dx == cell2.getX()) && (cell1.getY() + dy == cell2.getY()) );
	}
	
	private boolean attemptMerge(Cell cell, Cell neighbor) {
		
		if (neighbor == null || neighbor.isMoving())
			return false;
		
		if (cell.getJelly() == neighbor.getJelly())
			return false;
		
		if (cell.getType() == neighbor.getType()) {
			neighbor.getJelly().merge(cell.getJelly());
			return true;
		}
		
		// anchored cells will be merged
		if (isNeighbour(cell, cell.anchoredTo, neighbor) ||
			isNeighbour(neighbor, neighbor.anchoredTo, cell) ) {
				neighbor.getJelly().merge(cell.getJelly());
				return true;				
		}
		
		return false;
	}
	
	private boolean attemptMerge(boolean blacksMerge) {
		// blacksMerge enables us to create black jellies
		// larger than one cell.
		// this is useful 'couse we usually need it on start
		
		boolean merge = false; // indicates wether something merged
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
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
				if (x < COLS - 1)
					neighbor = cells[x+1][y];
				else
					neighbor = null;
				
				merge |= attemptMerge(cell, neighbor);
				
				// try to merge with down neighbor
				if (y < ROWS - 1)
					neighbor = cells[x][y+1];
				else
					neighbor = null;
				
				if (neighbor != null && cell.getJelly() != neighbor.getJelly()
							&& cell.getType() == neighbor.getType() && !neighbor.isMoving()) {
					neighbor.getJelly().merge(cell.getJelly());
					merge = true;
				}
			}
		}
		
		if (merge) {
			for (int x = 0; x < COLS ; x++) {
				for (int y = 0; y < ROWS ; y++) {
					Cell cell = cells[x][y];
					
					// moving cell do not merge
					if (cell == null || cell.isMoving())
						continue;
					
					// try to merge black jellies only if 
					// called with blacksMerge == true
					if (!blacksMerge && cell.getType() == JELLY_BLACK)
						continue;
					
					cell.setNeighbours();
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