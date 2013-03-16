package com.shval.jnpgame;

import com.shval.jnpgame.BoardConfig;
import static com.shval.jnpgame.Globals.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;

public class Board {
	
	private static final String TAG = Board.class.getSimpleName();
	private final int ROWS, COLS;
	private final int REVERT_DEPTH = 3;
	private Cell cells[][];
	private Cell initialBoard[][];
	private Cell boardStateStack[][][];
	private int boardStateIndex;
	private boolean stable;
	int cellWidth;
	int cellHeight;
	PlayScreen screen;
	
	private Sound[] sounds;
	private float soundVolume; // in [0,1]
	
	// dummy "out of scope" cell, make it a wall
	//static final Cell outOfScopeCell = new Cell(null, 0, 0 ,null , WALL, NONE);
	static final Cell outOfScopeCell = Cell.createCell(-1, -1, null);
	
	private class WinTask extends Timer.Task {
		@Override
		public void run() {
			screen.win();
		}
	}

	private class DelayedSoundPlay extends Timer.Task {
		
		Sound sound;
		
		DelayedSoundPlay(Sound sound) {
			this.sound = sound;
		}
		
		@Override
		public void run() {
			sound.play(soundVolume);
		}
	}
	
	public Board(BoardConfig config, PlayScreen screen) {
		this.ROWS = config.ROWS;
		this.COLS = config.COLS;
		this.screen = screen;
		
		soundVolume = config.getSoundVolume();
		sounds = new Sound[MAX_BOARD_SOUNDS];
		for (int i = 0; i < MAX_BOARD_SOUNDS; i++)
			sounds[i] = config.getSound(i);
		
		
		initialBoard = new Cell[COLS][ROWS];
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				initialBoard[x][y] = Cell.createCell(x, y, config);
			}
		}
		
		cells = new Cell[COLS][ROWS];
		boardStateStack = new Cell[REVERT_DEPTH][COLS][ROWS];
		for (int i = 0; i <REVERT_DEPTH; i++)
			boardStateStack[i] = new Cell[COLS][ROWS];
		
	}

	private void copyBoardState(Cell dst[][], Cell src[][]) {
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				Cell cell = src[x][y];
				if (cell != null) {
					//Gdx.app.debug(TAG, "Coping cell " + x + ", " + y);
					dst[x][y] = new Cell(cell);
				} else {
					dst[x][y] = null;
				}
			}
		}		
	}
	
	private void pushCurrentBoardState() {
		pushBoardState(cells);
	}
	
	private void pushBoardState(Cell[][] state) {
		Gdx.app.debug(TAG, "Pushing: boardStateIndex = " + boardStateIndex);
		if (boardStateIndex == REVERT_DEPTH) {
			// a circular spin
			Cell[][] tmp = boardStateStack[0];
			for (int i = 0; i < REVERT_DEPTH - 1; i++) {
				boardStateStack[i] = boardStateStack[i+1];
			}
			boardStateStack[REVERT_DEPTH - 1] = tmp;
			boardStateIndex = REVERT_DEPTH - 1;
		}
		
		copyBoardState(boardStateStack[boardStateIndex], state);
		boardStateIndex++;
	}
	

	// returns null if stack empty
	private Cell[][] popBoardState() {
		if (boardStateIndex == 0)
			return null;
		boardStateIndex--; 
		return boardStateStack[boardStateIndex];
	}

	public void revert() {
		Gdx.app.debug(TAG, "Reverting: boardStateIndex = " + boardStateIndex);
		Cell[][] state = popBoardState();
		if (state != null) {
			startFrom(state);
		}
	}
	
	public void start() {
		Gdx.app.debug(TAG, "Starting");
		boardStateIndex = 0; // flush state stack
		startFrom(initialBoard);
	}
	
	private void startFrom(Cell[][] state) {
		copyBoardState(cells, state);
		stable = true;
		jellifyBoard();
		attemptMerge();
		setNeighbours();
		updateBoardPhysics();
	}

	public int getRows() {
		return ROWS;
	}

	public int getCols() {
		return COLS;
	}
	
	int getSpriteHeight() {
		return cellHeight;
	}
	
	int getSpriteWidth() {
		return cellWidth;
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
		this.cellWidth = (int) Math.ceil((float)boardWidth / (float)COLS);
		this.cellHeight = (int) Math.ceil((float)boardHeight / (float)ROWS);
		
		// make sprite w/h even
		this.cellWidth += this.cellWidth % 2;
		this.cellHeight += this.cellHeight % 2;
			
		Gdx.app.debug(TAG, "Sprite size = " + cellWidth + " x " + cellHeight);
		Gdx.app.debug(TAG, "Board size = " + boardWidth + " x " + boardHeight);
		
		// set resolution to cells in initialBoard
		// hope that start() will be called after serResolution
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				Cell cell = initialBoard[x][y];
				if (cell != null)
					cell.setResolution(cellWidth, cellHeight);
			}
		}
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
		
		if (ret)
			sounds[SOUND_SLIDE].play(soundVolume / 2); // sliding is quieter
		
		return ret;
	}
	
	void render(SpriteBatch spriteBatch) {		
		
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
		
		return merge;
	}
	
	private void setNeighbours() {
		Gdx.app.debug(TAG, "Setting neighbors");
		for (int x = 0; x < COLS ; x++) {
			for (int y = 0; y < ROWS ; y++) {
				Cell cell = cells[x][y];
				
				// moving cells do not merge
				if (cell == null || cell.isMoving())
					continue;
				
				cell.setNeighbours();
			}
		}
	}
	
	// returns true if moving
	private boolean attemptMove(int dir, Cell cell) {
		//Gdx.app.debug(TAG, "Attempt to move (" + cell.getX() + ", " + cell.getY() + ") in direction: " + dir);
		resetAllScanFlags(); // reset all cells scan flag
		if(cell.canMove(dir)) {
			resetAllScanFlags();
			if (stable) { // just swithched from stable to non stable
				pushCurrentBoardState();
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

		if (stable) {// milestone is stable
			if (attemptMerge()) {
				Timer.schedule(new DelayedSoundPlay(sounds[SOUND_MERGE_START]), 0.2f);
				//Timer.schedule(new DelayedSoundPlay(sounds[SOUND_MERGE_FINISH]), 0.3f);
				setNeighbours();
			}
			if (isWinPosition()) { // check only if something merged
				Gdx.app.debug(TAG, "You win!");
				Timer.schedule(new DelayedSoundPlay(sounds[SOUND_WIN]), 0.7f);
				Timer.schedule(new WinTask(), 1f);
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
				
				if (cell.isMoving()) {
					if (!attemptMove(DOWN, cell)) {
						// cell just hit ground
						cell.stopVertical();
						sounds[SOUND_FALL].play(soundVolume);
					} else {
						allStill = false;
					}
				} else if (attemptMove(DOWN, cell)) {
					// cells begins to fall
					allStill = false;
				}
			}
		}
		stable = allStill;
	}
	
	
}