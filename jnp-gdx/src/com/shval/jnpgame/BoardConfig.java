package com.shval.jnpgame;

import static com.shval.jnpgame.Globals.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BoardConfig {

	int ROWS;
	int COLS;
	private static String TAG = BoardConfig.class.getName();
	private char cells[][];
	
	// level 1
	private String levels[][] = { 
			{ // level 0 - dev playground
			"xxxxxxxxxxxxxx",
			"x    dbrg  rRx",
			"xd b gygr    x",
			"xd   dgdy   Bx",
			"xw   yrydd   x",
			"x    ryby   Yx",
			"x    bdrg    x",
			"xxxxxxxxxxxxxx"
			},
			{ // level 1			
			"xxxxxxxxxxxxxx",
			"x            x",
			"x            x",
			"x            x",
			"x            x",
			"x       r    x",
			"x      xx    x",
			"x  g     r b x",
			"xxbxxxg xxxxxx",
			"xxxxxxxxxxxxxx"
			},
			{ // level 2
			"xxxxxxxxxxxxxx",
			"x            x",
			"x            x",
			"x            x",
			"x     g   g  x",
			"x   r r   r  x",
			"xxxxx x x xxxx",
			"xxxxxxxxxxxxxx"
			},
			};
	
	
	public void setLevel(int i) {
		String board[] = levels[i];
		ROWS = board.length;
		COLS = board[0].length();
		cells = new char[COLS][ROWS];
		Gdx.app.debug(TAG, "boardsize (" + COLS + "," + ROWS + ")");
		transposeBoard(board);
		Gdx.app.debug(TAG, "Level " + i + " defined");
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
	
	public BoardConfig(int level) {
		setLevel(level);
	}
	
	Texture getTexture(int x, int y) {
		int type = getType(x, y);
		return Assets.getTexture(type);
	}
	
	Texture getBgTexture(int level) {
		return Assets.getBgTexture(level);
	}
	
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
		 		ret = JELLY_BLACK;
		 		break;
		 	default :
		 		ret = NONE;
		 		break;
		 }
		 return ret;
	}
	
}
