package com.shval.jnpgame;

import static com.shval.jnpgame.Globals.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class BoardConfig {

	int ROWS;
	int COLS;
	int LEVELS;
	int level;
	private static String TAG = BoardConfig.class.getSimpleName();
	private char cells[][];
	
	// level 1
	private String levels[][] = { 
			{ // level 0 - dev playground

			"xxxxxxxxxxxxxx",
			"x   G4brg  rRx",
			"xd b gygr    x",
			"xd   0g1yB  Bx",
			"xw   yry22   x",
			"x    rybyB  Yx",
			"x   Y33Rg    x",
			"xxxxxxxxxxxxxx",
			},
			{ // level 1			
			"xxxxxxxxxxxxxx",
//			"x            x",
			"x            x",
			"x            x",
			"x            x",
			"x       r    x",
			"x      xx    x",
			"x  g     r b x",
			"xxbxxxg xxxxxx",
			"xxxxxxxxxxxxxx",
			},
			{ // level 2
			"xxxxxxxxxxxxxx",
			"x            x",
			"x            x", // 			
			"x            x",
			"x            x",
			"x     g   g  x",
			"x   r r   r  x",
			"xxxxx x x xxxx",
			"xxxxxxxxxxxxxx",
			},
			{ // level 3
			"xxxxxxxxxxxxxx",
			"x            x",
			"x            x", //			
			"x            x",
			"x   bg  x g  x",
			"xxx xxxrxxx  x",
			"x      b     x",
			"xxx xxxrxxxxxx",
			"xxxxxxxxxxxxxx",
			},
			{ // level 4
			"xxxxxxxxxxxxxx",
//			"x            x",
			"x       r    x",
			"x       b    x",
			"x       x    x",
			"x b r        x",
			"x b r      b x",
			"xxx x      xxx",
			"xxxxx xxxxxxxx",
			"xxxxxxxxxxxxxx",
			},
			{ // level 5
			"xxxxxxxxxxxxxx",
			"x            x",
			"x            x",
			"xrg  gg      x",
			"xxx xxxx xx  x",
			"xrg          x",
			"xxxxx  xx   xx",
			"xxxxxx xx  xxx",
			"xxxxxxxxxxxxxx",
			},
			{ // level 6
			"xxxxxxxxxxxxxx",
//			"xxxxxxx      x",
			"xxxxxxx g    x",
			"x       xx   x",
			"x r   b      x",
			"x x xxx x g  x",
			"x         x bx",
			"x       r xxxx",
			"x   xxxxxxxxxx",
			"xxxxxxxxxxxxxx",
			}
			};
	
	
	public BoardConfig() {
		LEVELS = levels.length - 1; // zero level doesn't count
		Gdx.app.debug(TAG, "Number of levels is " + LEVELS);
	}
	
	public void setLevel(int level) {
		Gdx.app.debug(TAG, "Attempting to define level " + level);
		if (level > LEVELS || level < 0)
			level = 0; // ha !!! see you pass this one ...
		
		this.level = level;
		String board[] = levels[level];
		ROWS = board.length;
		COLS = board[0].length();
		cells = new char[COLS][ROWS];
		Gdx.app.debug(TAG, "boardsize (" + COLS + "," + ROWS + ")");
		transposeBoard(board);
		Gdx.app.debug(TAG, "Level " + level + " defined");
	}

	int getLevels() {
		return LEVELS;
	}
	
	private void transposeBoard(String board[]) {
		// in libgdx (0,0) is the lower left corner
		// we'll stick to that
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				cells[x][ROWS - 1 - y] = board[y].charAt(x);
			}
		}
	}
	
	Texture getTexture(int x, int y) {
		int type = getType(x, y);
		return Assets.getTexture(type);
	}
	
	Texture getBgTexture() {
		return Assets.getBgTexture(level);
	}
	
	/*
	boolean isFixed(int x, int y) {
		 char cell = cells[x][y];
		 boolean ret;
		 switch(cell) {
		 	case 'r' :
		 	case 'b' :
		 	case 'g' :
		 	case 'y' :
		 	case 'd' :		 		
		 		ret = false;
		 		break;
		 	default:
		 		ret = true;
		 		break;
		 }
		 return ret;
	}	
	*/
	
	int getAncoredTo(int x, int y) {
		 char cell = cells[x][y];
		 		 
		 switch(cell) {
		 	case 'R' :
		 	case 'G' :		 		
		 	case 'B' :		 		
		 	case 'Y' :
		 		break;
		 	default :
		 		return NONE;
		 }
		 
		 // TODO: for now, anchor to blacks, if no blacks around, to walls
		 if (Cell.isBlack(getType(x-1, y)))
			 return LEFT;

		 if (Cell.isBlack(getType(x, y+1)))
			 return UP;

		 if (Cell.isBlack(getType(x+1, y)))
			 return RIGHT;

		 if (Cell.isBlack(getType(x, y-1)))
			 return DOWN;


		 if (Cell.isWall(getType(x-1, y)))
			 return LEFT;

		 if (Cell.isWall(getType(x, y+1)))
			 return UP;

		 if (Cell.isWall(getType(x+1, y)))
			 return RIGHT;

		 if (Cell.isWall(getType(x, y-1)))
			 return DOWN;

		 // should not be here
		 return NONE;
	}
	
	int getBlackType(char blackAscii) {
		return blackAscii + JELLY_BLACK_MIN;
	}
	
	int getType(int x, int y) {
		 char cell = cells[x][y];
		 int ret;
		 switch(cell) {
		 	case 'W' :
		 	case 'w' :		 		
		 	case 'x' :		 		
		 		ret = WALL;
		 		break;
		 	case 'r' :
		 	case 'R' :
		 		ret = JELLY_RED;
		 		break;
		 	case 'b' :
		 	case 'B' :
		 		ret = JELLY_BLUE;
		 		break;
		 	case 'g' :
		 	case 'G' :
		 		ret = JELLY_GREEN;
		 		break;
		 	case 'y' :
		 	case 'Y' :
		 		ret = JELLY_YELLOW;
		 		break;		 		
		 	case 'd' :
		 	case 'D' :		 		
		 	case '0' :
		 	case '1' :
		 	case '2' :
		 	case '3' :
		 	case '4' :
		 	case '5' :
		 	case '6' :
		 	case '7' :
		 	case '8' :
		 	case '9' :
		 		ret = getBlackType(cell);
		 		break;
		 	default :
		 		ret = NONE;
		 		break;
		 }
		 return ret;
	}

	public Texture getResetButtonsTexture() {
		return Assets.getButtonsTexture(level);
	}
	
}
