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
	private final int REVERT_DEPTH = 3;
	private Cell cells[][];
	private Cell boardStateStack[][][];
	private int boardStateIndex;
	private boolean stable;
	int spriteWidth;
	int spriteHeight;
	int boardWidth;
	int boardHeight;
	BoardConfig config;
	Sprite bgSprite; // TODO: can it be just a texure?
	TextureRegion resetButtonTextureR;
	PlayScreen screen;
	
	// dummy "out of scope" cell, make it a wall
	//static final Cell outOfScopeCell = new Cell(null, 0, 0 ,null , WALL, NONE);
	static final Cell outOfScopeCell = Cell.createCell(-1, -1, null);
	
	public Board(int level, PlayScreen screen) {
		config = new BoardConfig(level);
		this.ROWS = config.ROWS;
		this.COLS = config.COLS;
		this.stable = true;
		this.screen = screen;
		
		cells = new Cell[COLS][ROWS];
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				cells[x][y] = Cell.createCell(x, y, config);
			}
		}
		
		boardStateStack = new Cell[REVERT_DEPTH][COLS][ROWS]; 
		
		Texture texture = config.getBgTexture(level);
		bgSprite = new Sprite(texture);
		Texture resetButtonsTexture = config.getResetButtonsTexture(level);
		resetButtonTextureR = new TextureRegion(resetButtonsTexture, 0, 0, 256, 128);
		jellifyBoard();
		
	}

	private void pushBoardState() {
		Gdx.app.debug(TAG, "Pushing: boardStateIndex = " + boardStateIndex);
		if (boardStateIndex == REVERT_DEPTH) {
			for (int i = 0; i < REVERT_DEPTH - 1; i++) {
				boardStateStack[i] = boardStateStack[i+1];
			}
			boardStateIndex--;
		}
		
		Cell stackedCells[][] = new Cell[COLS][ROWS];
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				Cell cell = cells[x][y];
				if (cell != null)
					stackedCells[x][y] = new Cell(cells[x][y]);
			}
		}
		boardStateStack[boardStateIndex] = stackedCells;
		boardStateIndex++;
	}
	

	// returns false if stack empty
	private boolean popBoardState() {
		if (boardStateIndex == 0)
			return false;
		boardStateIndex--;
		cells = boardStateStack[boardStateIndex];
		return true;
	}


	public void revert() {
		Gdx.app.debug(TAG, "Reverting: boardStateIndex = " + boardStateIndex);
		if (popBoardState()) {
			stable =  true;
			jellifyBoard();
			attemptMerge();
			updateBoardPhysics();
		}
	}
	
	public int getRows() {
		return ROWS;
	}

	public int getCols() {
		return COLS;
	}
	
	int getSpriteHeight() {
		return spriteHeight;
	}
	
	int getSpriteWidth() {
		return spriteWidth;
	}
	
	// create jelly for each cell and try merging them
	public void jellifyBoard()
	{
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				Cell cell = cells[x][y];
				if (cell == null || cell.getType() == NONE)
					continue;
				Jelly jelly = new Jelly(this);
				cell.setJelly(jelly);
				jelly.join(cell);
			}
		}
		attemptMerge();
	}
	
	private boolean isWinPosition() {
		// win iff all non-black jellies
		Jelly jellies[] = new Jelly[MAX_COLORED_JELLY_TYPES];

		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				Cell cell = cells[x][y];
				// we check for NONE although it isn't there
				if (cell == null || cell.getType() == NONE)
					continue;
				
				int type = cell.getType();
				// walls and blacks are not in the game
				if (type == WALL || Cell.isBlack(type))
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
		attemptMerge(); 
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
		
		boolean ret = attemptMove(dir, cell);
		return ret;
	}
	
	void render(SpriteBatch spriteBatch) {		
		spriteBatch.draw(bgSprite, 0, 0, boardWidth, boardHeight);
		
		// render cells
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				Cell cell = cells[x][y];
				if (cell == null)
					continue;
				cell.render(spriteBatch, 1);
			}
		}
		
		// render anchors
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				Cell cell = cells[x][y];
				if (cell == null)
					continue;
				cell.render(spriteBatch, 2);
			}
		}	
		
		// buttons
		
		spriteBatch.draw(resetButtonTextureR, (COLS - 4) * spriteWidth, 0 * spriteHeight,
				3 * spriteWidth, spriteHeight * 6 / 8);
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
		case NONE:
			return false;
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
	
	private boolean attemptMerge() {
		boolean merge = false; // indicates whether something merged
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				Cell cell = cells[x][y];
				
				// moving cell do not merge
				if (cell == null || cell.isMoving())
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
				
				merge |= attemptMerge(cell, neighbor);
			}
		}
		
		if (merge) {
			Gdx.app.debug(TAG, "Setting neighbors");
			for (int x = 0; x < COLS ; x++) {
				for (int y = 0; y < ROWS ; y++) {
					Cell cell = cells[x][y];
					
					// moving cell do not merge
					if (cell == null || cell.isMoving())
						continue;
					
					cell.setNeighbours();
				}
			}
		}
		return merge;
	}
	
	// returns true if moving
	private boolean attemptMove(int dir, Cell cell) {
		//Gdx.app.debug(TAG, "Attempt to move (" + cell.getX() + ", " + cell.getY() + ") in direction: " + dir);
		resetAllScanFlags(); // reset all cells scan flag
		if(cell.canMove(dir)) {
			resetAllScanFlags();
			if (stable) { // just swithched from stable to non stable
				pushBoardState();
				stable = false;
			}
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
		
		if (attemptMerge()) {
			if (isWinPosition()) { // check only if something merged
				Gdx.app.debug(TAG, "You win!");
				screen.win();
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